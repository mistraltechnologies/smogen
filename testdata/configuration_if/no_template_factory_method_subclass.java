import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static com.mistraltech.smog.proxy.javassist.JavassistMatcherGenerator.matcherOf;

@Matches(value = Widget.class, description = "a Widget")
public interface WidgetMatcher extends BaseWidgetMatcher<WidgetMatcher, Widget> {
    static WidgetMatcher aWidgetThat() {
        return matcherOf(WidgetMatcher.class);
    }

    WidgetMatcher hasProp(String prop);

    WidgetMatcher hasProp(Matcher<? super String> propMatcher);
}
