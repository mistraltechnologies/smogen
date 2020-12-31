import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static com.mistraltech.smog.proxy.javassist.JavassistMatcherGenerator.matcherOf;

@Matches(value = Widget.class, description = "a Widget")
public interface WidgetMatcher extends Matcher<Widget> {
    static WidgetMatcher aWidgetThat() {
        return matcherOf(WidgetMatcher.class);
    }

    static WidgetMatcher aWidgetLike(final Widget template) {
        return matcherOf(WidgetMatcher.class).like(template);
    }

    WidgetMatcher like(Widget template);

    WidgetMatcher hasURL(String uRL);

    WidgetMatcher hasURL(Matcher<? super String> uRLMatcher);

    WidgetMatcher has_prop(String _prop);

    WidgetMatcher has_prop(Matcher<? super String> _propMatcher);

    WidgetMatcher hasI(String i);

    WidgetMatcher hasI(Matcher<? super String> iMatcher);
}
