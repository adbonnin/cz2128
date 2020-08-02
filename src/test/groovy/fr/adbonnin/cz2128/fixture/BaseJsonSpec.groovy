package fr.adbonnin.cz2128.fixture

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.json.JsonWriteFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import spock.lang.Specification

abstract class BaseJsonSpec extends Specification {

    public static DEFAULT_MAPPER = JsonMapper.builder()
            .disable(JsonWriteFeature.QUOTE_FIELD_NAMES)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build()

    static ObjectNode readObjectNode(String content) {
        if (content == null) {
            return null
        }
        else if (content.isEmpty()) {
            return DEFAULT_MAPPER.createObjectNode()
        }
        else {
            return DEFAULT_MAPPER.readTree(content) as ObjectNode
        }
    }

    static JsonParser createParser(String content) {
        return content == null ? null : DEFAULT_MAPPER.getFactory().createParser(content)
    }
}
