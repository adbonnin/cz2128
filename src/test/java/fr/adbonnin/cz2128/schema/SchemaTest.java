package fr.adbonnin.cz2128.schema;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.adbonnin.cz2128.schema.validator.RequiredValidator;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static fr.adbonnin.cz2128.schema.TestUtils.buildFactory;
import static fr.adbonnin.cz2128.schema.XtraSchema.*;
import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class SchemaTest {

    @Test
    public void testFieldSchemaConstructor() {
        // when
        try {
            new FieldSchema(null);

            // then
            fail();
        }
        catch (NullPointerException e) {
            // should throw an exception
        }

        // when
        final Schema schema = new FieldSchema(numberType());

        // then
        assertEquals(numberType(), schema.getType());
    }

    @Test
    public void testArraySchemaConstructor() {
        // when
        Schema schema = new ArraySchema(numberType());

        // then
        assertEquals(arrayType(), schema.getType());
        assertEquals(numberType(), schema.getArrayOf().getType());

        // when
        try {
            new ArraySchema((SchemaType) null);

            // then
            fail();
        }
        catch (NullPointerException e) {
            // should throw an exception
        }

        // when
        Schema fieldSchema = new FieldSchema(stringType());
        schema = new ArraySchema(fieldSchema);

        // then
        assertEquals(fieldSchema, schema.getArrayOf());

        // when
        try {
            new ArraySchema((Schema) null);

            // then
            fail();
        }
        catch (NullPointerException e) {
            // should throw an exception
        }
    }

    @Test
    @Parameters(method = "parametersForTestSchema")
    public void testSchema(Schema schema) throws IOException {
        // given
        final JsonFactory factory = buildFactory();
        final ObjectMapper mapper = new ObjectMapper(factory);

        // when / then
        assertTrue(schema.getValidators().isEmpty());

        // when
        try {
            schema.addValidator(null);

            // then
            fail();
        }
        catch (NullPointerException e){
            // should throw an exception
            schema.getValidators().isEmpty();
        }

        // when
        Validator validator = new RequiredValidator();
        schema.addValidator(validator);

        // then
        assertEquals(1, schema.getValidators().size());
        assertEquals(validator, schema.getValidators().get(0));

        // when / then
        assertNull(schema.getDefaultValue());

        // when
        JsonNode node = mapper.readTree("{ foo: 'bar' }");
        schema.setDefaultValue(node);

        // then
        assertEquals(node, schema.getDefaultValue());
    }

    @Test
    @Parameters(method = "parametersForTestSchema")
    public void testFieldSchema(Schema schema) {
        if (schema.isField()) {
            // expect
            assertEquals(schema.getType(), numberType());
        }
    }

    @Test
    @Parameters(method = "parametersForTestSchema")
    public void testArraySchema(Schema schema) {
        if (schema.isArray()) {
            // when
            try {
                schema.setArrayOf((SchemaType) null);

                // then
                fail();
            }
            catch (NullPointerException e) {
                // should throw an exception
            }

            // when
            try {
                schema.setArrayOf((Schema) null);

                // then
                fail();
            }
            catch (NullPointerException e) {
                // should throw an exception
            }

            // when / then
            assertEquals(numberType(), schema.getArrayOf().getType());

            // when
            schema.setArrayOf(stringType());

            // then
            assertEquals(stringType(), schema.getArrayOf().getType());

            // when
            schema.setArrayOf(new FieldSchema(booleanType()));

            // then
            assertEquals(booleanType(), schema.getArrayOf().getType());
        }
        else {
            // when
            try {
                schema.getArrayOf();

                // then
                fail();
            }
            catch (UnsupportedOperationException e) {
                // should throw an exception
            }

            // when
            try {
                schema.setArrayOf(new FieldSchema(stringType()));

                // then
                fail();
            }
            catch (UnsupportedOperationException e) {
                // should throw an exception
            }

            // when
            try {
                schema.setArrayOf(stringType());

                // then
                fail();
            }
            catch (UnsupportedOperationException e) {
                // should throw an exception
            }
        }
    }

    @Test
    @Parameters(method = "parametersForTestSchema")
    public void testObjectSchema(Schema schema) {
        if (schema.isObject()) {
            // when / then
            assertTrue(schema.getFields().isEmpty());

            // when
            final Map<String, Schema> fields = new HashMap<>();
            fields.put("foo", new FieldSchema(numberType()));
            fields.put("bar", new FieldSchema(stringType()));
            schema.addFields(fields);

            Map<String, Schema> schemaFields = schema.getFields();

            // then
            assertEquals(2, schemaFields.size());
            assertEquals(numberType(), schemaFields.get("foo").getType());
            assertEquals(stringType(), schemaFields.get("bar").getType());
            assertNull(schemaFields.get("unknown"));

            // when
            schema.addField("foo", new FieldSchema(booleanType()));
            Schema schemaField = schema.getField("foo");

            // then
            assertEquals(2, schema.getFields().size());
            assertEquals(booleanType(), schemaField.getType());
        }
        else {
            // when
            try {
                schema.getField("foo");

                // then
                fail();
            }
            catch (UnsupportedOperationException e) {
                // should throw an exception
            }

            // when
            try {
                schema.addField("foo", new FieldSchema(numberType()));

                // then
                fail();
            }
            catch (UnsupportedOperationException e) {
                // should throw an exception
            }

            // when
            try {
                schema.addFields(new HashMap<String, Schema>());

                // then
                fail();
            }
            catch (UnsupportedOperationException e) {
                // should throw an exception
            }

            // when
            try {
                schema.getFields();

                // then
                fail();
            }
            catch (UnsupportedOperationException e) {
                // should throw an exception
            }
        }
    }

    private Object[] parametersForTestSchema() {
        return new Object[][]{
            {new FieldSchema(numberType())},
            {new ArraySchema(numberType())},
            {new ObjectSchema()}
        };
    }
}

