import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultMessageSplitter implements ISplitable {
    private static final int MAX_PACKET_SIZE = 65_000;
    private final int dataSegmentSize;
    private final byte[] data;

    public DefaultMessageSplitter(byte[] data) {
        this.data = data;
        this.dataSegmentSize = MAX_PACKET_SIZE;
    }

    public DefaultMessageSplitter(byte[] data, int dataSegmentSize) {
       this.data = data;
       this.dataSegmentSize = dataSegmentSize;
    }

    @Override
    public List<byte[]> split() {
        if (this.data == null || this.dataSegmentSize == 0) {
            return new ArrayList<>();
        }
        int dataSegmentSizeAsUint = Math.abs(this.dataSegmentSize);
        int segmentCount = (int)Math.ceil(((double) this.data.length / dataSegmentSizeAsUint) + 0.1);
        return splitDataIntoSegments(new ArrayList<>(), segmentCount);
    }

    private List<byte[]> splitDataIntoSegments(List<byte[]> segments, int segmentCount) {
        for (int i = 0; i < segmentCount; i++) {
            int start = i * (this.data.length / segmentCount);
            int end = (i == segmentCount - 1) ? this.data.length : (i + 1) * (this.data.length / segmentCount);
            segments.add(Arrays.copyOfRange(this.data, start, end));
        }
        return segments;
    }
}
