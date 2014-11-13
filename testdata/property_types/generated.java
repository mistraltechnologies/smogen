import Widget.Box;
import Widget.Final;
import Widget.X;
import com.mistraltech.smog.core.CompositePropertyMatcher;
import com.mistraltech.smog.core.MatchAccumulator;
import com.mistraltech.smog.core.PropertyMatcher;
import com.mistraltech.smog.core.ReflectingPropertyMatcher;
import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.equalTo;

@Matches(Widget.class)
public final class WidgetMatcher extends CompositePropertyMatcher<Widget> {
    private static final String MATCHED_OBJECT_DESCRIPTION = "a Widget";
    private final PropertyMatcher<Box<?>> unboundedGenericsMatcher = new ReflectingPropertyMatcher<Box<?>>("unboundedGenerics", this);
    private final PropertyMatcher<Widget> recursiveTypeMatcher = new ReflectingPropertyMatcher<Widget>("recursiveType", this);
    private final PropertyMatcher<Final> finalTypeMatcher = new ReflectingPropertyMatcher<Final>("finalType", this);
    private final PropertyMatcher<Box<Box<? super X>>> nestedWildcardGenericsMatcher = new ReflectingPropertyMatcher<Box<Box<? super X>>>("nestedWildcardGenerics", this);
    private final PropertyMatcher<Box<X>> genericsMatcher = new ReflectingPropertyMatcher<Box<X>>("generics", this);
    private final PropertyMatcher<Box<? extends X>> wildcardGenericsMatcher = new ReflectingPropertyMatcher<Box<? extends X>>("wildcardGenerics", this);
    private final PropertyMatcher<int[][]> multiDimensionalArrayMatcher = new ReflectingPropertyMatcher<int[][]>("multiDimensionalArray", this);
    private final PropertyMatcher<Boolean> primitiveMatcher = new ReflectingPropertyMatcher<Boolean>("primitive", this);

    private WidgetMatcher(final String matchedObjectDescription, final Widget template) {
        super(matchedObjectDescription);
        if (template != null) {
            hasUnboundedGenerics(template.getUnboundedGenerics());
            hasRecursiveType(template.getRecursiveType());
            hasFinalType(template.getFinalType());
            hasNestedWildcardGenerics(template.getNestedWildcardGenerics());
            hasGenerics(template.getGenerics());
            hasWildcardGenerics(template.getWildcardGenerics());
            hasMultiDimensionalArray(template.getMultiDimensionalArray());
            hasPrimitive(template.isPrimitive());
        }
    }

    public static WidgetMatcher aWidgetThat() {
        return new WidgetMatcher(MATCHED_OBJECT_DESCRIPTION, null);
    }

    public static WidgetMatcher aWidgetLike(final Widget template) {
        return new WidgetMatcher(MATCHED_OBJECT_DESCRIPTION, template);
    }

    public WidgetMatcher hasUnboundedGenerics(final Box<?> unboundedGenerics) {
        return hasUnboundedGenerics(CoreMatchers.<Box<?>>equalTo(unboundedGenerics));
    }

    public WidgetMatcher hasUnboundedGenerics(final Matcher<? super Box<?>> unboundedGenericsMatcher) {
        this.unboundedGenericsMatcher.setMatcher(unboundedGenericsMatcher);
        return this;
    }

    public WidgetMatcher hasRecursiveType(final Widget recursiveType) {
        return hasRecursiveType(equalTo(recursiveType));
    }

    public WidgetMatcher hasRecursiveType(final Matcher<? super Widget> recursiveTypeMatcher) {
        this.recursiveTypeMatcher.setMatcher(recursiveTypeMatcher);
        return this;
    }

    public WidgetMatcher hasFinalType(final Final finalType) {
        return hasFinalType(equalTo(finalType));
    }

    public WidgetMatcher hasFinalType(final Matcher<? super Final> finalTypeMatcher) {
        this.finalTypeMatcher.setMatcher(finalTypeMatcher);
        return this;
    }

    public WidgetMatcher hasNestedWildcardGenerics(final Box<Box<? super X>> nestedWildcardGenerics) {
        return hasNestedWildcardGenerics(equalTo(nestedWildcardGenerics));
    }

    public WidgetMatcher hasNestedWildcardGenerics(final Matcher<? super Box<Box<? super X>>> nestedWildcardGenericsMatcher) {
        this.nestedWildcardGenericsMatcher.setMatcher(nestedWildcardGenericsMatcher);
        return this;
    }

    public WidgetMatcher hasGenerics(final Box<X> generics) {
        return hasGenerics(equalTo(generics));
    }

    public WidgetMatcher hasGenerics(final Matcher<? super Box<X>> genericsMatcher) {
        this.genericsMatcher.setMatcher(genericsMatcher);
        return this;
    }

    public WidgetMatcher hasWildcardGenerics(final Box<? extends X> wildcardGenerics) {
        return hasWildcardGenerics(CoreMatchers.<Box<? extends X>>equalTo(wildcardGenerics));
    }

    public WidgetMatcher hasWildcardGenerics(final Matcher<? super Box<? extends X>> wildcardGenericsMatcher) {
        this.wildcardGenericsMatcher.setMatcher(wildcardGenericsMatcher);
        return this;
    }

    public WidgetMatcher hasMultiDimensionalArray(final int[][] multiDimensionalArray) {
        return hasMultiDimensionalArray(equalTo(multiDimensionalArray));
    }

    public WidgetMatcher hasMultiDimensionalArray(final Matcher<? super int[][]> multiDimensionalArrayMatcher) {
        this.multiDimensionalArrayMatcher.setMatcher(multiDimensionalArrayMatcher);
        return this;
    }

    public WidgetMatcher hasPrimitive(final boolean primitive) {
        return hasPrimitive(equalTo(primitive));
    }

    public WidgetMatcher hasPrimitive(final Matcher<? super Boolean> primitiveMatcher) {
        this.primitiveMatcher.setMatcher(primitiveMatcher);
        return this;
    }

    @Override
    protected void matchesSafely(final Widget item, final MatchAccumulator matchAccumulator) {
        super.matchesSafely(item, matchAccumulator);
    }
}
