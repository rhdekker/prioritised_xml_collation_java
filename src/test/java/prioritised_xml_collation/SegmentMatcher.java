package prioritised_xml_collation;

import com.codepoetics.protonpack.StreamUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * Created by ellibleeker on 08/02/2017.
 */
public class SegmentMatcher extends BaseMatcher<Segment> {
    private Segment.Type type;
    private List<XMLTokenContentMatcher> tokensWa;
    private List<XMLTokenContentMatcher> tokensWb;

    @Override
    public boolean matches(Object o) {
        if (!(o instanceof Segment)) {
            return false;
        }
        Segment segment = (Segment) o;
        if (!(segment.type == this.type)) {
            return false;
        }
        // two lists for tokensWa (XMLTokenContentMatcher and actual XMLToken)
        // aligned every item in both lists >> with zip
        Stream<XMLTokenContentMatcher> streamTokensWaMatcher = this.tokensWa.stream();
        Stream<XMLToken> streamTokensWaActual = segment.tokensWa.stream();
        List<Boolean> zippedWa = StreamUtils.zip(streamTokensWaMatcher, streamTokensWaActual, (matcher, actual) -> matcher.matches(actual))
                .collect(Collectors.toList());
        if (zippedWa.contains(false)) {
            return false;
        }
        // There are more or less expectations than actual!
        if (this.tokensWa.size() != segment.tokensWa.size()) {
            return false;
        }
        // two lists for tokensWb (XMLTokenContentMatcher and actual XMLToken)
        // aligned every item in both lists >> with zip
        Stream<XMLTokenContentMatcher> streamTokensWbMatcher = this.tokensWb.stream();
        Stream<XMLToken> streamTokensWbActual = segment.tokensWb.stream();
        List<Boolean> zippedWb = StreamUtils.zip(streamTokensWbMatcher, streamTokensWbActual, (matcher, actual) -> matcher.matches(actual))
                .collect(Collectors.toList());
        if (zippedWb.contains(false)) {
            return false;
        }
        // There are more or less expectations than actual!
        // For alignment segments it is allowed to not specify b!
        if (this.type != Segment.Type.aligned && this.tokensWb.size() != segment.tokensWb.size()) {
            return false;
        }
        return true;
    }

    @Override
        public void describeTo(Description description) {
        description.appendText(type.toString()+" ");
        description.appendText(tokensWa.stream()
                .map(tokenContentMatcher -> tokenContentMatcher.expectedContent)
                .collect(joining(", "))+" ");
        description.appendText(tokensWb.stream()
                .map(tokenContentMatcher -> tokenContentMatcher.expectedContent)
                .collect(joining(", "))+" ");
        }

    @Override
    public void describeMismatch(Object object, Description description) {
            if (object instanceof Segment) {
                Segment segment = (Segment) object;
                description.appendText(" and type is " + segment.type.toString());
                description.appendText(", with tokensWa ");
                for (XMLToken token : segment.tokensWa) {
                    description.appendText(token.content+" ");
                }
                description.appendText(" and tokensWb ");
                for (XMLToken token : segment.tokensWb) {
                    description.appendText(token.content+" ");
                }
                description.appendText(".");
            }
            else {description.appendText("but actual object is "+object.getClass().getName());
            }
    }


    static SegmentMatcher sM(Segment.Type type) {
        return new SegmentMatcher(type);
    }

    SegmentMatcher tokensWa(XMLTokenContentMatcher... tokens) {
        this.tokensWa = Arrays.asList(tokens);
        return this;
    }

    SegmentMatcher tokensWb(XMLTokenContentMatcher... tokens) {
        this.tokensWb = Arrays.asList(tokens);
        return this;
    }

    private SegmentMatcher(Segment.Type type) {
        this.tokensWa = Collections.emptyList();
        this.tokensWb = Collections.emptyList();
        this.type = type;
    }


    SegmentMatcher tokensWa(String... expectedTokens) {
        List<XMLTokenContentMatcher> matchers = new ArrayList<>();
        for (String expectedToken : expectedTokens) {
            matchers.add(XMLTokenContentMatcher.t(expectedToken));
        }
        this.tokensWa = matchers;
        return this;
    }

    SegmentMatcher tokensWb(String... expectedTokens) {
        List<XMLTokenContentMatcher> matchers = new ArrayList<>();
        for (String expectedToken : expectedTokens) {
            matchers.add(XMLTokenContentMatcher.t(expectedToken));
        }
        this.tokensWb = matchers;
        return this;
    }

}


