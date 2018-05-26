import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;
import io.atomix.catalyst.transport.Server;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Peer {
    private String username;
    private Address addr;
    private Map<String, Address> peers;
    private List<String> users;
    private Transport transport;
    private ThreadContext tc;
    private Server server;
    private List<Connection> neighborConns;

    public Peer(String username, Address address){
       this.username = username;
        this.addr = address;
       this.peers = new HashMap<String, Address>();
       this.users = new ArrayList<String>();

       this.transport = new NettyTransport();
       this.tc = new SingleThreadContext("proto-%d", new Serializer());

       this.server = this.transport.server();
    }

    public void listen(Consumer<Connection> connHandler){
        tc.execute(() -> {
            this.server.listen(this.addr, connHandler);
        });
    }

    public void bootstrap(Address addr) throws Exception{
        Connection c = tc.execute(() -> this.transport.client().connect(addr)).join().get();
        RegisterRep regRep =
                (RegisterRep) this.tc.execute(() ->
                c.sendAndReceive(new RegisterReq(this.username))).join().get();

        connectToNeigbors(regRep.getLivePeers());
    }

    public void connectToNeigbors(List<Address> regRep) throws Exception{
        for(Address a: regRep){
            Connection c = tc.execute(() -> transport.client().connect(a)).join().get();
            /*TODO: test if successful*/
            this.neighborConns.add(c);
        }
    }

    /* The client functionalities like Register and Subscribe
       would be methods that would talk to the peers neighbors 
     */

}
