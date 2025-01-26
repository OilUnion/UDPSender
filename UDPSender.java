import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class UDPSender implements ISendable {
    private final String address;
    private final int port;
    private final List<byte[]> messagePackets;

    public UDPSender(String address, int port, List<byte[]> messagePackets) {
        this.address = address;
        this.port = port;
        this.messagePackets = messagePackets;
    }

    public void send() throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(this.address);
            for (byte[] segment : this.messagePackets) {
                socket.send(new DatagramPacket(segment, segment.length, address, this.port));
            }
        } catch (IOException e) {
            throw new IOException(STR."Ошибка отправки сообщения: \{e.getMessage()}");
        }
    }
}
