public class Widget {
    private boolean primitive;
    private Widget simpleObject;
    private Pair<? extends Pair<? extends Widget, ? super Widget>, ?> nestedGenerics;
    private Pair<Widget, ?>[] arrayOfGeneric;
    private int[][] multiDimensionalArray;

    public Widget() {
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public Widget getSimpleObject() {
        return simpleObject;
    }

    public Pair<? extends Pair<? extends Widget, ? super Widget>, ?> getNestedGenerics() {
        return nestedGenerics;
    }

    public Pair<Widget, ?>[] getArrayOfGeneric() {
        return arrayOfGeneric;
    }

    public int[][] getMultiDimensionalArray() {
        return multiDimensionalArray;
    }

    public class Pair<U, V> {
    }
}
