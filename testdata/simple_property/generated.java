import com.mistraltech.smog.core.CompositePropertyMatcher;
import com.mistraltech.smog.core.PropertyMatcher;
import com.mistraltech.smog.core.ReflectingPropertyMatcher;
import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.equalTo;

@Matches(Widget.class)
public final class WidgetMatcher extends CompositePropertyMatcher<Widget> {
    private final PropertyMatcher<String> propMatcher = new ReflectingPropertyMatcher<String>("prop", this);

    private WidgetMatcher(final String matchedObjectDescription, final Widget template) {
        super(matchedObjectDescription);
        if (template != null) {
            hasProp(template.getProp());
        }
    }

    public static WidgetMatcher aWidgetThat() {
        return aWidgetLike(null);
    }

    public static WidgetMatcher aWidgetLike(final Widget template) {
        return new WidgetMatcher("a Widget", template);
    }

    public WidgetMatcher hasProp(final String prop) {
        return hasProp(equalTo(prop));
    }

    public WidgetMatcher hasProp(final Matcher<String> propMatcher) {
        this.propMatcher.setMatcher(propMatcher);
        return this;
    }
}
