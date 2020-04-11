package fr.adbonnin.cz2128.fixture

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import spock.lang.Specification

abstract class BaseJsonSpec extends Specification {

    static mapper = new ObjectMapper()
            .disable(JsonGenerator.Feature.QUOTE_FIELD_NAMES)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    static ObjectNode readObjectNode(String content) {
        return content == null ? null : mapper.readTree(content) as ObjectNode
    }

    static JsonParser createParser(String content) {
        return content == null ? null : mapper.getFactory().createParser(content)
    }
}
