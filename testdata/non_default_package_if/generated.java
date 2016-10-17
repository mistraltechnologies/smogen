package com.acme;

import com.acme.widget.Widget;
import com.mistraltech.smog.core.annotation.Matches;
import org.hamcrest.Matcher;

import static com.mistraltech.smog.proxy.javassist.JavassistMatcherGenerator.matcherOf;

@Matches(value = com.acme.widget.Widget.class, description = "a Widget")
public interface WidgetMatcher extends Matcher<Widget> {
    static WidgetMatcher aWidgetThat() {
        return matcherOf(WidgetMatcher.class);
    }

    static WidgetMatcher aWidgetLike(final Widget template) {
        return matcherOf(WidgetMatcher.class).like(template);
    }

    WidgetMatcher like(Widget template);
}
