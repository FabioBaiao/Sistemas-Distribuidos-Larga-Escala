package routing;

import io.atomix.catalyst.transport.Connection;

public class BestConnection extends BestPath {

    public Connection connection;

    public BestConnection(Connection connection, int numHops) {
        super(numHops);
        this.connection = connection;
    }
}
