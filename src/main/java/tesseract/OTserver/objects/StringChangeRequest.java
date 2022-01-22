package tesseract.OTserver.objects;

public class StringChangeRequest {
    private String timestamp;
    private String text;
    private String identity; // currently just client ip. this will change
    private MonacoRange range;

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


    public String getIdentity() {
        return identity;
    }

    public StringChangeRequest setIdentity(String identity) {
        this.identity = identity;
        return this;
    }

    public MonacoRange getRange() {
        return range;
    }

    public void setRange(MonacoRange range) {
        this.range = range;
    }
}
