import java.util.List;

public interface IMessageTypeHandler {
    List<byte[]> getMessageSegments ();
}
