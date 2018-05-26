package connection;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

import java.util.HashSet;
import java.util.Set;

public class ConnectRep implements CatalystSerializable{

    public String username;
    public Set<String> usernames;

    public ConnectRep() {}

    public ConnectRep(String username, Set<String> usernames) {
        this.username = username;
        this.usernames = usernames;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeString(username);
        bufferOutput.writeInt(usernames.size());
        for (String username : usernames) {
            bufferOutput.writeString(username);
        }
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        username = bufferInput.readString();
        int size = bufferInput.readInt();
        usernames = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            usernames.add(bufferInput.readString());
        }
    }
}
