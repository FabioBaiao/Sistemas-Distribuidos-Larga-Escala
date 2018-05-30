package connection;

import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import pt.haslab.ekit.Clique;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SupernodeConnection {

    private static final List<Connection> connections = new ArrayList<>();
    private static final int MAX_CONN = 20;
    //private static final Map<String, connection.BestConnection> routing = new HashMap<>();
    //private static final Map<String, connection.BestSupernode> supernodeRouting = new HashMap<>();

    private static final ThreadContext tc = new SingleThreadContext("node-%d", new Serializer());
    private static final Transport t = new NettyTransport();

    private static final int myId = 0;
    private static final Clique clique = new Clique(t, myId);

    public static void accept(int supernodes) {
        int port = 0;
        Address myPublicAddress = new Address("", port);

        tc.execute(() -> t.server().listen(myPublicAddress, c -> {

            c.handler(ConnectReq.class, req -> {

                if (connections.size() < MAX_CONN) {
                    connections.add(c);
                }
                else {
                    c.close();
                }

                // enviar pedido para supernodos, inclusive ele proprio
                for (int i = 0; i < supernodes; i++) {
                    clique.send(i, req);
                }
            });

            c.onClose((closedConn) -> {
                connections.remove(closedConn);
            });

        }));
    }


    public static void chooseNeighbour() {

        // escolher vizinho para responder a pedido
        clique.handler(ConnectReq.class, (i, req) -> {
            int randomIndex = new Random().nextInt(connections.size());
            connections.get(randomIndex).send(req);
        });
    }
}
