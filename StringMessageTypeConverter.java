public class StringMessageTypeConverter implements IMessageTypeConverter {
    private String message;

    public StringMessageTypeConverter(String message) {
       this.message = message;   
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }
}
