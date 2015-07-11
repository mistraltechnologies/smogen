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

    WidgetMatcher like(final Widget template);

    WidgetMatcher has_prop(final String _prop);

    WidgetMatcher has_prop(final Matcher<? super String> _propMatcher);

    WidgetMatcher hasURL(final String uRL);

    WidgetMatcher hasURL(final Matcher<? super String> uRLMatcher);

    WidgetMatcher hasI(final String i);

    WidgetMatcher hasI(final Matcher<? super String> iMatcher);
}
