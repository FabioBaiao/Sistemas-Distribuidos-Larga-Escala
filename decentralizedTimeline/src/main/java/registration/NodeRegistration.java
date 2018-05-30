package registration;

import connection.BestConnection;
import io.atomix.catalyst.transport.Connection;
import messages.RegisterRep;
import messages.RegisterReq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NodeRegistration {
    private String username;
    private List<Connection> neighborConnections;
    private Map<String, BestConnection> routing;

    public NodeRegistration(String username){
        this.username = username;
        this.neighborConnections = new ArrayList<>();
        this.routing = new HashMap<>();
    }

    /** Must be called inside a SingleThreadContext */
    public CompletableFuture<RegisterRep.Status> register(Connection connection) {
        CompletableFuture<RegisterRep.Status> futureReplyStatus = new CompletableFuture<>();

        connection.handler(RegisterRep.class, rep -> {
            futureReplyStatus.complete(rep.getStatus());
        });

        connection.send(new RegisterReq(username));
        return futureReplyStatus;
    }

    /** Must be called inside a SingleThreadContext */
    public void registerRequestHandler(Connection connection) {
        connection.handler(RegisterReq.class, req -> {
            String username = req.getRequestedUsername();
            int numHops = req.getNumHops();
            RegisterRep.Status replyStatus = null;

            if (routing.containsKey(username)) {
                replyStatus = RegisterRep.Status.USERNAME_IN_USE;
            } else { // broadcast to neighbors
                replyStatus = RegisterRep.Status.SUCCESS;
                req.incrementNumHops();

                for(Connection c : neighborConnections) {
                    c.send(req);
                }
            }

            if (numHops == 1) {
                RegisterRep rep = new RegisterRep(replyStatus);
                connection.send(rep);
            }
        });
    }
}
