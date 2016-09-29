import com.mistraltech.smog.core.CompositePropertyMatcher;
import com.mistraltech.smog.core.MatchAccumulator;
import com.mistraltech.smog.core.PropertyMatcher;
import com.mistraltech.smog.core.ReflectingPropertyMatcher;
import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.equalTo;

@Matches(Widget.class)
public class WidgetMatcher<P1, P2, R extends WidgetMatcher<P1, P2, R, T>, T extends Widget<P1, P2>> extends CompositePropertyMatcher<T> {
    private static final String MATCHED_OBJECT_DESCRIPTION = "a Widget";
    private final PropertyMatcher<P1> pMatcher = new ReflectingPropertyMatcher<P1>("p", this);
    private final PropertyMatcher<P2> qMatcher = new ReflectingPropertyMatcher<P2>("q", this);

    protected WidgetMatcher(final String matchedObjectDescription, final T template) {
        super(matchedObjectDescription);
        if (template != null) {
            hasP(template.getP());
            hasQ(template.getQ());
        }
    }

    public static <P1, P2> WidgetMatcherType<P1, P2> aWidgetThat() {
        return new WidgetMatcherType<P1, P2>(MATCHED_OBJECT_DESCRIPTION, null);
    }

    public static <P1, P2> WidgetMatcherType<P1, P2> aWidgetLike(final Widget<P1, P2> template) {
        return new WidgetMatcherType<P1, P2>(MATCHED_OBJECT_DESCRIPTION, template);
    }

    @SuppressWarnings("unchecked")
    private R self() {
        return (R) this;
    }

    public R hasP(final P1 p) {
        return hasP(equalTo(p));
    }

    public R hasP(final Matcher<? super P1> pMatcher) {
        this.pMatcher.setMatcher(pMatcher);
        return self();
    }

    public R hasQ(final P2 q) {
        return hasQ(equalTo(q));
    }

    public R hasQ(final Matcher<? super P2> qMatcher) {
        this.qMatcher.setMatcher(qMatcher);
        return self();
    }

    public static class WidgetMatcherType<P1, P2> extends WidgetMatcher<P1, P2, WidgetMatcherType<P1, P2>, Widget<P1, P2>> {
        protected WidgetMatcherType(final String matchedObjectDescription, final Widget<P1, P2> template) {
            super(matchedObjectDescription, template);
        }

        @Override
        protected void matchesSafely(final Widget<P1, P2> item, final MatchAccumulator matchAccumulator) {
            super.matchesSafely(item, matchAccumulator);
        }
    }
}
