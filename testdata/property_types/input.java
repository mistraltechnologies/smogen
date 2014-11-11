public class Widget {
    private boolean primitive;
    private Widget recursiveType;
    private Final finalType;
    private Box<X> generics;
    private Box<? extends X> wildcardGenerics;
    private Box<?> unboundedGenerics;
    private Box<Box<? super X>> nestedWildcardGenerics;
    private int[][] multiDimensionalArray;

    public Widget() {
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public Widget getRecursiveType() {
        return recursiveType;
    }

    public Final getFinalType() {
        return finalType;
    }

    public Box<X> getGenerics() {
        return generics;
    }

    public Box<? extends X> getWildcardGenerics() {
        return wildcardGenerics;
    }

    public Box<?> getUnboundedGenerics() {
        return unboundedGenerics;
    }

    public Box<Box<? super X>> getNestedWildcardGenerics() {
        return nestedWildcardGenerics;
    }

    public int[][] getMultiDimensionalArray() {
        return multiDimensionalArray;
    }

    public class Box<T> {
    }

    public static final class Final {
    }

    public class X {
    }
}
