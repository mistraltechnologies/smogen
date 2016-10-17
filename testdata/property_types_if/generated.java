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

    WidgetMatcher like(Widget template);

    WidgetMatcher hasFinalType(Final finalType);

    WidgetMatcher hasFinalType(Matcher<? super Final> finalTypeMatcher);

    WidgetMatcher hasGenerics(Box<X> generics);

    WidgetMatcher hasGenerics(Matcher<? super Box<X>> genericsMatcher);

    WidgetMatcher hasMultiDimensionalArray(int[][] multiDimensionalArray);

    WidgetMatcher hasMultiDimensionalArray(Matcher<? super int[][]> multiDimensionalArrayMatcher);

    WidgetMatcher hasNestedWildcardGenerics(Box<Box<? super X>> nestedWildcardGenerics);

    WidgetMatcher hasNestedWildcardGenerics(Matcher<? super Box<Box<? super X>>> nestedWildcardGenericsMatcher);

    WidgetMatcher hasPrimitive(boolean primitive);

    WidgetMatcher hasPrimitive(Matcher<? super Boolean> primitiveMatcher);

    WidgetMatcher hasRecursiveType(Widget recursiveType);

    WidgetMatcher hasRecursiveType(Matcher<? super Widget> recursiveTypeMatcher);

    WidgetMatcher hasUnboundedGenerics(Box<?> unboundedGenerics);

    WidgetMatcher hasUnboundedGenerics(Matcher<? super Box<?>> unboundedGenericsMatcher);

    WidgetMatcher hasWildcardGenerics(Box<? extends X> wildcardGenerics);

    WidgetMatcher hasWildcardGenerics(Matcher<? super Box<? extends X>> wildcardGenericsMatcher);
}
