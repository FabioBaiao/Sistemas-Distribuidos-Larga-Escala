import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;

import java.util.ArrayList;
import java.util.List;

public class RegisterRep implements CatalystSerializable {
    public List<Address> livePeers;

    public RegisterRep(){}

    public RegisterRep(List<Address> livePeers){
        this.livePeers = livePeers;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(livePeers.size());
        for(Address a: livePeers){
            bufferOutput.writeString(a.host());
            bufferOutput.writeInt(a.port());
        }
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        int i = bufferInput.readInt();
        int j = 0;
        while(j < i) {
            this.livePeers.add(new Address(bufferInput.readString(), bufferInput.readInt()));
            j++;
        }
    }
}
