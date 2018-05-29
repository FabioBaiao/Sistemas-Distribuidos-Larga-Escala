import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class User {
    public static final User.SerializerUser SERIALIZER = new SerializerUser();

    private final String username;
    private final List<UserMessage> sentMessages;
    private final List<UserMessage> unsentMessages;
    private final List<String> subscribers;
    private final List<String> subscribed;

    public User(String username) {
        this.username = username;
        this.sentMessages = new ArrayList<>();
        this.unsentMessages = new ArrayList<>();
        this.subscribers = new ArrayList<>();
        this.subscribed = new ArrayList<>();
    }

    public static class SerializerUser implements Serializer<User> {

        @Override
        public void serialize(@NotNull DataOutput2 dataOutput2, @NotNull User user) throws IOException {
            dataOutput2.writeUTF(user.username);

            // sentMessages
            dataOutput2.writeInt(user.sentMessages.size());
            for (UserMessage m : user.sentMessages) {
                UserMessage.SERIALIZER.serialize(dataOutput2, m);
            }

            // unsentMessages
            dataOutput2.writeInt(user.unsentMessages.size());
            for (UserMessage m : user.unsentMessages) {
                UserMessage.SERIALIZER.serialize(dataOutput2, m);
            }

            // subscribers
            dataOutput2.writeInt(user.subscribers.size());
            for (String s : user.subscribers) {
                dataOutput2.writeUTF(s);
            }

            // subscribed
            dataOutput2.writeInt(user.subscribed.size());
            for (String s : user.subscribed) {
                dataOutput2.writeUTF(s);
            }
        }

        @Override
        public User deserialize(@NotNull DataInput2 dataInput2, int nBytes) throws IOException {
            int size; // for list sizes
            String username = dataInput2.readUTF();

            // sentMessages
            size = dataInput2.readInt();
            List<UserMessage> sentMessages = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                sentMessages.add(UserMessage.SERIALIZER.deserialize(dataInput2))
            }

        }
    }
}
