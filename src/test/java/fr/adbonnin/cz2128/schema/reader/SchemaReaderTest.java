package fr.adbonnin.cz2128.schema.reader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import fr.adbonnin.cz2128.schema.Schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static fr.adbonnin.cz2128.schema.XtraSchema.arrayType;
import static fr.adbonnin.cz2128.schema.XtraSchema.stringType;
import static fr.adbonnin.cz2128.schema.TestUtils.buildFactory;

public class SchemaReaderTest {

    @Test
    public void testReadType() throws Exception {
        // given
        final JsonFactory factory = buildFactory();

        final JsonParser parser = factory.createParser("{foo: 'String'}");
        parser.nextToken(); // START_OBJECT
        parser.nextToken(); // FIELD_NAME
        parser.nextToken(); // VALUE_STRING

        // when
        final Schema field = new SchemaReader(factory).readSchema(parser);

        // then
        assertTrue(field.isField());
        assertEquals(stringType(), field.getType());
    }

    @Test
    public void testReadArrayType() throws Exception {
        // given
        final JsonFactory factory = buildFactory();
        final JsonParser parser = factory.createParser("['String']");

        // when
        final Schema array = new SchemaReader(factory).readSchema(parser);

        // then
        assertTrue(array.isArray());
        assertEquals(arrayType(), array.getType());
        assertEquals(stringType(), array.getArrayOf().getType());
    }

    @Test
    public void testReadObjectArray() throws Exception {
        // given
        final JsonFactory factory = buildFactory();
        final JsonParser parser = factory.createParser("[{foo: 'String'}]");

        // when
        final Schema array = new SchemaReader(factory).readSchema(parser);

        // then
        assertTrue(array.isArray());
        assertEquals(arrayType(), array.getType());
        assertEquals(stringType(), array.getArrayOf().getField("foo").getType());
    }

    @Test
    public void testReadComplexTypeArray() throws Exception {
        // given
        final JsonFactory factory = buildFactory();
        final JsonParser parser = factory.createParser("{type: [{foo: 'String'}]}");

        // when
        final Schema array = new SchemaReader(factory).readSchema(parser);

        // then
        assertTrue(array.isArray());
        assertEquals(arrayType(), array.getType());
        assertEquals(stringType(), array.getArrayOf().getField("foo").getType());
    }

    @Test
    public void testReadComplexType() throws Exception {
        // given
        final JsonFactory factory = buildFactory();
        final JsonParser parser = factory.createParser("{type: 'String'}");

        // when
        final Schema field = new SchemaReader(factory).readSchema(parser);

        // then
        assertTrue(field.isField());
        assertEquals(stringType(), field.getType());
    }

    @Test
    public void testReadTypeField() throws Exception {
        // given
        final JsonFactory factory = buildFactory();
        final JsonParser parser = factory.createParser("{type: {type: 'String'}}");

        // when
        final Schema object = new SchemaReader(factory).readSchema(parser);

        // then
        assertTrue(object.isObject());
        assertEquals(stringType(), object.getField("type").getType());
    }

    @Test
    public void testReadDefault() throws Exception {
        // given
        final JsonFactory factory = buildFactory();
        final JsonParser parser = factory.createParser("{type: 'String', defaultValue: 'foo'}");

        // when
        final Schema field = new SchemaReader(factory).readSchema(parser);
        final JsonNode defaultValue = field.getDefaultValue();

        // then
        assertTrue(field.isField());
        assertTrue(defaultValue.isTextual());
        assertEquals(defaultValue.asText(), "foo");
    }
}
