import com.mistraltech.smog.core.CompositePropertyMatcher;
import com.mistraltech.smog.core.MatchAccumulator;
import com.mistraltech.smog.core.PropertyMatcher;
import com.mistraltech.smog.core.ReflectingPropertyMatcher;
import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.equalTo;

@Matches(Widget.class)
public final class WidgetMatcher<P1> extends CompositePropertyMatcher<Widget<P1>> {
    private static final String MATCHED_OBJECT_DESCRIPTION = "a Widget";
    private final PropertyMatcher<P1> contentsMatcher = new ReflectingPropertyMatcher<>("contents", this);

    private WidgetMatcher(final String matchedObjectDescription, final Widget<P1> template) {
        super(matchedObjectDescription);
        if (template != null) {
            hasContents(template.getContents());
        }
    }

    public static <P1> WidgetMatcher<P1> aWidgetThat() {
        return new WidgetMatcher<P1>(MATCHED_OBJECT_DESCRIPTION, null);
    }

    public static <P1> WidgetMatcher<P1> aWidgetLike(final Widget<P1> template) {
        return new WidgetMatcher<P1>(MATCHED_OBJECT_DESCRIPTION, template);
    }

    public WidgetMatcher<P1> hasContents(final P1 contents) {
        return hasContents(equalTo(contents));
    }

    public WidgetMatcher<P1> hasContents(final Matcher<? super P1> contentsMatcher) {
        this.contentsMatcher.setMatcher(contentsMatcher);
        return this;
    }

    @Override
    protected void matchesSafely(final Widget<P1> item, final MatchAccumulator matchAccumulator) {
        super.matchesSafely(item, matchAccumulator);
    }
}
