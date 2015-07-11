import Widget.Box;
import Widget.Final;
import Widget.X;
import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static com.mistraltech.smog.proxy.javassist.JavassistMatcherGenerator.matcherOf;

@Matches(value = Widget.class, description = "a Widget")
public interface WidgetMatcher extends Matcher<Widget> {
    static WidgetMatcher aWidgetThat() {
        return matcherOf(WidgetMatcher.class);
    }

    static WidgetMatcher aWidgetLike(final Widget template) {
        return matcherOf(WidgetMatcher.class).like(template);
    }

    WidgetMatcher like(final Widget template);

    WidgetMatcher hasUnboundedGenerics(final Box<?> unboundedGenerics);

    WidgetMatcher hasUnboundedGenerics(final Matcher<? super Box<?>> unboundedGenericsMatcher);

    WidgetMatcher hasRecursiveType(final Widget recursiveType);

    WidgetMatcher hasRecursiveType(final Matcher<? super Widget> recursiveTypeMatcher);

    WidgetMatcher hasFinalType(final Final finalType);

    WidgetMatcher hasFinalType(final Matcher<? super Final> finalTypeMatcher);

    WidgetMatcher hasNestedWildcardGenerics(final Box<Box<? super X>> nestedWildcardGenerics);

    WidgetMatcher hasNestedWildcardGenerics(final Matcher<? super Box<Box<? super X>>> nestedWildcardGenericsMatcher);

    WidgetMatcher hasGenerics(final Box<X> generics);

    WidgetMatcher hasGenerics(final Matcher<? super Box<X>> genericsMatcher);

    WidgetMatcher hasWildcardGenerics(final Box<? extends X> wildcardGenerics);

    WidgetMatcher hasWildcardGenerics(final Matcher<? super Box<? extends X>> wildcardGenericsMatcher);

    WidgetMatcher hasMultiDimensionalArray(final int[][] multiDimensionalArray);

    WidgetMatcher hasMultiDimensionalArray(final Matcher<? super int[][]> multiDimensionalArrayMatcher);

    WidgetMatcher hasPrimitive(final boolean primitive);

    WidgetMatcher hasPrimitive(final Matcher<? super Boolean> primitiveMatcher);
}
