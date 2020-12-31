public class Widget extends BaseWidget {
    private String prop;

    public Widget(int superProp, String prop) {
        super(superProp);
        this.prop = prop;
    }

    public String getProp() {
        return prop;
    }
}

class BaseWidget {
    private int superProp;

    public BaseWidget(int superProp) {
        this.superProp = superProp;
    }

    public int getSuperProp() {
        return superProp;
    }
}