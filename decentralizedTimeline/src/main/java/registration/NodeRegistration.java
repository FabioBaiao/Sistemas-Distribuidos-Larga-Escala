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
        neighborConnections = new ArrayList<>();
        routing = new HashMap<>();
    }


    public void register(CompletableFuture<RegisterRep.Status> reply, Connection connection) {
        connection.handler(RegisterRep.class, rep -> {
            reply.complete(rep.getStatus());
        });

        connection.send(new RegisterReq(username));
    }

    public void registerRequestHandler(Connection connection){
        connection.handler(RegisterReq.class, req -> {
            String username = req.getRequestedUsername();
            int hops = req.getNumHops();
            RegisterRep.Status replyStatus = null;

            if (routing.containsKey(username)) {
                replyStatus = RegisterRep.Status.USERNAME_IN_USE;
            } else {
                replyStatus = RegisterRep.Status.SUCCESS;
                req.incrementNumHops();

                for(Connection c : neighborConnections)
                    c.send(req);
            }
            // N
            if(hops == 1) {
                RegisterRep rep = new RegisterRep(replyStatus);
                connection.send(rep);
            }
        });
    }


}
