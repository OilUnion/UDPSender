import java.util.Arrays;

// Класс для хранения заголовка данных.
class DataHeader {
    // Строки составляющие заголовок данных.
    private final String[] items;
    // Разделитель.
    private final String separator;

    private String[] tempItems;

    public DataHeader(String separator, String ...items) {
       this.separator = separator;
       this.items = items;
    }

    public void addTempItems (String ...tempItems) {
        this.tempItems = tempItems;
    }

    public void clearTempItems () {
        this.tempItems = null;
    }

    @Override
    public String toString() {
        // Возвращаем строку вида "GUID|type|orderId".
        String str = String.join(this.separator, this.items);
        if (this.tempItems != null && this.tempItems.length != 0) {
            str = String.join(this.separator, this.tempItems);
        }
        return str + "|";
    }
}