public class OneCStringMessageTypeConverter implements IMessageTypeConverter {
    private String message;

    public OneCStringMessageTypeConverter(String message) {
        this.message = message;
    }
    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }
}
