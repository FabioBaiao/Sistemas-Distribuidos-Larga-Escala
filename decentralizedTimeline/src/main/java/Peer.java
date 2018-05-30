import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Connection;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;

import java.util.ArrayList;
import java.util.List;

// TODO: Refactor this class!
public class Peer {
    private User user;
    private Address addr;
    private Transport transport;
    private ThreadContext tc;
    private List<Connection> neighborConns;
    //  private Server server;
    //  private Map<String, Address> peers;
    //  private List<String> users;

    public Peer(Address address) {
        this.addr = address;
        this.transport = new NettyTransport();
        this.tc = new SingleThreadContext("proto-%d", new Serializer());
        this.neighborConns = new ArrayList<>();
        // this.server = this.transport.server();
        // this.peers = new HashMap<String, Address>();
        // this.users = new ArrayList<String>();
    }

    /*
    public void listen(Consumer<Connection> connHandler) {
        tc.execute(() -> {
            this.server.listen(this.addr, connHandler);
        });
    }
*/

    public void setUser(User user) {
        this.user = user;
    }

    /*
    public void bootstrap(Address addr) throws Exception {
        Connection c = tc.execute(() -> this.transport.client().connect(addr)).join().get();
        messages.RegisterRep regRep = (messages.RegisterRep) this.tc.execute(() ->
                c.sendAndReceive(new messages.RegisterReq())
        ).join().get();

        connectToNeigbors(regRep.getLivePeers());
    }
    */

    public void connectToNeighbors(List<Address> regRep) throws Exception {
        for (Address a : regRep) {
            Connection c = tc.execute(() -> transport.client().connect(a)).join().get();
            /*TODO: test if successful*/
            this.neighborConns.add(c);
        }
    }

    /* The client functionalities like Register and Subscribe
       would be methods that would talk to the peers neighbors 
     */
}
