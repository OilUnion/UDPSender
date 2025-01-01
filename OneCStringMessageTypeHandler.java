import java.util.List;

public class OneCStringMessageTypeHandler implements IMessageTypeHandler {
    public final Object message;


    public OneCStringMessageTypeHandler (Object message, String additionalDataAsString) {
        this.message = message;
    }
    @Override
    public List<byte[]> getMessageSegments() {
        byte[] messageAsByteArray =  new MessageTypeConverter(this.message).toByteArray();
        DefaultMessageSplitter defaultMessageSplitter = new DefaultMessageSplitter(messageAsByteArray);
        return defaultMessageSplitter.split();
    }
}
