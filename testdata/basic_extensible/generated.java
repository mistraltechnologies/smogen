import com.mistraltech.smog.core.CompositePropertyMatcher;
import com.mistraltech.smog.core.MatchAccumulator;
import com.mistraltech.smog.core.PropertyMatcher;
import com.mistraltech.smog.core.ReflectingPropertyMatcher;
import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.equalTo;

@Matches(Widget.class)
public class WidgetMatcher<R extends WidgetMatcher<R, T>, T extends Widget> extends CompositePropertyMatcher<T> {
    private static final String MATCHED_OBJECT_DESCRIPTION = "a Widget";
    private final PropertyMatcher<String> propMatcher = new ReflectingPropertyMatcher<>("prop", this);

    protected WidgetMatcher(final String matchedObjectDescription, final T template) {
        super(matchedObjectDescription);
        if (template != null) {
            hasProp(template.getProp());
        }
    }

    public static WidgetMatcherType aWidgetThat() {
        return new WidgetMatcherType(MATCHED_OBJECT_DESCRIPTION, null);
    }

    public static WidgetMatcherType aWidgetLike(final Widget template) {
        return new WidgetMatcherType(MATCHED_OBJECT_DESCRIPTION, template);
    }

    @SuppressWarnings("unchecked")
    private R self() {
        return (R) this;
    }

    public R hasProp(final String prop) {
        return hasProp(equalTo(prop));
    }

    public R hasProp(final Matcher<? super String> propMatcher) {
        this.propMatcher.setMatcher(propMatcher);
        return self();
    }

    public static class WidgetMatcherType extends WidgetMatcher<WidgetMatcherType, Widget> {
        protected WidgetMatcherType(final String matchedObjectDescription, final Widget template) {
            super(matchedObjectDescription, template);
        }

        @Override
        protected void matchesSafely(final Widget item, final MatchAccumulator matchAccumulator) {
            super.matchesSafely(item, matchAccumulator);
        }
    }
}
