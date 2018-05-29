import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class UserDirectedMessage extends AbstractDirectedMessage {

    private String text;
    private LocalDateTime dateTime;

    public UserDirectedMessage(String senderUsername, String text, LocalDateTime dateTime) {
        this(senderUsername, new ArrayList<>(), text, dateTime);
    }

    public UserDirectedMessage(String senderUsername, List<String> destinationUsernames, String text, LocalDateTime dateTime) {
        super(senderUsername, destinationUsernames);
        this.text = text;
        this.dateTime = dateTime;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public void writeObject()

    public static class UserMessageSerializer implements Serializer<UserDirectedMessage> {
        private UserMessageSerializer() {}

        @Override
        public void serialize(@NotNull DataOutput2 dataOutput2, @NotNull UserDirectedMessage userMessage) throws IOException {
            dataOutput2.writeUTF(userMessage.text);

            Instant instant = userMessage.dateTime.atZone(ZoneId.systemDefault()).toInstant();
            Date date = Date.from(instant);
            dataOutput2.writeLong(date.getTime());
        }

        @Override
        public UserDirectedMessage deserialize(@NotNull DataInput2 dataInput2, int nBytes) throws IOException {
            if (nBytes <= 0)
                return null;

            String senderUsername = dataInput2.readUTF();
            String text = dataInput2.readUTF();
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(dataInput2.readLong()), TimeZone.getDefault().toZoneId()
            );

            return new UserDirectedMessage(senderUsername, text, dateTime);
        }
    }
}
