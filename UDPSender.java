import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPSender {
    private final String address;
    private final int port;
    private final Object message;
    private String flag = null;
    private int datagramSize = 65_000;
    private String additionalDataAsString = null;

    private IMessageTypeHandler messageTypeHandler;
    private List<byte[]> messageSegments = new ArrayList<>();

    public UDPSender (String address, int port, Object message) throws Exception {
        this.address = address;
        this.port = port;
        this.message = message;
        this.messageTypeHandler = identifyType();
    }

    public UDPSender (String address, int port, Object message, int datagramSize) throws Exception {
        this.address = address;
        this.port = port;
        this.message = message;
        this.datagramSize = datagramSize;
        this.messageTypeHandler = identifyType();
    }

    public UDPSender (String address, int port, Object message, String flag) throws Exception {
        this.address = address;
        this.port = port;
        this.message = message;
        this.flag = flag;
        this.messageTypeHandler = identifyType();
    }

    public UDPSender (String address, int port, Object message, int datagramSize, String flag) throws Exception {
        this.address = address;
        this.port = port;
        this.message = message;
        this.datagramSize = datagramSize;
        this.flag = flag;
        this.messageTypeHandler = identifyType();
    }

    public UDPSender (String address, int port, Object message, int datagramSize, String flag, String additionalDataAsString) throws Exception {
        this.address = address;
        this.port = port;
        this.message = message;
        this.datagramSize = datagramSize;
        this.flag = flag;
        this.additionalDataAsString = additionalDataAsString;
        this.messageTypeHandler = identifyType();
    }

    private IMessageTypeHandler identifyType () throws Exception {
        if (this.message instanceof String && this.flag == null) {
            return new StringMessageTypeHandler(this.message);
        } else if (this.message instanceof String && this.flag.equals("1C")) {
            return new OneCStringMessageTypeHandler(this.message, this.additionalDataAsString);
        }
        else {
            throw new Exception("Тип неопределен.");
        }
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
