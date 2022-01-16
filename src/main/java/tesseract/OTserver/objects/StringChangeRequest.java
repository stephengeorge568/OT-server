package tesseract.OTserver.objects;

public class StringChangeRequest {
    private String timestamp;
    private String text;
    private Integer index;

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

    public Integer getIndex() {
        return index;
    }

    public StringChangeRequest setIndex(Integer index) {
        this.index = index;
        return this;
    }
}
