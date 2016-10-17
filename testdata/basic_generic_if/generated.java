import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static com.mistraltech.smog.proxy.javassist.JavassistMatcherGenerator.matcherOf;

@Matches(value = Widget.class, description = "a Widget")
public interface WidgetMatcher<P1> extends Matcher<Widget<P1>> {
    static <P1> WidgetMatcher<P1> aWidgetThat() {
        return matcherOf(WidgetMatcher.class);
    }

    static <P1> WidgetMatcher<P1> aWidgetLike(final Widget<P1> template) {
        return matcherOf(WidgetMatcher.class).like(template);
    }

    WidgetMatcher<P1> like(Widget<P1> template);

    WidgetMatcher<P1> hasContents(P1 contents);

    WidgetMatcher<P1> hasContents(Matcher<? super P1> contentsMatcher);
}
