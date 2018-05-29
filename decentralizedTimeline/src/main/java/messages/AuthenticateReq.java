package messages;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import messages.AbstractDirectedMessage;

import java.util.ArrayList;
import java.util.List;

public class AuthenticateReq extends AbstractDirectedMessage {

    public AuthenticateReq() {}

    public AuthenticateReq(String sourceUsername, List<String> destinationUsernames) {
        super(sourceUsername, destinationUsernames);
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
