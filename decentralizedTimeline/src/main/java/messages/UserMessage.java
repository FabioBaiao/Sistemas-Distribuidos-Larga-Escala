package messages;

import converters.LocalDateTimeConverter;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.Serializer;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserMessage extends AbstractDirectedMessage implements Comparable<UserMessage>, Serializable {
    public static final SerializerUserMessage SERIALIZER = new SerializerUserMessage();

    private String text;
    private LocalDateTime dateTime;

    public UserMessage(String senderUsername, String text, LocalDateTime dateTime) {
        this(senderUsername, new ArrayList<>(), text, dateTime);
    }

    public UserMessage(String senderUsername, List<String> destinationUsernames, String text, LocalDateTime dateTime) {
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
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof UserMessage))
            return false;

        UserMessage that = (UserMessage) o;

        return Objects.equals(text, that.text) && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, dateTime);
    }

    @Override
    public int compareTo(UserMessage other) {
        int dateComparison = this.dateTime.compareTo(other.dateTime);

        return (dateComparison != 0) ? dateComparison : this.text.compareTo(other.text);
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        super.writeObject(bufferOutput, serializer);

        bufferOutput.writeString(text);
        bufferOutput.writeLong(LocalDateTimeConverter.convertToTimestamp(dateTime));
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        super.readObject(bufferInput, serializer);

        this.text = bufferInput.readString();
        this.dateTime = LocalDateTimeConverter.convertFromTimestamp(bufferInput.readLong());
    }

    public static class SerializerUserMessage implements org.mapdb.Serializer<UserMessage> {
        private SerializerUserMessage() {}

        @Override
        public void serialize(@NotNull DataOutput2 dataOutput2, @NotNull UserMessage userMessage) throws IOException {
            dataOutput2.writeUTF(userMessage.getSourceUsername());
            dataOutput2.writeUTF(userMessage.text);
            dataOutput2.writeLong(LocalDateTimeConverter.convertToTimestamp(userMessage.dateTime));
        }

        @Override
        public UserMessage deserialize(@NotNull DataInput2 dataInput2, int nBytes) throws IOException {
            String senderUsername = dataInput2.readUTF();
            String text = dataInput2.readUTF();
            LocalDateTime dateTime = LocalDateTimeConverter.convertFromTimestamp(dataInput2.readLong());

            return new UserMessage(senderUsername, text, dateTime);
        }
    }
}
