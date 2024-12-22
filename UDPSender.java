import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class UDPSender {
    private final String address;
    private final int port;
    private final Object message;
    private String flag = null;
    private int datagramSize = 65_000;
    private String additionalData = null;

    private IMessageTypeConverter messageTypeConverter;
    private List<byte[]> messageSegments;

    public UDPSender (String address, int port, Object message) {
        this.address = address;
        this.port = port;
        this.message = message;
    }

    public UDPSender (String address, int port, Object message, int datagramSize) {
        this.address = address;
        this.port = port;
        this.message = message;
        this.datagramSize = datagramSize;
    }

    public UDPSender (String address, int port, Object message, String flag) {
        this.address = address;
        this.port = port;
        this.message = message;
        this.flag = flag;
    }

    public UDPSender (String address, int port, Object message, int datagramSize, String flag) {
        this.address = address;
        this.port = port;
        this.message = message;
        this.datagramSize = datagramSize;
        this.flag = flag;
    }

    public UDPSender (String address, int port, Object message, int datagramSize, String flag, String additionalData) {
        this.address = address;
        this.port = port;
        this.message = message;
        this.datagramSize = datagramSize;
        this.flag = flag;
        this.additionalData = additionalData;
    }

    private void IdentifyType (Object message, String flag) throws Exception {
        if (message instanceof String && flag == null) {
            this.messageTypeConverter = new StringMessageTypeConverter((String) message);
        } else if (message instanceof String && flag.equals("1C")) {
            this.messageTypeConverter = new OneCStringMessageTypeConverter((String) message);
        }
        else {
            throw new Exception("Тип неопределен.");
        }
    }

    private void executeChainOfResponsibilitiees() {
       byte[] messageAsByteArray =  this.messageTypeConverter.toByteArray();
       MessageSplitter messageSplitter = new MessageSplitter(messageAsByteArray);
       this.messageSegments = messageSplitter.split();
    }

    public void send () throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(this.address);
            for (byte[] segment : this.messageSegments) {
                socket.send(new DatagramPacket(segment, segment.length, address, this.port));
            }
        } catch (IOException e) {
            throw new IOException("Error sending packets: " + e.getMessage());
        }
    }
}
