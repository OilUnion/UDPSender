import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

public class MainClass {
    public void main(String[] args) throws IOException {
        String address = "127.0.0.1";
        int port = 12345;
        byte[] data = new byte[15];
        UniqueldentifierGenerator guid = new UniqueldentifierGenerator();
        DataHeader dataHeader = new DataHeader("|",guid.getGUID(), "file", "fe");
        ISplitable a = new DefaultMessageSplitter(data,63_000);
        List<byte[]> b = this.addHeadersToPackets(dataHeader,a.split());
        ISendable udpSender = new UDPSender(address, port, b);
        udpSender.send();
    }

    // Добавление заголовков к каждому пакету.
    private List<byte[]> addHeadersToPackets(DataHeader dataHeader,List<byte[]> packets) {
        List<byte[]> packetsWithHeaders = new ArrayList<>();
        for (int i = 0; i < packets.size(); i++) {
            long checksum = calculateCRC32(packets.get(i));  // Вычисление CRC32 для каждого пакета.
            dataHeader.addTempItems(dataHeader.toString()
                                    , String.valueOf(i + 1)
                                    , String.valueOf(packets.size())
                                    , String.valueOf(checksum));  // Формируем строку заголовка.
            String headerStr = dataHeader.toString();
            dataHeader.clearTempItems();
            byte[] fullPacket = prependHeaderToPacket(headerStr, packets.get(i));  // Добавляем заголовок к пакету.
            packetsWithHeaders.add(fullPacket);
        }
        return packetsWithHeaders;
    }

    // Вычисление CRC32 для пакета.
    private long calculateCRC32(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return crc32.getValue();
    }

    // Добавление заголовка к пакету.
    private byte[] prependHeaderToPacket(String headerStr, byte[] data) {
        byte[] headerBytes = headerStr.getBytes();
        byte[] fullPacket = new byte[headerBytes.length + data.length];
        System.arraycopy(headerBytes, 0, fullPacket, 0, headerBytes.length);
        System.arraycopy(data, 0, fullPacket, headerBytes.length, data.length);
        return fullPacket;
    }
}
