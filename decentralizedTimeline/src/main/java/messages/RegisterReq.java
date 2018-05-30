package messages;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.Serializer;

public class RegisterReq extends AbstractMessage {
    private String requestedUsername;

    public RegisterReq() {
    }

    public RegisterReq(String requestedUsername){
        super();
        this.requestedUsername = requestedUsername;
    }

    public String getRequestedUsername() {
        return requestedUsername;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        super.writeObject(bufferOutput, serializer);
        bufferOutput.writeString(requestedUsername);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        super.readObject(bufferInput, serializer);
        this.requestedUsername = bufferInput.readString();
    }
}
