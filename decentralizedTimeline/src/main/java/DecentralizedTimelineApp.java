import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class DecentralizedTimelineApp implements Runnable {
    
    private static final String HELP =
        "register                     Register in the app\n" +
        "list                         List all users\n" +
        "sub username                 Subscribe to the specified user\n" +
        "pub                          Publish a message\n" +
        "get username [n_messages]    Get all messages from the specified user or its lastest n_messages\n" +
        "help                         Print this help\n" +
        "exit                         Exit the app (Ctrl + D also works)";

    private static final String PROMPT = "\r>>> ";

    private boolean exit;
    private final BufferedReader in;
    private final Map<String, CheckedIOFunction<String[], Integer>> cmdMap; // see CheckedIOFunction.java

    // TODO: Add peer instance variable
    public DecentralizedTimelineApp() {
        exit = false;
        in = new BufferedReader(new InputStreamReader(System.in));
        cmdMap = new HashMap<>();
        cmdMap.put("register", this::register);
        cmdMap.put("login", this::login);
        cmdMap.put("list", this::listUsers);
        cmdMap.put("sub", this::subscribe);
        cmdMap.put("pub", this::publish);
        cmdMap.put("get", this::getMessages);
        cmdMap.put("help", this::printHelp);
        cmdMap.put("exit", this::exit);
    }

    public static void main(String[] args) {
        new DecentralizedTimelineApp().run();
    }

    @Override
    public void run() {
        // TODO: Check for configuration files
        try {
            while (exit == false) {
                System.out.print(PROMPT); // TODO: Add username to prompt
                
                String line = in.readLine();
                String[] argv = (line == null) ? new String[] {"exit"} : line.split(" ");
                CheckedIOFunction<String[], Integer> cmd = cmdMap.get(argv[0]);

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

    // Commands
    public Integer register(String[] argv) throws IOException {
        // TODO: Check if client has already registered
        if (argv.length != 1) {
            System.out.println("Usage: register");
            return -1;
        }        
        System.out.print("Username: ");
        String username = in.readLine();

        System.out.println("Username '" + username + "' successfully read");
        System.out.println("TODO: Register user");

        return 0;
    }

    public Integer login(String[] argv) throws IOException {
        if (argv.length != 2) {
            System.out.println("Usage: login username");
            return -1;
        }
        System.out.println("TODO: Call login on peer");

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

    public Integer subscribe(String[] argv) throws IOException {
        if (argv.length != 2) {
            System.out.println("Usage: sub username");
            return -1;
        }
        String username = argv[1];
        System.out.println("TODO: Subscribe to '" + username + "'");

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

    public Integer getMessages(String[] argv) throws IOException {
        try {
            String username = null; // avoid "variable might not have been initialized"
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

    public Integer printHelp(String[] argv) throws IOException {
        System.out.println(HELP);
        return 0;
    }

    public Integer exit(String[] argv) throws IOException {
        this.exit = true;
        return 0;
    }
}
