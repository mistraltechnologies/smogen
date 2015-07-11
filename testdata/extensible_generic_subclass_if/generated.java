import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static com.mistraltech.smog.proxy.javassist.JavassistMatcherGenerator.matcherOf;

@Matches(value = Widget.class, description = "a Widget")
public interface WidgetMatcher<P1, P2, R extends WidgetMatcher<P1, P2, R, T>, T extends Widget<P1, P2>> extends BaseWidgetMatcher<P1, R, T> {
    @SuppressWarnings("unchecked")
    static <P1, P2> WidgetMatcher<?, Widget<P1, P2>> aWidgetThat() {
        return matcherOf(WidgetMatcher.class);
    }

    @SuppressWarnings("unchecked")
    static <P1, P2> WidgetMatcher<?, Widget<P1, P2>> aWidgetLike(final Widget<P1, P2> template) {
        return matcherOf(WidgetMatcher.class).like(template);
    }

    R like(final Widget<P1, P2> template);

    R hasProp(final P2 prop);

    R hasProp(final Matcher<? super P2> propMatcher);
}
