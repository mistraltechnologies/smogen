import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static com.mistraltech.smog.proxy.javassist.JavassistMatcherGenerator.matcherOf;

@Matches(value = Widget.class, description = "abc Widget")
public interface WidgetMatcher extends Matcher<Widget> {
    static WidgetMatcher abcWidget() {
        return matcherOf(WidgetMatcher.class);
    }

    static WidgetMatcher abcWidget(final Widget template) {
        return matcherOf(WidgetMatcher.class).like(template);
    }

    WidgetMatcher like(Widget template);

    WidgetMatcher hasProp(String prop);

    WidgetMatcher hasProp(Matcher<? super String> propMatcher);
}
