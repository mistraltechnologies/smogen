import com.mistraltech.smog.core.MatchAccumulator;
import com.mistraltech.smog.core.PropertyMatcher;
import com.mistraltech.smog.core.ReflectingPropertyMatcher;
import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.equalTo;

@Matches(Widget.class)
public final class WidgetMatcher extends BaseWidgetMatcher<WidgetMatcher, Widget> {
    private static final String MATCHED_OBJECT_DESCRIPTION = "a Widget";
    private final PropertyMatcher<String> propMatcher = new ReflectingPropertyMatcher<>("prop", this);

    private WidgetMatcher(final String matchedObjectDescription) {
        super(matchedObjectDescription);
    }

    public static WidgetMatcher aWidgetThat() {
        return new WidgetMatcher(MATCHED_OBJECT_DESCRIPTION);
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
    }
}
