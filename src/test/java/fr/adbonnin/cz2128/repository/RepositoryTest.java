package fr.adbonnin.cz2128.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.adbonnin.cz2128.io.stream.BytesStreamProcessor;
import fr.adbonnin.cz2128.io.stream.StreamProcessor;
import fr.adbonnin.cz2128.serializer.SetSerializer;
import fr.adbonnin.cz2128.serializer.ValueReader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static fr.adbonnin.cz2128.base.PredicateUtils.alwaysTrue;
import static fr.adbonnin.cz2128.serializer.ValueReader.readerFor;
import static java.util.Collections.singleton;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RepositoryTest {

    private StreamProcessor processor;

    private Repository<Foo> repository;

    @BeforeMethod
    public void setUp() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final SetSerializer serializer = new SetSerializer(mapper);
        final ValueReader<Foo> reader = readerFor(mapper, Foo.class);

        this.processor = new BytesStreamProcessor(-1);
        this.repository = new Repository<>(processor, mapper, serializer, reader);
    }

    @Test
    public void test() throws Exception {

        // Repository is empty
        final Foo foo1 = new Foo(null, null);

        assertEquals(repository.count(), 0);
        assertTrue(repository.findAll(alwaysTrue()).isEmpty());
        assertEquals(foo1, repository.findOne(alwaysTrue(), foo1));

        // Add value
        assertTrue(repository.save(singleton(new Foo("foo1", "bar1"))));
        System.out.println(processor.toString());
    }

    public static class Foo implements Map.Entry<String, String> {

        private final String key;

        private String value;

        public Foo(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public String setValue(String value) {
            final String oldValue = this.value;
            this.value = value;
            return oldValue;
        }


    }
}
