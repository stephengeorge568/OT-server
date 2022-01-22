package tesseract.OTserver.objects;
import java.time.Instant;

public class StringChangeRequest implements Comparable<StringChangeRequest>{
    private String timestamp;
    private String text;
    private String identity; // currently just client ip. this will change
    private MonacoRange range;
    private Long revID;

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

    /*
    0 if equal
    greater than 0 if this is greater than other
    less than 0 if this is less than other
     */
    @Override
    public int compareTo(StringChangeRequest stringChangeRequest) {
        // ex: 2022-01-22T21:43:12.547Z
        return Instant.parse(this.timestamp).compareTo(Instant.parse(stringChangeRequest.timestamp));
    }
}
