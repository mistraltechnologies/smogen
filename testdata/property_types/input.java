public class Widget {
    private boolean primitive;
    private Widget recursiveType;
    private Final finalType;
    private Pair<? extends Widget, ? super Widget> generics;
    private int[][] multiDimensionalArray;

    public Widget() {
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public Widget getRecursiveType() {
        return recursiveType;
    }

    public Widget getFinalType() {
        return recursiveType;
    }

    public Pair<? extends Widget, ? super Widget> getGenerics() {
        return generics;
    }

    public int[][] getMultiDimensionalArray() {
        return multiDimensionalArray;
    }

    public class Pair<U, V> {
    }

    public final class Final {
    }
}
