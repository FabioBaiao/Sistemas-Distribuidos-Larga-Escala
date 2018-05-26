package connection;

import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;

import java.util.*;


public class NodeConnection {

    private static final List<Connection> connections = new ArrayList<>();
    private static final int MAX_CONN = 10;
    private static final Map<String, BestConnection> routing = new HashMap<>();

    private static final ThreadContext tc = new SingleThreadContext("node-%d", new Serializer());
    private static final Transport t = new NettyTransport();


    public static void connectToNetwork(List<Address> supernodes, Address myAddress) {

        int randomIndex = new Random().nextInt(supernodes.size());
        Address supernode = supernodes.get(randomIndex);

        Connection c = tc.execute(() -> t.client().connect(supernode)).join().join();
        connections.add(c);
        c.onClose((closedConn) -> connections.remove(closedConn));

        c.send(new ConnectReq(myAddress));

        accept(myAddress);
    }

    private static void accept(Address myAddress) {

        tc.execute(() -> t.server().listen(myAddress, c -> {

            c.handler(ConnectRep.class, rep -> {

                if (connections.size() * 2 < MAX_CONN) {
                    connections.add(c);

                    if (routing.size() == 0) {
                        // lista de usernames nao recebida

                        for (String username : rep.usernames) {
                            routing.put(username, null);
                        }
                    }

                    // associar username a conexao
                    routing.put(rep.username, new BestConnection(c, 1));
                }
                else {
                    c.close();
                }
            });

            c.onClose((closedConn) -> {

                connections.remove(closedConn);

                // remover todas as associacoes entre usernames com a conexao fechada
                for (String username : routing.keySet()) {

                    if (closedConn.equals(routing.get(username))) {
                        routing.put(username, null);
                    }
                }
            });
        }));
    }

    // invocada depois de estar registado/autenticado
    // autenticacao consiste em indicar aos vizinhos o seu username
    // registo consiste em indicar aos vizinhos o username pretendido e receber resposta
    private static void connectToNode(Connection c, String myUsername) {

        c.handler(ConnectReq.class, req -> {

            if (connections.size() < MAX_CONN) {

                Connection newConn = t.client().connect(req.address).join();

                connections.add(newConn);

                newConn.onClose((closedConn) -> {
                    connections.remove(closedConn);
                });

                newConn.send(new ConnectRep(myUsername, routing.keySet()));
            }
            else {

                Connection neighbour = c;
                // escolher vizinho sem ser o vizinho de quem recebeu
                while (c.equals(neighbour)) {
                    int randomIndex = new Random().nextInt(connections.size());
                    neighbour = connections.get(randomIndex);
                }
                neighbour.send(req);
            }
        });
    }
}
