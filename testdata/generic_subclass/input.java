public class Widget<R, Q> extends BaseWidget<R> {
    private Q prop;

    public Widget(R superProp, Q prop) {
        super(superProp);
        this.prop = prop;
    }

    public Q getProp() {
        return prop;
    }
}

class BaseWidget<P> {
    private P superProp;

    public BaseWidget(P superProp) {
        this.superProp = superProp;
    }

    public P getSuperProp() {
        return superProp;
    }
}
