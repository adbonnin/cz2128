package fr.adbonnin.albedo.util;

import java.util.Iterator;

public abstract class Converter<A, B, C> {

    private final transient Converter<B, A, C> reverse = new ReverseConverter<>(this);

    protected abstract B doForward(A from, B to, C context);

    protected abstract A doBackward(B from, A to, C context);

    public final B convert(A from, C context) {
        return doForward(from, null, context);
    }

    public final B convert(A from, B to, C context) {
        return doForward(from, to, context);
    }

    public Iterable<B> convert(final Iterable<? extends A> from, final C context) {

        if (from == null) {
            return null;
        }

        return new Iterable<B>() {

            @Override
            public Iterator<B> iterator() {
                return convert(from.iterator(), context);
            }
        };
    }

    public Iterator<B> convert(final Iterator<? extends A> from, final C context) {

        if (from == null) {
            return null;
        }

        return new Iterator<B>() {

            @Override
            public boolean hasNext() {
                return from.hasNext();
            }

            @Override
            public B next() {
                final A next = from.next();
                return convert(next, context);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Converter<B, A, C> reverse() {
        return reverse;
    }

    private static final class ReverseConverter<A, B, F> extends Converter<B, A, F> {

        private final Converter<A, B, F> original;

        private ReverseConverter(Converter<A, B, F> original) {
            this.original = original;
        }

        @Override
        public A doForward(B from, A to, F context) {
            return original.doBackward(from, to, context);
        }

        @Override
        public B doBackward(A from, B to, F context) {
            return original.doForward(from, to, context);
        }

        @Override
        public Converter<A, B, F> reverse() {
            return original;
        }

        @Override
        public boolean equals(Object obj) {

            if (obj == this) {
                return true;
            }

            if (!(obj instanceof Converter.ReverseConverter)) {
                return false;
            }

            final ReverseConverter other = (ReverseConverter) obj;
            return this.original.equals(other.original);
        }

        @Override
        public int hashCode() {
            return ~original.hashCode();
        }

        @Override
        public String toString() {
            return original + ".reverse()";
        }
    }
}
