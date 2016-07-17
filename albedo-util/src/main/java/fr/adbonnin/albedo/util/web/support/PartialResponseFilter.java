package fr.adbonnin.albedo.util.web.support;

import com.pressassociation.pr.match.Leaf;
import com.pressassociation.pr.match.Matcher;
import fr.adbonnin.albedo.util.web.PartialFilter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import static fr.adbonnin.albedo.util.StringUtils.isEmpty;
import static java.util.Objects.requireNonNull;

public class PartialResponseFilter implements PartialFilter {

    private static final String WILDCARD = "*";

    private static final int START_INDEX = 0;

    private final Map<String, PartialResponseFilter> children = new HashMap<>();

    private final Map<String, Boolean> matches = new HashMap<>();

    private final Matcher matcher;

    private final String name;

    private final int index;

    private final PartialResponseFilter parent;

    protected PartialResponseFilter(Matcher matcher, String name, PartialResponseFilter parent) {
        this.matcher = requireNonNull(matcher);
        this.name = name;
        this.index = parent == null ? START_INDEX : (parent.index() + 1);
        this.parent = parent;
    }

    protected PartialResponseFilter(Matcher matcher) {
        this(matcher, null, null);
    }

    public static PartialResponseFilter build(CharSequence fields) {
        final CharSequence cleanedFields = isEmpty(fields) ? WILDCARD : fields;
        final Matcher matcher = Matcher.of(cleanedFields);
        return new PartialResponseFilter(matcher);
    }

    public static PartialResponseFilter buildWildcard() {
        return build(null);
    }

    public String name() {
        return name;
    }

    private int index() {
        return index;
    }

    private String parentName(int index) {
        return this.index <= index ? name : parent.parentName(index);
    }

    @Override
    public PartialResponseFilter in(String field) {

        PartialResponseFilter child = children.get(field);
        if (child == null) {
            child = new PartialResponseFilter(matcher, field, this);
            children.put(field, child);
        }

        return child;
    }

    @Override
    public boolean match(String field) {

        Boolean match = matches.get(field);
        if (match == null) {
            final Iterable<String> pathParts = pathParts(field);
            final Leaf leaf = Leaf.copyOf(pathParts);

            match = matcher.matches(leaf);
            matches.put(field, match);
        }

        return match;
    }

    private Iterable<String> pathParts(final String lastPart) {
        final int lastPartIndex = index + 1;
        return new Iterable<String>() {

            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {

                    private int next = START_INDEX + 1; // Root node is not included

                    @Override
                    public boolean hasNext() {
                        return next <= lastPartIndex;
                    }

                    @Override
                    public String next() {
                        final String result;

                        if (next < lastPartIndex) {
                            result = parentName(next);
                        }
                        else if (next == lastPartIndex) {
                            result = lastPart;
                        }
                        else {
                            throw new NoSuchElementException();
                        }

                        ++next;
                        return result;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}
