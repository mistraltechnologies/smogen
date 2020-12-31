public class Widget<T> {
    private T contents;

    public Widget(T contents) {
        this.contents = contents;
    }

    public T getContents() {
        return contents;
    }
}