import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public abstract class AbstractMessage implements CatalystSerializable {
    private int numHops;
    private String sourceUsername;
    private String destinationUsername;

    public AbstractMessage() {
    }

    public AbstractMessage(String sourceUsername, String destinationUsername) {
        this.numHops = 0;
        this.sourceUsername = sourceUsername;
        this.destinationUsername = destinationUsername;
    }

    public int getNumHops() {
        return numHops;
    }

    public String getSourceUsername() {
        return sourceUsername;
    }

    public String getDestinationUsername() {
        return destinationUsername;
    }

    public void incrementNumHops() {
        ++numHops;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(numHops);
        bufferOutput.writeString(sourceUsername);
        bufferOutput.writeString(destinationUsername);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.numHops = bufferInput.readInt();
        this.sourceUsername = bufferInput.readString();
        this.destinationUsername = bufferInput.readString();
    }
}
