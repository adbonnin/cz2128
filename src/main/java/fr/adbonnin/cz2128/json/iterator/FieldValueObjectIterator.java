package fr.adbonnin.cz2128.json.iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.adbonnin.cz2128.base.Pair;
import fr.adbonnin.cz2128.collect.AbstractIterator;
import fr.adbonnin.cz2128.json.JsonException;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class FieldValueObjectIterator<E> extends AbstractIterator<Map.Entry<String, E>> {

    private final ObjectIterator iterator;

    private final ObjectReader reader;

    private final Predicate<String> fieldPredicate;

    private final Predicate<? super E> valuePredicate;

    public FieldValueObjectIterator(JsonParser parser, ObjectReader reader) {
        this(parser, reader, field -> true, value -> true);
    }

    public FieldValueObjectIterator(JsonParser parser, ObjectReader reader, Predicate<String> fieldPredicate, Predicate<? super E> valuePredicate) {
        this.iterator = new ObjectIterator(parser);
        this.reader = requireNonNull(reader);
        this.fieldPredicate = requireNonNull(fieldPredicate);
        this.valuePredicate = requireNonNull(valuePredicate);
    }

    @Override
    protected Map.Entry<String, E> computeNext() {
        while (iterator.hasNext()) {
            final Map.Entry<String, JsonParser> next = iterator.next();
            final String field = next.getKey();
            final JsonParser parser = next.getValue();

            final E value;
            try {
                if (!fieldPredicate.test(field)) {
                    parser.skipChildren();
                    continue;
                }

                value = reader.readValue(parser);
                if (!valuePredicate.test(value)) {
                    continue;
                }
            }
            catch (IOException e) {
                throw new JsonException(e);
            }

            return Pair.of(field, value);
        }

        return endOfData();
    }
}
