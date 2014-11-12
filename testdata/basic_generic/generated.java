import com.mistraltech.smog.core.CompositePropertyMatcher;
import com.mistraltech.smog.core.MatchAccumulator;
import com.mistraltech.smog.core.ReflectingPropertyMatcher;
import com.mistraltech.smog.core.PropertyMatcher;
import com.mistraltech.smog.examples.model.generics.Box;

import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.equalTo;

public class BoxMatcher<P1> extends CompositePropertyMatcher<Box<P1>>
{
    private static final String MATCHED_OBJECT_DESCRIPTION = "a Box";
    private PropertyMatcher<P1> contentsMatcher = new ReflectingPropertyMatcher<P1>("contents", this);

    protected BoxMatcher(final String matchedObjectDescription, final Box<P1> template)
    {
        super(matchedObjectDescription);
        if (template != null)
        {
            hasContents(template.getContents());
        }
    }

    public static <P1> BoxMatcher<P1> aBoxThat()
    {
        return new BoxMatcher<P1>(MATCHED_OBJECT_DESCRIPTION, null);
    }

    public static <P1> BoxMatcher<P1> aBoxLike(final Box<P1> template)
    {
        return new BoxMatcher<P1>(MATCHED_OBJECT_DESCRIPTION, template);
    }

    public BoxMatcher<P1> hasContents(final P1 contents)
    {
        return this.hasContents(equalTo(contents));
    }

    public BoxMatcher<P1> hasContents(Matcher<? super P1> contentsMatcher)
    {
        this.contentsMatcher.setMatcher(contentsMatcher);
        return this;
    }

    // TODO is this required in other matchers?
    @Override
    protected void matchesSafely(Box<P1> item, MatchAccumulator matchAccumulator)
    {
        super.matchesSafely(item, matchAccumulator);
    }
}