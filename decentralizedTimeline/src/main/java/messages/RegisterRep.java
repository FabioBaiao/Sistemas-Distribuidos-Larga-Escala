package messages;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import sun.jvm.hotspot.asm.Register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegisterRep extends AbstractMessage {
    private RegisterRep.Status status;

    public RegisterRep() {}

    public RegisterRep(RegisterRep.Status status) {
        super();
        this.status = status;
    }

    public RegisterRep.Status getStatus() { return status; }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        super.writeObject(bufferOutput, serializer);

        bufferOutput.writeInt(status.getValue());
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        super.readObject(bufferInput, serializer);

        this.status = Status.fromInteger(bufferInput.readInt());
    }

    public enum Status {
        SUCCESS(0), USERNAME_IN_USE(1);

        private final int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Status fromInteger(int i) {
            switch (i) {
                case 0:
                    return SUCCESS;
                case 1:
                    return USERNAME_IN_USE;
            }
            return null;
        }
    }
}
