package fr.adbonnin.cz2128.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.adbonnin.cz2128.base.Foo;
import fr.adbonnin.cz2128.io.stream.BytesStreamProcessor;
import fr.adbonnin.cz2128.io.stream.StreamProcessor;
import fr.adbonnin.cz2128.serializer.SetSerializer;
import fr.adbonnin.cz2128.serializer.ValueReader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static fr.adbonnin.cz2128.base.PredicateUtils.alwaysTrue;
import static java.util.Collections.singleton;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RepositoryTest {

    private Repository<Foo> repository;

    @BeforeMethod
    public void setUp() throws Exception {
        final ObjectMapper mapper = new ObjectMapper()
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

        final StreamProcessor processor = new BytesStreamProcessor(-1);
        final SetSerializer serializer = new SetSerializer(mapper);
        final ValueReader<Foo> reader = ValueReader.readerFor(mapper, Foo.class);

        this.repository = new Repository<>(processor, mapper, serializer, reader);
    }

    @Test
    public void test() throws Exception {

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
        assertEquals(repository.count(alwaysTrue()), 0);
        assertEquals(repository.findAll(alwaysTrue()).get(0), foo2);
        assertEquals(repository.findOne(alwaysTrue(), null), foo2);
    }
}
