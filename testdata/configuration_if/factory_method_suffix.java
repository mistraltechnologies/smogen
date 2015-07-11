import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static com.mistraltech.smog.proxy.javassist.JavassistMatcherGenerator.matcherOf;

@Matches(value = Widget.class, description = "Widget")
public interface WidgetMatcher extends Matcher<Widget> {
    static WidgetMatcher Widgetabc() {
        return matcherOf(WidgetMatcher.class);
    }

    static WidgetMatcher Widgetdef(final Widget template) {
        return matcherOf(WidgetMatcher.class).like(template);
    }

    WidgetMatcher like(final Widget template);

    WidgetMatcher hasProp(final String prop);

    WidgetMatcher hasProp(final Matcher<? super String> propMatcher);
}
