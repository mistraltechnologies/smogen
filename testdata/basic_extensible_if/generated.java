import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static com.mistraltech.smog.proxy.javassist.JavassistMatcherGenerator.matcherOf;

@Matches(value = Widget.class, description = "a Widget")
public interface WidgetMatcher<R extends WidgetMatcher<R, T>, T extends Widget> extends Matcher<T> {
    @SuppressWarnings("unchecked")
    static WidgetMatcher<?, Widget> aWidgetThat() {
        return matcherOf(WidgetMatcher.class);
    }

    @SuppressWarnings("unchecked")
    static WidgetMatcher<?, Widget> aWidgetLike(final Widget template) {
        return matcherOf(WidgetMatcher.class).like(template);
    }

    R like(final Widget template);

    R hasProp(final String prop);

    R hasProp(final Matcher<? super String> propMatcher);
}
