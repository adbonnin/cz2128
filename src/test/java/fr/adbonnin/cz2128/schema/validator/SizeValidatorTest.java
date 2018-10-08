package fr.adbonnin.cz2128.schema.validator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import fr.adbonnin.cz2128.schema.Schema;
import fr.adbonnin.cz2128.schema.reader.SchemaReader;

import static org.junit.Assert.*;
import static fr.adbonnin.cz2128.schema.TestUtils.buildFactory;

public class SizeValidatorTest {

    @Test
    public void test() throws Exception {
        // given
        final JsonFactory factory = buildFactory();
        final ObjectMapper mapper = new ObjectMapper(factory);

        // when
        try {
            final JsonParser parser = factory.createParser("{type: ['Number'], minSize: -1}");
            new SchemaReader(factory).readSchema(parser);

            // then
            fail();
        }
        catch (IllegalArgumentException e) {
            assertEquals("'min' must be positive ; min: -1", e.getMessage());
        }

        // when
        JsonParser parser = factory.createParser("{type: ['Number'], maxSize: -1}");
        try {
            new SchemaReader(factory).readSchema(parser);

            // then
            fail();
        }
        catch (IllegalArgumentException e) {
            assertEquals("'max' must be positive ; max: -1", e.getMessage());
        }

        // when
        parser = factory.createParser("{type: ['Number'], size: -1}");
        try {
            new SchemaReader(factory).readSchema(parser);

            // then
            fail();
        }
        catch (IllegalArgumentException e) {
            assertEquals("'min' must be positive ; min: -1", e.getMessage());
        }

        // when
        parser = factory.createParser("{type: ['Number'], minSize: 1, maxSize: 0}");
        try {
            new SchemaReader(factory).readSchema(parser);

            // then
            fail();
        }
        catch (IllegalArgumentException e) {
            assertEquals("'min' must be lower than or equals to 'max' ; min: 1, max: 0", e.getMessage());
        }

        // when
        parser = factory.createParser("{type: ['Number'], minSize: 0, maxSize: 1}");
        Schema array = new SchemaReader(factory).readSchema(parser);
        SizeValidator validator = (SizeValidator) array.getValidators().get(0);

        // then
        assertEquals(1, array.getValidators().size());
        assertEquals(0, validator.getMin());
        assertEquals(1, validator.getMax());

        assertTrue(validator.evaluate(null));
        assertFalse(validator.evaluate(mapper.readTree("[0]").get(0)));

        assertTrue(validator.evaluate(mapper.readTree("[]")));
        assertFalse(validator.evaluate(mapper.readTree("[0, 1]")));

        assertTrue(validator.evaluate(mapper.readTree("{}")));
        assertFalse(validator.evaluate(mapper.readTree("{foo: 0, bar: 1}")));

        // when
        parser = factory.createParser("{type: ['Number'], size: 1}");
        array = new SchemaReader(factory).readSchema(parser);
        validator = (SizeValidator) array.getValidators().get(0);

        // then
        assertEquals(1, array.getValidators().size());
        assertEquals(1, validator.getMin());
        assertEquals(1, validator.getMax());
    }
}

