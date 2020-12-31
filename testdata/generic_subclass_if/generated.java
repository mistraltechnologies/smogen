import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static com.mistraltech.smog.proxy.javassist.JavassistMatcherGenerator.matcherOf;

@Matches(value = Widget.class, description = "a Widget")
public interface WidgetMatcher<P1, P2> extends BaseWidgetMatcher<P1, WidgetMatcher<P1, P2>, Widget<P1, P2>> {
    static <P1, P2> WidgetMatcher<P1, P2> aWidgetThat() {
        return matcherOf(WidgetMatcher.class);
    }

    static <P1, P2> WidgetMatcher<P1, P2> aWidgetLike(final Widget<P1, P2> template) {
        return matcherOf(WidgetMatcher.class).like(template);
    }

    WidgetMatcher<P1, P2> like(Widget<P1, P2> template);

    WidgetMatcher<P1, P2> hasProp(P2 prop);

    WidgetMatcher<P1, P2> hasProp(Matcher<? super P2> propMatcher);
}
