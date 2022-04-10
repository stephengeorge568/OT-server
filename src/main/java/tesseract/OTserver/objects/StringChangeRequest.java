package tesseract.OTserver.objects;
import java.time.Instant;

public class StringChangeRequest {
    private String timestamp;
    private String text;
    private String identity; // currently just client ip. this will change
    private MonacoRange range;
    private Long revID;

    public StringChangeRequest(String text, MonacoRange range) {
        this.text = text;
        this.range = range;
    }

    public StringChangeRequest() {}

    // Deep copy constructor
    public StringChangeRequest(StringChangeRequest other) {
        this.text = other.text;
        this.timestamp = other.timestamp;
        this.identity = other.identity;
        this.range = new MonacoRange(
                other.getRange().getStartColumn(),
                other.getRange().getEndColumn(),
                other.getRange().getStartLineNumber(),
                other.getRange().getEndLineNumber());
        this.revID = other.getRevID();
    }

    public Long getRevID() {
        return revID;
    }

    public void setRevID(Long revID) {
        this.revID = revID;
    }

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


    public boolean isEqual(StringChangeRequest req) {
        return this.getRange().isEqual(req.getRange())
                && this.text == req.getText();
    }

    @Override
    public String toString() {
        return "StringChangeRequest{" +
                "timestamp='" + timestamp + '\'' +
                ", text='" + text + '\'' +
                ", identity='" + identity + '\'' +
                ", rangeSC=" + range.getStartColumn() +
                ", revID=" + revID +
                '}';
    }
}
