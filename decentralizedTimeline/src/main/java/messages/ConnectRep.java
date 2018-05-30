package messages;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.Serializer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ConnectRep extends AbstractDirectedMessage {

    public Set<String> usernames;

    public ConnectRep() {}

    public ConnectRep(String sourceUsername, Set<String> usernames) {
        super(sourceUsername, new ArrayList<>());
        this.usernames = usernames;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        super.writeObject(bufferOutput, serializer);

        bufferOutput.writeInt(usernames.size());
        for (String username : usernames) {
            bufferOutput.writeString(username);
        }
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        super.readObject(bufferInput, serializer);

        int size = bufferInput.readInt();
        usernames = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            usernames.add(bufferInput.readString());
        }
    }
}
