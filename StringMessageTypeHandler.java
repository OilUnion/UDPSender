import java.util.List;

public class StringMessageTypeHandler implements IMessageTypeHandler {
    public final Object message;

    public StringMessageTypeHandler(Object message) {
        this.message = message;
    }

    @Override
    public List<byte[]> getMessageSegments() {
        byte[] messageAsByteArray =  new MessageTypeConverter(this.message).toByteArray();
        MessageSplitter messageSplitter = new MessageSplitter(messageAsByteArray);
        return messageSplitter.split();
    }
}