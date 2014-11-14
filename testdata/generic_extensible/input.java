public class Widget<P, Q> {
    private P p;
    private Q q;

    public Widget(P p, Q q) {
        this.p = p;
        this.q = q;
    }

    public P getP() {
        return p;
    }

    public Q getQ() {
        return q;
    }
}