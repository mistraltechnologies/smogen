import com.mistraltech.smog.core.CompositePropertyMatcher;
import com.mistraltech.smog.core.annotation.Matches;

@Matches(Widget.class)
public final class WidgetMatcher extends CompositePropertyMatcher<Widget> {
    private WidgetMatcher(final String matchedObjectDescription, final Widget template) {
        super(matchedObjectDescription);
    }

    public static WidgetMatcher aWidgetThat() {
        return aWidgetLike(null);
    }

    public static WidgetMatcher aWidgetLike(final Widget template) {
        return new WidgetMatcher("a Widget", template);
    }
}
