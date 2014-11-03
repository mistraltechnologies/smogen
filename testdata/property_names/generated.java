import com.mistraltech.smog.core.CompositePropertyMatcher;
import com.mistraltech.smog.core.PropertyMatcher;
import com.mistraltech.smog.core.ReflectingPropertyMatcher;
import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.equalTo;

@Matches(Widget.class)
public final class WidgetMatcher extends CompositePropertyMatcher<Widget> {
    private static final String MATCHED_OBJECT_DESCRIPTION = "a Widget";
    private final PropertyMatcher<String> _propMatcher = new ReflectingPropertyMatcher<String>("_prop", this);
    private final PropertyMatcher<String> uRLMatcher = new ReflectingPropertyMatcher<String>("URL", this);
    private final PropertyMatcher<String> iMatcher = new ReflectingPropertyMatcher<String>("i", this);

    private WidgetMatcher(final String matchedObjectDescription, final Widget template) {
        super(matchedObjectDescription);
        if (template != null) {
            has_prop(template.get_prop());
            hasURL(template.getURL());
            hasI(template.getI());
        }
    }

    public static WidgetMatcher aWidgetThat() {
        return new WidgetMatcher(MATCHED_OBJECT_DESCRIPTION, null);
    }

    public static WidgetMatcher aWidgetLike(final Widget template) {
        return new WidgetMatcher(MATCHED_OBJECT_DESCRIPTION, template);
    }

    public WidgetMatcher has_prop(final String _prop) {
        return has_prop(equalTo(_prop));
    }

    public WidgetMatcher has_prop(final Matcher<? super String> _propMatcher) {
        this._propMatcher.setMatcher(_propMatcher);
        return this;
    }

    public WidgetMatcher hasURL(final String uRL) {
        return hasURL(org.hamcrest.CoreMatchers.equalTo(uRL));
    }

    public WidgetMatcher hasURL(final Matcher<? super String> uRLMatcher) {
        this.uRLMatcher.setMatcher(uRLMatcher);
        return this;
    }

    public WidgetMatcher hasI(final String i) {
        return hasI(org.hamcrest.CoreMatchers.equalTo(i));
    }

    public WidgetMatcher hasI(final Matcher<? super String> iMatcher) {
        this.iMatcher.setMatcher(iMatcher);
        return this;
    }
}
