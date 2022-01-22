package tesseract.OTserver.objects;

public class StringResponse {
    private String string;

    public StringResponse(String s) {
        this.string = s;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
