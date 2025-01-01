
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.zip.CRC32;

//Основной класс
public class UIntegrationSendViaUDP {

    // 65507 - Максимальный размер пакета.
    // Адрес сервера.
    private final String HOST = "127.0.0.1";
    // Порт сервера.
    private final int PORT = 12345;

    public void main(String[] args) throws Exception {
        UDPClient udpClient = new UDPClient(this.HOST, this.PORT, new ArrayList<byte[]>(List.of("fe".getBytes())));
        udpClient.send();
        final String orderId = "Test";
        final String message = "test";
        final String type = "Test";
        Sender Sender = new Sender(this.HOST, this.PORT);
        byte[] messageData = message.getBytes();
        String guid = new UniqueldentifierGenerator().getGUID();
        DataHeader header = new DataHeader(guid, type, orderId);
        DatagramPacketsCreator datagramPacketsCreator = new DatagramPacketsCreator(messageData, header);
        List<byte[]> packets = datagramPacketsCreator.createDataPackets();
        Sender.sendPackets(packets);
    }
}

// Служит для разбиения данных на массив.
class DatagramPacketsCreator {
    // Ограничение передачи по UDP протоколу 65 507 байт
    public static final int MAX_PACKET_SIZE = 65_000;

    private final DataHeader dataHeader;
    private final byte[] messageData;

    DatagramPacketsCreator(byte[] messageData, DataHeader dataHeader) {
        this.messageData = messageData;
        this.dataHeader = dataHeader;
    }

    // Создание пакетов из данных.
    public List<byte[]> createDataPackets() {
        int segmentCount = (int) Math.ceil(((double) this.messageData.length / MAX_PACKET_SIZE) + 0.1);
        List<byte[]> segments = this.splitDataIntoSegments(this.messageData, segmentCount);
        List<byte[]> segmentsWithHeader = this.addHeadersToPackets(segments);
        return segmentsWithHeader;
    }

    // Разбиение данных на сегменты.
    private List<byte[]> splitDataIntoSegments(byte[] data, int segmentCount) {
        List<byte[]> segments = new ArrayList<>();
        for (int i = 0; i < segmentCount; i++) {
            int start = i * (data.length / segmentCount);
            int end = (i == segmentCount - 1) ? data.length : (i + 1) * (data.length / segmentCount);
            segments.add(Arrays.copyOfRange(data, start, end));
        }
        return segments;
    }

    // Добавление заголовков к каждому пакету.
    private List<byte[]> addHeadersToPackets(List<byte[]> packets) {
        List<byte[]> packetsWithHeaders = new ArrayList<>();
        for (int i = 0; i < packets.size(); i++) {
            long checksum = calculateCRC32(packets.get(i));  // Вычисление CRC32 для каждого пакета
            String headerStr = createHeaderString(this.dataHeader, i + 1, packets.size(), checksum);  // Формируем строку заголовка
            byte[] fullPacket = prependHeaderToPacket(headerStr, packets.get(i));  // Добавляем заголовок к пакету
            packetsWithHeaders.add(fullPacket);
        }
        return packetsWithHeaders;
    }

    // Формирование строки заголовка для пакета.
    private String createHeaderString(DataHeader header, int segment, int totalSegments, long checksum) {
        // Строка заголовка: GUID|orderId|номер пакета|общее количество пакетов|CRC32.
        return String.join("|"
                , header.toString()
                , String.valueOf(segment)
                , String.valueOf(totalSegments)
                , String.valueOf(checksum)) + "|";
    }

    // Добавление заголовка к пакету.
    private byte[] prependHeaderToPacket(String headerStr, byte[] data) {
        byte[] headerBytes = headerStr.getBytes();
        byte[] fullPacket = new byte[headerBytes.length + data.length];
        System.arraycopy(headerBytes, 0, fullPacket, 0, headerBytes.length);
        System.arraycopy(data, 0, fullPacket, headerBytes.length, data.length);
        return fullPacket;
    }

    // Вычисление CRC32 для пакета.
    private long calculateCRC32(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return crc32.getValue();
    }
}

// Класс для хранения заголовка данных.
class DataHeader {
    private final String guid;
    // Параметр необходим для правильного декодирования.
    // file, txt и т.д.
    private final String type;
    private final String orderId;

    public DataHeader(String guid, String type, String orderId) {
        this.guid = this.valueOrDefault(guid);
        this.type = this.valueOrDefault(type);
        this.orderId = this.valueOrDefault(orderId);
    }

    public String valueOrDefault(String value) {
        if (value == null || value.isEmpty()) value = "";
        return value;
    }

    @Override
    public String toString() {
        // Возвращаем строку вида "GUID|type|orderId".
        return String.join("|", this.guid, this.type, this.orderId);
    }
}

// Класс для отправки пакетов.
class Sender {
    private final String host;
    private final int port;

    Sender(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Отправка пакетов через UDP.
    public void sendPackets(List<byte[]> packets) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(host);
            for (byte[] packet : packets) {
                socket.send(new DatagramPacket(packet, packet.length, address, port));
            }
        } catch (IOException e) {
            System.err.println("Error sending packets: " + e.getMessage());
        }
    }
}

// Генератор уникальных идентификаторов UUID и GUID.
 class UniqueldentifierGenerator {
    private final String uuidAsStr;

    public UniqueldentifierGenerator() {
        this.uuidAsStr = UUID.randomUUID().toString().replace("-", "");
    }

    public String getUUID() {
        return this.uuidAsStr;
    }

    public String getGUID() {
        return uuidToGuid();
    }

    private String uuidToGuid() {
        return String.join("-"
                , this.uuidAsStr.substring(24, 32)
                , this.uuidAsStr.substring(20, 24)
                , this.uuidAsStr.substring(16, 20)
                , this.uuidAsStr.substring(0, 4)
                , this.uuidAsStr.substring(4, 16));
    }
}

