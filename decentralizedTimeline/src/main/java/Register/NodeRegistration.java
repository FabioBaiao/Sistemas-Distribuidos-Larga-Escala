package Register;

import connection.BestConnection;
import io.atomix.catalyst.transport.Connection;

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


    public void register(CompletableFuture<RegisterRep.Reply> reply, Connection connection) {
        connection.handler(RegisterRep.class, rep -> {
            reply.complete(rep.getReply());
        });

        connection.send(new RegisterReq(username));
    }

    public void registerRequestHandler(Connection connection){
        connection.handler(RegisterReq.class, req -> {
            String username = req.getRequestedUsername();
            int hops = req.getNumHops();
            RegisterRep.Reply reply = null;

            if (routing.containsKey(username))
                reply = RegisterRep.Reply.USERNAME_IN_USE;
            else{
                reply = RegisterRep.Reply.SUCCESSFUL;
                RegisterReq request = new RegisterReq(username);
                request.setNumHops(hops+1);
                for(Connection c: neighborConnections)
                    c.send(request);
            }
            if(hops == 1) {
                RegisterRep rep = new RegisterRep(reply);
                connection.send(rep);
            }
        });
    }


}
