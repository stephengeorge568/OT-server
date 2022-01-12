package tesseract.OTserver.objects;

public class StringChangeRequest {
    private String timestamp;
    private String text;
    private Integer number;

    public String getTimestamp() {
        return timestamp;
    }

    public StringChangeRequest setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getText() {
        return text;
    }

    public StringChangeRequest setText(String text) {
        this.text = text;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public StringChangeRequest setNumber(Integer number) {
        this.number = number;
        return this;
    }
}
