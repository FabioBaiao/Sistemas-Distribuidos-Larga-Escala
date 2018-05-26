package connection;

import io.atomix.catalyst.transport.Connection;

public class BestConnection {

    public Connection c;
    public int hops;

    BestConnection(Connection c, int hops) {
        this.c = c;
        this.hops = hops;
    }
}
