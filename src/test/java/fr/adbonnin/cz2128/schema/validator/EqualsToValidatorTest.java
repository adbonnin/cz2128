package fr.adbonnin.cz2128.schema.validator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import fr.adbonnin.cz2128.schema.Schema;
import fr.adbonnin.cz2128.schema.Validator;
import fr.adbonnin.cz2128.schema.reader.SchemaReader;

import static org.junit.Assert.*;
import static fr.adbonnin.cz2128.schema.TestUtils.buildFactory;

public class EqualsToValidatorTest {

    @Test
    public void testAssertEquals() throws Exception {
        // given
        final JsonFactory factory = buildFactory();
        final ObjectMapper mapper = new ObjectMapper(factory);

        // when
        final JsonParser parser = factory.createParser("{type: 'String', equalsTo: 'foo'}");
        final Schema field = new SchemaReader(factory).readSchema(parser);
        final Validator validator = field.getValidator(0);

        // then
        assertEquals(1, field.getValidators().size());
        assertTrue(validator.evaluate(null));
        assertTrue(validator.evaluate(mapper.readTree("{field: 'foo'}").get("field")));
        assertFalse(validator.evaluate(mapper.readTree("{field: 'bar'}").get("field")));
    }

    @Test
    public void testAssertEqualsObject() throws Exception {
        // given
        final JsonFactory factory = buildFactory();
        final ObjectMapper mapper = new ObjectMapper(factory);

        // when
        final JsonParser parser = factory.createParser("{type: 'String', equalsTo: {field: 'foo'}}");
        final Schema field = new SchemaReader(factory).readSchema(parser);
        final Validator validator = field.getValidator(0);

        // then
        assertEquals(1, field.getValidators().size());
        assertTrue(validator.evaluate(null));
        assertTrue(validator.evaluate(mapper.readTree("{field: 'foo'}")));
        assertFalse(validator.evaluate(mapper.readTree("{field: 'bar'}")));
    }
}

