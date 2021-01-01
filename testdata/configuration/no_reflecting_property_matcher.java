import com.mistraltech.smog.core.CompositePropertyMatcher;
import com.mistraltech.smog.core.MatchAccumulator;
import com.mistraltech.smog.core.PropertyMatcher;
import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.equalTo;

@Matches(Widget.class)
public final class WidgetMatcher extends CompositePropertyMatcher<Widget> {
    private static final String MATCHED_OBJECT_DESCRIPTION = "a Widget";
    private final PropertyMatcher<String> propMatcher = new PropertyMatcher<String>("prop", this);

    private WidgetMatcher(final String matchedObjectDescription, final Widget template) {
        super(matchedObjectDescription);
        if (template != null) {
            hasProp(template.getProp());
        }
    }

    public static WidgetMatcher aWidgetThat() {
        return new WidgetMatcher(MATCHED_OBJECT_DESCRIPTION, null);
    }

    public static WidgetMatcher aWidgetLike(final Widget template) {
        return new WidgetMatcher(MATCHED_OBJECT_DESCRIPTION, template);
    }

    public WidgetMatcher hasProp(final String prop) {
        return hasProp(equalTo(prop));
    }

    public WidgetMatcher hasProp(final Matcher<? super String> propMatcher) {
        this.propMatcher.setMatcher(propMatcher);
        return this;
    }

    @Override
    protected void matchesSafely(final Widget item, final MatchAccumulator matchAccumulator) {
        super.matchesSafely(item, matchAccumulator);
        matchAccumulator.matches(propMatcher, item.getProp());
    }
}
