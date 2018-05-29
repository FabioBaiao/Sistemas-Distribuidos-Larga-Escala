package messages;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public abstract class AbstractMessage implements CatalystSerializable  {
    private int numHops;

    public AbstractMessage() {
        this.numHops = 1;
    }

    public int getNumHops() {
        return numHops;
    }

    public void incrementNumHops() {
        ++numHops;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(this.numHops);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.numHops = bufferInput.readInt();
    }
}
