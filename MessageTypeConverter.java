public final class MessageTypeConverter {
    private final Object message;

    public MessageTypeConverter(Object message) {

        this.message = message;
    }

    public byte[] toByteArray() {
        if (this.message instanceof String) {
            return String.valueOf(this.message).getBytes();
        } else {
            return new byte[0];
        }
    }
}
