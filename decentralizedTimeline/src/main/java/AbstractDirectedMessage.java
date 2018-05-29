import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.Serializer;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDirectedMessage extends AbstractMessage {
    private String sourceUsername;
    private List<String> destinationUsernames;

    public AbstractDirectedMessage() {
    }

    public AbstractDirectedMessage(String sourceUsername, List<String> destinationUsernames) {
        this.sourceUsername = sourceUsername;
        this.destinationUsernames = new ArrayList<>(destinationUsernames);
    }

    public String getSourceUsername() {
        return sourceUsername;
    }

    public List<String> getDestinationUsernames() { return destinationUsernames; }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        super.writeObject(bufferOutput, serializer);

        bufferOutput.writeString(sourceUsername);
        bufferOutput.writeInt(destinationUsernames.size());
        for (String s : destinationUsernames) {
            bufferOutput.writeString(s);
        }
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        super.readObject(bufferInput, serializer);

        this.sourceUsername = bufferInput.readString();

        final int size = bufferInput.readInt();
        this.destinationUsernames = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.destinationUsernames.add(bufferInput.readString());
        }
    }
}
