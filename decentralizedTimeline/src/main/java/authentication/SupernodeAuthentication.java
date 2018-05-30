package authentication;

import io.atomix.catalyst.transport.Connection;
import messages.AuthenticateReq;
import routing.BestPath;
import routing.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupernodeAuthentication {

    private static final Map<String, BestPath> routing = new HashMap<>();
    private static final List<Path> paths = new ArrayList<>();

    public static void recvAuthentication(Connection c) {

        c.handler(AuthenticateReq.class, req -> {
            //updateRouting(req, c);

            List<Path> remainingPaths = new ArrayList<>(paths);
            remainingPaths.remove(c);

            Map<Path, List<String>> destinations = mapDestinations(remainingPaths, routing, req.getDestinationUsernames());

            for (Path path : destinations.keySet()) {
                path.send(destinations.get(path));
            }
        });
    }
}
