package messages;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.Serializer;

import java.util.*;

public class AuthenticateRep extends AbstractDirectedMessage {

    public Map<String, Set<UserMessage>> subscriptionsMessages;

    public AuthenticateRep() {}

    public AuthenticateRep(String sourceUsername, List<String> destinationUsernames, Map<String, Set<UserMessage>> subscriptionsMessages) {
        super(sourceUsername, destinationUsernames);
        this.subscriptionsMessages = subscriptionsMessages;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        super.writeObject(bufferOutput, serializer);

        bufferOutput.writeInt(subscriptionsMessages.size());
        for (String username : subscriptionsMessages.keySet()) {
            bufferOutput.writeString(username);
            Set<UserMessage> msgs = subscriptionsMessages.get(username);
            bufferOutput.writeInt(msgs.size());
            for (UserMessage msg : msgs) {
                serializer.writeObject(msg, bufferOutput);
            }
        }
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        super.readObject(bufferInput, serializer);

        int size = bufferInput.readInt();
        subscriptionsMessages = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            String username = bufferInput.readString();

            int listSize = bufferInput.readInt();
            Set<UserMessage> msgs = new HashSet<>(listSize);
            for (int j = 0; j < listSize; j++) {
                msgs.add(serializer.readObject(bufferInput));
            }
            subscriptionsMessages.put(username, msgs);
        }
    }
}
