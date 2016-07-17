package fr.adbonnin.albedo.util.web.support;

import fr.adbonnin.albedo.util.Converter;
import fr.adbonnin.albedo.util.collect.IterableMap;
import fr.adbonnin.albedo.util.web.PartialFilter;

import java.util.Iterator;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class ConverterPartialFilter implements PartialFilter, IterableMap<Object, Object> {

    private final PartialFilter filter;

    private final ConverterPartialFilter parent;

    public ConverterPartialFilter() {
        this(PartialResponseFilter.buildWildcard());
    }

    public ConverterPartialFilter(PartialFilter filter) {
        this(filter, null);
    }

    protected ConverterPartialFilter(PartialFilter filter, ConverterPartialFilter parent) {
        this.filter = requireNonNull(filter);
        this.parent = parent;
    }

    @Override
    public Set<Object> keys() {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public Iterator<Object> values(Object key) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public Object first(Object key) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public Object first(Object key, Object defaultValue) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public boolean empty() {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public PartialFilter in(String field) {
        return filter.in(field);
    }

    @Override
    public boolean match(String field) {
        return filter.match(field);
    }

    public <A, B, F> Converter<A, B, F> matched(String field, Converter<? extends A, ? extends B, ? extends F> converter) {
        throw new UnsupportedOperationException(); // TODO
    }
}
