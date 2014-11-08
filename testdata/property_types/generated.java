import Widget.Pair;
import com.mistraltech.smog.core.CompositePropertyMatcher;
import com.mistraltech.smog.core.PropertyMatcher;
import com.mistraltech.smog.core.ReflectingPropertyMatcher;
import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.equalTo;

@Matches(Widget.class)
public final class WidgetMatcher extends CompositePropertyMatcher<Widget> {
    private static final String MATCHED_OBJECT_DESCRIPTION = "a Widget";
    private final PropertyMatcher<Pair<Widget, ?>[]> arrayOfGenericMatcher = new ReflectingPropertyMatcher<Pair<Widget, ?>[]>("arrayOfGeneric", this);
    private final PropertyMatcher<Pair<? extends Pair<? extends Widget, ? super Widget>, ?>> nestedGenericsMatcher = new ReflectingPropertyMatcher<Pair<? extends Pair<? extends Widget, ? super Widget>, ?>>("nestedGenerics", this);
    private final PropertyMatcher<Widget> simpleObjectMatcher = new ReflectingPropertyMatcher<Widget>("simpleObject", this);
    private final PropertyMatcher<int[][]> multiDimensionalArrayMatcher = new ReflectingPropertyMatcher<int[][]>("multiDimensionalArray", this);
    private final PropertyMatcher<Boolean> primitiveMatcher = new ReflectingPropertyMatcher<Boolean>("primitive", this);

    private WidgetMatcher(final String matchedObjectDescription, final Widget template) {
        super(matchedObjectDescription);
        if (template != null) {
            hasArrayOfGeneric(template.getArrayOfGeneric());
            hasNestedGenerics(template.getNestedGenerics());
            hasSimpleObject(template.getSimpleObject());
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

    public WidgetMatcher hasArrayOfGeneric(final Pair<Widget, ?>[] arrayOfGeneric) {
        return hasArrayOfGeneric(equalTo(arrayOfGeneric));
    }

    public WidgetMatcher hasArrayOfGeneric(final Matcher<? super Pair<Widget, ?>[]> arrayOfGenericMatcher) {
        this.arrayOfGenericMatcher.setMatcher(arrayOfGenericMatcher);
        return this;
    }

    public WidgetMatcher hasNestedGenerics(final Pair<? extends Pair<? extends Widget, ? super Widget>, ?> nestedGenerics) {
        return hasNestedGenerics(org.hamcrest.CoreMatchers.equalTo(nestedGenerics));
    }

    public WidgetMatcher hasNestedGenerics(final Matcher<? super Pair<? extends Pair<? extends Widget, ? super Widget>, ?>> nestedGenericsMatcher) {
        this.nestedGenericsMatcher.setMatcher(nestedGenericsMatcher);
        return this;
    }

    public WidgetMatcher hasSimpleObject(final Widget simpleObject) {
        return hasSimpleObject(org.hamcrest.CoreMatchers.equalTo(simpleObject));
    }

    public WidgetMatcher hasSimpleObject(final Matcher<? super Widget> simpleObjectMatcher) {
        this.simpleObjectMatcher.setMatcher(simpleObjectMatcher);
        return this;
    }

    public WidgetMatcher hasMultiDimensionalArray(final int[][] multiDimensionalArray) {
        return hasMultiDimensionalArray(org.hamcrest.CoreMatchers.equalTo(multiDimensionalArray));
    }

    public WidgetMatcher hasMultiDimensionalArray(final Matcher<? super int[][]> multiDimensionalArrayMatcher) {
        this.multiDimensionalArrayMatcher.setMatcher(multiDimensionalArrayMatcher);
        return this;
    }

    public WidgetMatcher hasPrimitive(final boolean primitive) {
        return hasPrimitive(org.hamcrest.CoreMatchers.equalTo(primitive));
    }

    public WidgetMatcher hasPrimitive(final Matcher<? super Boolean> primitiveMatcher) {
        this.primitiveMatcher.setMatcher(primitiveMatcher);
        return this;
    }
}
