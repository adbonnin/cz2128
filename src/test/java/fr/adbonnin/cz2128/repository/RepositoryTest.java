package fr.adbonnin.cz2128.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.adbonnin.cz2128.base.Foo;
import fr.adbonnin.cz2128.base.PredicateUtils;
import fr.adbonnin.cz2128.io.stream.BytesStreamProcessor;
import fr.adbonnin.cz2128.io.stream.StreamProcessor;
import fr.adbonnin.cz2128.serializer.FieldSerializer;
import fr.adbonnin.cz2128.serializer.Serializer;
import fr.adbonnin.cz2128.serializer.SetSerializer;
import fr.adbonnin.cz2128.serializer.ValueReader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static fr.adbonnin.cz2128.base.PredicateUtils.alwaysTrue;
import static org.testng.Assert.*;

public class RepositoryTest {

    public Repository<Foo> createRepository(ObjectMapper mapper, Serializer serializer) {
        final StreamProcessor processor = new BytesStreamProcessor(-1);
        final ValueReader<Foo> reader = ValueReader.readerFor(mapper, Foo.class);
        return new Repository<>(processor, mapper, serializer, reader);
    }

    @Test
    public void test1() throws Exception {
        final ObjectMapper mapper = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        final SetSerializer serializer = new SetSerializer(mapper);
        test(createRepository(mapper, serializer));
    }

    @Test
    public void test2() throws Exception {
        final ObjectMapper mapper = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        final FieldSerializer serializer = new FieldSerializer(new SetSerializer(mapper), "test");
        test(createRepository(mapper, serializer));
    }

    public void test(Repository<Foo> repository) throws Exception {

        // Repository is empty
        final Foo foo1 = new Foo("foo1", "bar1");

        assertEquals(repository.count(), 0);
        assertEquals(repository.count(alwaysTrue()), 0);
        assertTrue(repository.findAll(alwaysTrue()).isEmpty());
        assertEquals(repository.findOne(alwaysTrue(), foo1), foo1);

        // Add value
        final Foo foo2 = new Foo("foo2", "bar2");
        assertTrue(repository.save(foo2));

        assertEquals(repository.count(), 1);
        assertEquals(repository.count(alwaysTrue()), 1);
        assertEquals(repository.findAll(alwaysTrue()).get(0), foo2);
        assertEquals(repository.findOne(alwaysTrue(), null), foo2);

        // Add existing value
        assertTrue(repository.save(foo2));

        assertEquals(repository.count(), 1);
        assertEquals(repository.count(alwaysTrue()), 1);
        assertEquals(repository.findAll(alwaysTrue()).get(0), foo2);
        assertEquals(repository.findOne(alwaysTrue(), null), foo2);

        // Remove not found value
        assertFalse(repository.delete(PredicateUtils.<Foo>alwaysFalse()));

        assertEquals(repository.count(), 1);
        assertEquals(repository.count(alwaysTrue()), 1);
        assertEquals(repository.findAll(alwaysTrue()).get(0), foo2);
        assertEquals(repository.findOne(alwaysTrue(), null), foo2);

        // Remove exiting value
        assertTrue(repository.delete(PredicateUtils.<Foo>alwaysTrue()));

        assertEquals(repository.count(), 0);
        assertEquals(repository.count(alwaysTrue()), 0);
        assertTrue(repository.findAll(alwaysTrue()).isEmpty());
        assertEquals(repository.findOne(alwaysTrue(), foo1), foo1);
    }
}
