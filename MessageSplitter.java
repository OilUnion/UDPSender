import java.util.ArrayList;
import java.util.List;

public class MessageSplitter {
    private final byte[] messageAsByteArray;

    public MessageSplitter (byte[] messageAsByteArray) {
       this.messageAsByteArray = messageAsByteArray; 
    }

    public List<byte[]> split() {
        return new ArrayList<>();
    }
}
