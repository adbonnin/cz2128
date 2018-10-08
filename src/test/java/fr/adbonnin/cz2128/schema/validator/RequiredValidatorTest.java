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

public class RequiredValidatorTest {

    @Test
    public void testRequiredValidator() throws Exception {
        // given
        final JsonFactory factory = buildFactory();
        final ObjectMapper mapper = new ObjectMapper(factory);

        // when
        final JsonParser parser = factory.createParser("{type: 'String', required: true}");
        final Schema field = new SchemaReader(factory).readSchema(parser);
        final Validator validator = field.getValidator(0);

        // then
        assertEquals(1, field.getValidators().size());
        assertFalse(validator.evaluate(null));
        assertTrue(validator.evaluate(mapper.readTree("{field: 'foo'}").get("field")));
        assertFalse(validator.evaluate(mapper.readTree("{field: null}").get("field")));
    }

    @Test
    public void testRequiredValidatorWithRequiredFalse() throws Exception {
        // given
        final JsonFactory factory = buildFactory();
        final JsonParser parser = factory.createParser("{type: 'String', required: false}");

        // when
        final Schema field = new SchemaReader(factory).readSchema(parser);

        // then
        assertTrue(field.getValidators().isEmpty());
    }
}

