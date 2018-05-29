import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegisterRep extends AbstractMessage {
    private List<Address> livePeers;

    public RegisterRep() {}

    public RegisterRep(List<Address> livePeers) {
        super();
        this.livePeers = Collections.unmodifiableList(new ArrayList<>(livePeers));
    }

    public List<Address> getLivePeers() {
        return livePeers;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        super.writeObject(bufferOutput, serializer);

        bufferOutput.writeInt(livePeers.size());
        for(Address a: livePeers){
            bufferOutput.writeString(a.host());
            bufferOutput.writeInt(a.port());
        }
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        super.readObject(bufferInput, serializer);

        final int size = bufferInput.readInt();
        this.livePeers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.livePeers.add(new Address(bufferInput.readString(), bufferInput.readInt()));
        }
        this.livePeers = Collections.unmodifiableList(this.livePeers);
    }
}
