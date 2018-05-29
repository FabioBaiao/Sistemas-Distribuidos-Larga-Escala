import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.Serializer;

public class SubscriptionDirectedMessage extends AbstractDirectedMessage {
    public SubscriptionDirectedMessage() {
    }

    public SubscriptionDirectedMessage(String senderUsername, String destinationUsername) {
        super(senderUsername, destinationUsername);
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        super.writeObject(bufferOutput, serializer);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        super.readObject(bufferInput, serializer);
    }
}
