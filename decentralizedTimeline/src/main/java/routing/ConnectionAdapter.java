package routing;

import io.atomix.catalyst.transport.Connection;

import java.util.List;

public class ConnectionAdapter extends Path {

    public Connection connection;

    public ConnectionAdapter(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void send(List<String> strings) {

    }
}
