import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {
    public static final User.SerializerUser SERIALIZER = new SerializerUser();

    private final String username;
    private final List<UserMessage> sentMessages;
    private final List<UserMessage> unsentMessages;
    private final Set<String> subscribers;
    private final Set<String> subscribed;

    public User(String username) {
        this(username, new ArrayList<>(), new ArrayList<>(), new HashSet<>(), new HashSet<>());
    }

    private User(String username, List<UserMessage> sentMessages, List<UserMessage> unsentMessages,
                 Set<String> subscribers, Set<String> subscribed)
    {
        this.username = username;
        this.sentMessages = sentMessages;
        this.unsentMessages = unsentMessages;
        this.subscribers = subscribers;
        this.subscribed = subscribed;
    }

    public Set<UserMessage> getUnsentMessages() {
        return new HashSet<>(unsentMessages);
    }

    public void markAllAsSent() {
        sentMessages.addAll(unsentMessages);
        unsentMessages.clear();
    }

    public void addUnsentMessage(UserMessage message) {
        unsentMessages.add(message);
    }

    public boolean addSubscriber(String subscriber) {
        return subscribers.add(subscriber);
    }

    public boolean removeSubscriber(String username) {
        return subscribers.remove(username);
    }

    public boolean addSubscribed(String username) {
        return subscribed.add(username);
    }

    public boolean removeSubscribed(String username) {
        return subscribed.remove(username);
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
                sentMessages.add(UserMessage.SERIALIZER.deserialize(dataInput2, -1)); // TODO: Check if this works
            }

            // unsentMessages
            size = dataInput2.readInt();
            List<UserMessage> unsentMessages = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                unsentMessages.add(UserMessage.SERIALIZER.deserialize(dataInput2, -1));
            }

            // subscribers
            size = dataInput2.readInt();
            Set<String> subscribers = new HashSet<>((int) (size / .75f) + 1);
            for (int i = 0; i < size; i++) {
                subscribers.add(dataInput2.readUTF());
            }

            // subscribed
            size = dataInput2.readInt();
            Set<String> subscribed = new HashSet<>((int) (size / .75f) + 1);
            for (int i = 0; i < size; i++) {
                subscribed.add(dataInput2.readUTF());
            }

            return new User(username, sentMessages, unsentMessages, subscribers, subscribed);
        }
    }
}
