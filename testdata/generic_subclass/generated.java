import com.mistraltech.smog.core.MatchAccumulator;
import com.mistraltech.smog.core.PropertyMatcher;
import com.mistraltech.smog.core.ReflectingPropertyMatcher;
import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.equalTo;

@Matches(Widget.class)
public final class WidgetMatcher<P1, P2> extends BaseWidgetMatcher<P1, WidgetMatcher<P1, P2>, Widget<P1, P2>> {
    private static final String MATCHED_OBJECT_DESCRIPTION = "a Widget";
    private final PropertyMatcher<P2> propMatcher = new ReflectingPropertyMatcher<P2>("prop", this);

    private WidgetMatcher(final String matchedObjectDescription, final Widget<P1, P2> template) {
        super(matchedObjectDescription, template);
        if (template != null) {
            hasProp(template.getProp());
        }
    }

    public static <P1, P2> WidgetMatcher<P1, P2> aWidgetThat() {
        return new WidgetMatcher<P1, P2>(MATCHED_OBJECT_DESCRIPTION, null);
    }

    public static <P1, P2> WidgetMatcher<P1, P2> aWidgetLike(final Widget<P1, P2> template) {
        return new WidgetMatcher<P1, P2>(MATCHED_OBJECT_DESCRIPTION, template);
    }

    public WidgetMatcher<P1, P2> hasProp(final P2 prop) {
        return hasProp(equalTo(prop));
    }

    public WidgetMatcher<P1, P2> hasProp(final Matcher<? super P2> propMatcher) {
        this.propMatcher.setMatcher(propMatcher);
        return this;
    }

    @Override
    protected void matchesSafely(final Widget<P1, P2> item, final MatchAccumulator matchAccumulator) {
        super.matchesSafely(item, matchAccumulator);
    }
}
