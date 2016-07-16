package fr.adbonnin.albedo.util.io.serializer;

import fr.adbonnin.albedo.util.Identifiable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static fr.adbonnin.albedo.util.IdentifiableUtils.equalsIdPredicate;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ArraySerializerTest {

    private ArraySerializer serializer;

    public ArraySerializerTest(ArraySerializer serializer) {
        this.serializer = serializer;
    }

    @Test
    public void testCount() throws IOException {
        final String json = "[{\"id\":\"test0\"}, {\"id\":\"test1\"}]";
        assertEquals(2, serializer.count(new StringReader(json)));
    }

    @Test
    public void testCountWithPredicate() throws IOException {
        final String json = "[{\"id\":\"test0\"}, {\"id\":\"test1\"}]";
        assertEquals(1, serializer.count(equalsIdPredicate("test0"), new StringReader(json), IdentifiableA.class));
        assertEquals(1, serializer.count(equalsIdPredicate("test1"), new StringReader(json), IdentifiableA.class));
        assertEquals(0, serializer.count(equalsIdPredicate("test2"), new StringReader(json), IdentifiableA.class));
    }

    @Test
    public void testExistsNotFound() throws IOException {
        final String json = "[{\"id\":\"test0\"}, {\"id\":\"test1\"}]";
        assertFalse(serializer.exists("test2", new StringReader(json), IdentifiableA.class));
    }

    @Test
    public void testExistsNotFoundWithInner() throws IOException {
        final String json = "[{\"id\":\"test0\", \"inner\":{\"id\":\"test2\"}}, {\"id\":\"test1\"}]";
        assertFalse(serializer.exists("test2", new StringReader(json), IdentifiableA.class));
    }

    @Test
    public void testExists() throws IOException {
        final String json = "[{\"id\":\"test0\"}, {\"id\":\"test1\"}]";
        assertTrue(serializer.exists("test1", new StringReader(json), IdentifiableA.class));
    }

    @Test
    public void testFindAllWithEmptyStr() throws IOException {
        final List<IdentifiableA> empty = serializer.findAll(new StringReader(""), IdentifiableA.class);
        assertTrue(empty.isEmpty());
    }

    @Test
    public void testFindAllWithEmptyArray() throws IOException {
        final List<IdentifiableA> emptyArray = serializer.findAll(new StringReader("[]"), IdentifiableA.class);
        assertTrue(emptyArray.isEmpty());
    }

    @Test
    public void testFindAllWithUnknownProperty() throws IOException {
        final String json = "[{\"id\":\"test0\", \"unknown\":\"property\"}]";
        serializer.findAll(new StringReader(json), IdentifiableA.class);
    }

    @Test
    public void testFindAllWithOneElement() throws IOException {
        final String json = "[{\"id\":\"test\"}]";
        final List<IdentifiableA> found = serializer.findAll(new StringReader(json), IdentifiableA.class);
        assertEquals(1, found.size());
        assertEquals(new IdentifiableA("test"), found.get(0));
    }

    @Test
    public void testFindAll() throws IOException {
        final String json = "[{\"id\":\"test0\"}, {\"id\":\"test1\"}]";
        final List<IdentifiableA> found = serializer.findAll(new StringReader(json), IdentifiableA.class);
        assertEquals(2, found.size());
        assertEquals(new IdentifiableA("test0"), found.get(0));
        assertEquals(new IdentifiableA("test1"), found.get(1));
    }

    @Test
    public void testFindAllByIds() throws IOException {
        final String json = "[{\"id\":\"test0\"}, {\"id\":\"test1\"}, {\"id\":\"test2\"}]";
        final List<IdentifiableA> found = serializer.findAll(Arrays.<Object>asList("test0", "test2"), new StringReader(json), IdentifiableA.class);
        assertEquals(2, found.size());
        assertEquals(new IdentifiableA("test0"), found.get(0));
        assertEquals(new IdentifiableA("test2"), found.get(1));
    }

    @Test
    public void testFindOne() throws IOException {
        final String json = "[{\"id\":\"test0\"}, {\"id\":\"test1\"}]";
        final Identifiable entity = serializer.findOne("test1", new StringReader(json), IdentifiableA.class);
        assertNotNull(entity);
        assertEquals("test1", entity.id());
    }

    @Test
    public void testSave() throws IOException {

        // Create entity
        StringWriter writer = new StringWriter();
        assertTrue(serializer.save(new IdentifiableB("B1", "value1", "value2"), new StringReader(""), writer, IdentifiableB.class));
        String json = writer.toString();

        IdentifiableB foundB1 = serializer.findOne("B1", new StringReader(json), IdentifiableB.class);
        assertEquals("B1", foundB1.id);
        assertEquals("value1", foundB1.value1);
        assertEquals("value2", foundB1.value2);

        // Save nothing
        writer = new StringWriter();
        assertFalse(serializer.save(Collections.<Identifiable>emptyList(), new StringReader(json), new StringWriter(), IdentifiableA.class));
        json = writer.toString();

        // Update entity
        writer = new StringWriter();
        assertTrue(serializer.save(new IdentifiableB("B1", "value3", "value2"), new StringReader(json), writer, IdentifiableB.class));
        json = writer.toString();

        foundB1 = serializer.findOne("B1", new StringReader(json), IdentifiableB.class);
        assertEquals("B1", foundB1.id);
        assertEquals("value3", foundB1.value1);
        assertEquals("value2", foundB1.value2);

        // Keep missing property
        writer = new StringWriter();
        assertTrue(serializer.save(new IdentifiableA("B1", "value4"), new StringReader(json), writer, IdentifiableA.class));
        json = writer.toString();

        foundB1 = serializer.findOne("B1", new StringReader(json), IdentifiableB.class);
        assertEquals("B1", foundB1.id);
        assertEquals("value4", foundB1.value1);
        assertEquals("value2", foundB1.value2);

        // Create new property
        writer = new StringWriter();
        assertTrue(serializer.save(new IdentifiableA("A1", "value5"), new StringReader(json), writer, IdentifiableA.class));
        json = writer.toString();

        writer = new StringWriter();
        assertTrue(serializer.save(new IdentifiableB("A1", "value5", "value6"), new StringReader(json), writer, IdentifiableB.class));
        json = writer.toString();

        IdentifiableB foundA1 = serializer.findOne("A1", new StringReader(json), IdentifiableB.class);
        assertEquals("A1", foundA1.id);
        assertEquals("value5", foundA1.value1);
        assertEquals("value6", foundA1.value2);

        foundB1 = serializer.findOne("B1", new StringReader(json), IdentifiableB.class);
        assertEquals("B1", foundB1.id);
        assertEquals("value4", foundB1.value1);
        assertEquals("value2", foundB1.value2);
    }

    @Test
    public void testDelete() throws IOException {
        String json = "[{\"id\":\"test\"}]";

        // Delete nothing
        StringWriter writer = new StringWriter();
        assertFalse(serializer.delete("unknown", new StringReader(json), writer, IdentifiableB.class));
        json = writer.toString();

        assertNotNull(serializer.findOne("test", new StringReader(json), IdentifiableB.class));

        // Delete entity
        writer = new StringWriter();
        assertTrue(serializer.delete("test", new StringReader(json), writer, IdentifiableB.class));
        json = writer.toString();

        assertNull(serializer.findOne("test", new StringReader(json), IdentifiableB.class));
    }

    @Parameterized.Parameters
    public static Collection<Object[]> instancesToTest() {
        return Arrays.asList(
            new Object[]{new JacksonArraySerializer()},
            new Object[]{new GsonArraySerializer()}
        );
    }
}
