import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class DecentralizedTimelineApp implements Runnable {

    private static final String BASE_PROMPT = "$ ";

    private static final String DISCONNECTED_PRE_LOGIN_HELP =
        "login username               Log-in with the specified username\n" +
        "connect                      Go online\n" +
        "help                         Print this help\n" +
        "exit                         Exit the app (Ctrl + D also works)";

    private static final String DISCONNECTED_POST_LOGIN_HELP =
        "pub                          Publish a message\n" +
        "connect                      Go online\n" +
        "logout                       Log-out of the app\n" +
        "help                         Print this help\n" +
        "exit                         Exit the app (Ctrl + D also works)";

    private static final String CONNECTED_PRE_LOGIN_HELP =
        "register                     Register in the app\n" +
        "login username               Log-in with the specified username\n" +
        "disconnect                   Go offline\n" +
        "help                         Print this help\n" +
        "exit                         Exit the app (Ctrl +' D also works)";

    private static final String CONNECTED_POST_LOGIN_HELP =
        "list                         List all users\n" +
        "sub username                 Subscribe to the specified user\n" +
        "pub                          Publish a message\n" +
        "get username [n_messages]    Get all messages from the specified user or its latest n_messages\n" +
        "logout                       Log-out of the app\n" +
        "help                         Print this help\n" +
        "exit                         Exit the app (Ctrl + D also works)";

    private String prompt;

    /** Input reader */
    private final BufferedReader in;

    /** Active command map */
    private Map<String, CheckedIOFunction<String[], Integer>> cmds;

    /** true if we should exit the app; false otherwise */
    private boolean exit;

    /** MapDB database */
    private final DB db;

    /** Map of registered users */
    private final ConcurrentMap users;

    /** Commands for each application state */
    private final Map<String, CheckedIOFunction<String[], Integer>> disconnectedPreLoginCmds;
    private final Map<String, CheckedIOFunction<String[], Integer>> disconnectedPostLoginCmds;
    private final Map<String, CheckedIOFunction<String[], Integer>> connectedPreLoginCmds;
    private final Map<String, CheckedIOFunction<String[], Integer>> connectedPostLoginCmds;

    // TODO: Add peer instance variable

    public DecentralizedTimelineApp() {
        prompt = BASE_PROMPT;
        in = new BufferedReader(new InputStreamReader(System.in));
        exit = false;
        db = DBMaker.fileDB("decentralized-timeline.db").closeOnJvmShutdown().fileMmapEnable().make();
        users = db.hashMap("userMap", Serializer.STRING, User.SERIALIZER).createOrOpen();

        // Disconnected pre log-in commands
        disconnectedPreLoginCmds = new HashMap<>();
        disconnectedPreLoginCmds.put("login", this::login);
        disconnectedPreLoginCmds.put("connect", this::connect);
        disconnectedPreLoginCmds.put("help", this::helpDisconnectedPreLogin);
        disconnectedPreLoginCmds.put("exit", this::exit);

        // Disconnected logged in
        disconnectedPostLoginCmds = new HashMap<>();
        disconnectedPostLoginCmds.put("pub", this::publish);
        disconnectedPostLoginCmds.put("connect", this::connect);
        disconnectedPostLoginCmds.put("help", this::helpDisconnectedPostLogin);
        disconnectedPostLoginCmds.put("exit", this::exit);

        // Connected pre-login commands
        connectedPreLoginCmds = new HashMap<>();
        connectedPreLoginCmds.put("register", this::register);
        connectedPreLoginCmds.put("login", this::login);
        connectedPreLoginCmds.put("disconnect", this::disconnect);
        connectedPreLoginCmds.put("help", this::helpConnectedPreLogin);
        connectedPreLoginCmds.put("exit", this::exit);

        // Connected post log-in commands
        connectedPostLoginCmds = new HashMap<>();
        connectedPostLoginCmds.put("list", this::listUsers);
        connectedPostLoginCmds.put("sub", this::subscribe);
        connectedPostLoginCmds.put("pub", this::publish);
        connectedPostLoginCmds.put("get", this::getMessages);
        connectedPostLoginCmds.put("logout", this::logout);
        connectedPostLoginCmds.put("help", this::helpConectedPostLogin);
        connectedPostLoginCmds.put("exit", this::exit);

        // Set commands to disconnected pre log-in
        cmds = disconnectedPreLoginCmds;
    }

    public static void main(String[] args) {
        new DecentralizedTimelineApp().run();
    }

    @Override
    public void run() {
        try {
            while (exit == false) {
                System.out.print(prompt);
                
                String line = in.readLine();
                String[] argv = (line == null) ? new String[] {"exit"} : line.split(" ");
                CheckedIOFunction<String[], Integer> cmd = cmds.get(argv[0]);

                if (cmd == null) {
                    System.out.println("'" + line + "' is not a valid command. Try 'help'");
                } else {
                    cmd.apply(argv);
                } 
            }
        } catch (IOException ex) {
            System.out.println("Exiting because of error '" + ex.getMessage() + "'");
        }
    }

    /* Commands */

    public Integer register(String[] argv) throws IOException {
        // TODO: Check if client has already registered
        if (argv.length != 1) {
            System.out.println("Usage: register");
            return -1;
        }        
        System.out.print("Username: ");
        String username = in.readLine();

        System.out.println("Username '" + username + "' successfully read");
        System.out.println("TODO: registration user");

        return 0;
    }

    public Integer login(String[] argv) throws IOException {
        if (argv.length != 2) {
            System.out.println("Usage: login username");
            return -1;
        }
        System.out.println("TODO: Call login on peer");
        this.prompt = argv[1] + this.prompt;

        // TODO: Refactor this if ... else
        if (this.cmds == disconnectedPreLoginCmds) {
            this.cmds = disconnectedPostLoginCmds;
        } else {
            this.cmds = connectedPostLoginCmds;
        }
        return 0;
    }

    public Integer logout(String[] argv) throws IOException {
        if (argv.length != 1) {
            System.out.println("Usage: logout");
            return -1;
        }
        System.out.println("TODO: Remove user from peer");
        this.prompt = BASE_PROMPT;
        this.cmds = connectedPreLoginCmds;

        return 0;
    }

    public Integer connect(String[] argv) throws IOException {
        if (argv.length != 1) {
            System.out.println("Usage: connect");
            return -1;
        }
        System.out.println("TODO: Connect to the supernodes");

        // TODO: Refactor this if ... else
        if (this.cmds == disconnectedPreLoginCmds) {
            this.cmds = connectedPreLoginCmds;
        } else { // disconnectedPostLoginCmds
            this.cmds = connectedPostLoginCmds;
        }

        return 0;
    }

    public Integer disconnect(String[] argv) throws IOException {
        if (argv.length != 1) {
            System.out.println("Usage: disconnect");
            return -1;
        }
        System.out.println("TODO: Disconnect from supernodes");

        // TODO: Refactor this if ... else
        if (this.cmds == connectedPreLoginCmds) {
            this.cmds = disconnectedPreLoginCmds;
        } else { // connectedPostLoginCmds
            this.cmds = disconnectedPostLoginCmds;
        }
        return 0;
    }

    public Integer listUsers(String[] argv) throws IOException {
        if (argv.length != 1) {
            System.out.println("Usage: list");
            return -1;
        }
        System.out.println("TODO: Get users list from neighbors");

        return 0;
    }

    public Integer publish(String[] argv) throws IOException {
        if (argv.length != 1) {
            System.out.println("Usage: publish");
            return -1;
        }
        System.out.print("Enter your message: ");
        String message = in.readLine();

        System.out.println("Read message '" + message + "'");
        System.out.println("TODO: Publish message and store it on disk");

        return 0;
    }

    public Integer subscribe(String[] argv) throws IOException {
        if (argv.length != 2) {
            System.out.println("Usage: sub username");
            return -1;
        }
        String username = argv[1];
        System.out.println("TODO: Subscribe to '" + username + "'");

        return 0;
    }

    public Integer getMessages(String[] argv) throws IOException {
        try {
            String username;
            int nMessages = Integer.MAX_VALUE; // all messages

            if (argv.length == 2) {
                username = argv[1];
            } else if (argv.length == 3) {
                username = argv[1];
                nMessages = Integer.valueOf(argv[2]);
            } else {
                System.out.println("Usage: get username [n_messages]");
                return -1;
            }
            String nMessagesStr = (nMessages == Integer.MAX_VALUE) ? "all" : (nMessages + " latest");
            System.out.println("TODO: Get " + nMessagesStr + " messages from '" + username + "'");

            return 0;
        } catch (NumberFormatException ex) {
            System.out.println("Error: '" + argv[2] + "' is not an integer");
            return -1;
        }
    }

    public Integer helpDisconnectedPreLogin(String[] argv) {
        System.out.println(DISCONNECTED_PRE_LOGIN_HELP);
        return 0;
    }

    public Integer helpDisconnectedPostLogin(String[] argv) {
        System.out.println(DISCONNECTED_POST_LOGIN_HELP);
        return 0;
    }

    public Integer helpConnectedPreLogin(String[] argv) {
        System.out.println(CONNECTED_PRE_LOGIN_HELP);
        return 0;
    }

    public Integer helpConectedPostLogin(String[] argv) throws IOException {
        System.out.println(CONNECTED_POST_LOGIN_HELP);
        return 0;
    }

    public Integer exit(String[] argv) throws IOException {
        this.exit = true;
        return 0;
    }
}
