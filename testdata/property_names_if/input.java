import java.lang.String;

public class Widget {
    private String i;
    private String URL;
    private String _prop;

    public Widget(String i, String URL, String _prop) {
        this.i = i;
        this.URL = URL;
        this._prop = _prop;
    }

    public String getI() {
        return i;
    }

    public String getURL() {
        return URL;
    }

    public String get_prop() {
        return _prop;
    }
}

