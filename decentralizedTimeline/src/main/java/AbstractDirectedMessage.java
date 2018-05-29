import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMessage implements CatalystSerializable {
    private int numHops;
    private String sourceUsername;
    private List<String> destinationUsernames;

    public AbstractMessage() {
    }

    public AbstractMessage(String sourceUsername, List<String> destinationUsernames) {
        this.numHops = 0;
        this.sourceUsername = sourceUsername;
        this.destinationUsernames = new ArrayList<>(destinationUsernames);
    }

    public int getNumHops() {
        return numHops;
    }

    public String getSourceUsername() {
        return sourceUsername;
    }

    public List<String> getDestinationUsername() { return destinationUsernames; }

    public void incrementNumHops() {
        ++numHops;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(numHops);
        bufferOutput.writeString(sourceUsername);

        final int size = destinationUsernames.size();
        bufferOutput.writeInt(size);

        for (String s : destinationUsernames) {
            bufferOutput.writeString(s);
        }
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.numHops = bufferInput.readInt();
        this.sourceUsername = bufferInput.readString();

        final int size = bufferInput.readInt();
        this.destinationUsernames = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.destinationUsernames.add(bufferInput.readString());
        }
    }
}
