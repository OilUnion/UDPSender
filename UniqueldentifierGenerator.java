import java.util.UUID;

// Генератор уникальных идентификаторов UUID и GUID.
public class UniqueldentifierGenerator {
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
