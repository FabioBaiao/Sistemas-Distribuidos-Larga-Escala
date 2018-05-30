package authentication;

import routing.BestConnection;
import io.atomix.catalyst.transport.Connection;
import messages.*;

import java.util.*;

public class NodeAuthentication {

    private static final List<Connection> connections = new ArrayList<>();
    private static final Map<String, BestConnection> routing = new HashMap<>();

    private static boolean authenticated = false;
    private static String myUsername;

    private static final List<String> subscriptions = new ArrayList<>();

    public static void authenticate(String myUsername) {

        NodeAuthentication.authenticated = true;
        NodeAuthentication.myUsername = myUsername;

        Map<Connection, List<String>> destinations = mapDestinations(connections, routing, subscriptions);

        for (Connection c : destinations.keySet()) {
            c.send(new AuthenticateReq(myUsername, destinations.get(c)));
        }
    }

    // mapeia para cada conexao os destinos de acordo com a tabela de encaminhamento
    private static Map<Connection, List<String>> mapDestinations
            (List<Connection> connections, Map<String, BestConnection> routing, List<String> destinations) {

        Map<Connection, List<String>> routedDestinations = new HashMap<>();
        List<String> broadcastDestinations = new ArrayList<>();

        for (Connection c : connections) {
            routedDestinations.put(c, new ArrayList<>());
        }

        for (String username : destinations) {
            BestConnection bc = routing.get(username);
            if (bc != null) {
                Connection c = bc.connection;
                routedDestinations.get(c).add(username);
            }
            else {
                broadcastDestinations.add(username);
            }
        }

        for (Connection c : routedDestinations.keySet()) {
            routedDestinations.get(c).addAll(broadcastDestinations);
        }

        return routedDestinations;
    }

    private static final Set<UserMessage> myMessages = new HashSet<>();
    private static final Map<String, Set<UserMessage>> subscriptionsMessages = new HashMap<>();

    // invocada depois de estar registado/autenticado
    public static void recvAuthentication(Connection c) {

        c.handler(AuthenticateReq.class, req -> {

            if (myUsername.equals(req.getSourceUsername())) {
                return;
            }

            updateRouting(req, c);


            Iterator<String> it = req.getDestinationUsernames().iterator();

            Map<String, Set<UserMessage>> messagesToSend = new HashMap<>();

            while (it.hasNext()) {
                String username = it.next();
                if (myUsername.equals(username)) {
                    it.remove();
                    messagesToSend.put(username, myMessages);
                }
                else if (subscriptions.contains(username)) {
                    it.remove();
                    messagesToSend.put(username, subscriptionsMessages.get(username));
                }
            }

            List<String> destinationList = new ArrayList<>();
            destinationList.add(req.getSourceUsername());

            c.send(new AuthenticateRep(myUsername, destinationList, messagesToSend));


            List<Connection> remainingConnections = new ArrayList<>(connections);
            remainingConnections.remove(c);

            Map<Connection, List<String>> destinations = mapDestinations(remainingConnections, routing, req.getDestinationUsernames());

            for (Connection connection : destinations.keySet()) {
                req.setDestinationUsernames(destinations.get(connection));
                connection.send(req);
            }

        });

        c.handler(AuthenticateRep.class, rep -> {

            if (myUsername.equals(rep.getSourceUsername())) {
                return;
            }

            updateRouting(rep, c);

            // apenas um destino
            String destination = rep.getDestinationUsernames().get(0);

            if (myUsername.equals(destination)) {

                for (String username : rep.subscriptionsMessages.keySet()) {
                    Set<UserMessage> msgs = subscriptionsMessages.get(username);
                    if (msgs == null) {
                        msgs = new TreeSet<>();
                        subscriptionsMessages.put(username, msgs);
                    }
                    msgs.addAll(rep.subscriptionsMessages.get(username));

                }
            }
            else {

                BestConnection bc = routing.get(destination);
                if (bc != null) {
                    Connection connection = bc.connection;
                    connection.send(rep);
                }
                else {
                    List<Connection> remainingConnections = new ArrayList<>(connections);
                    remainingConnections.remove(c);

                    for (Connection connection : remainingConnections) {
                        connection.send(rep);
                    }
                }
            }
        });
    }

    public static void updateRouting(AbstractDirectedMessage msg, Connection c) {
        BestConnection bc = routing.get(msg.getSourceUsername());
        if (bc == null || msg.getNumHops() <= bc.numHops) {
            routing.put(msg.getSourceUsername(), new BestConnection(c, msg.getNumHops()));
        }
    }
}
