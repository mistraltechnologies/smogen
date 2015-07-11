import com.mistraltech.smog.core.annotation.Matches;
import com.mistraltech.smog.core.annotation.MatchesProperty;
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

    @MatchesProperty("prop")
    WidgetMatcher abcProp(final String prop);

    @MatchesProperty("prop")
    WidgetMatcher abcProp(final Matcher<? super String> propMatcher);
}
