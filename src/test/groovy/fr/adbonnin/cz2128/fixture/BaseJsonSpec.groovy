package fr.adbonnin.cz2128.fixture

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.json.JsonWriteFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ObjectNode
import fr.adbonnin.cz2128.json.Json
import fr.adbonnin.cz2128.json.JsonProvider
import fr.adbonnin.cz2128.json.JsonUtils
import spock.lang.Specification

import java.nio.file.Files

abstract class BaseJsonSpec extends Specification {

    public static final FLATTEN_MODULE = new SimpleModule()
        .addDeserializer(SpaceCat, new FlattenCatDeserializer())
        .addSerializer(SpaceCat, new FlattenCatSerializer())

    public static final DEFAULT_MAPPER = JsonMapper.builder()
        .disable(JsonWriteFeature.QUOTE_FIELD_NAMES)
        .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .addModule(FLATTEN_MODULE)
        .build()

    public static final Json DEFAULT_CZ2128 = new Json(DEFAULT_MAPPER)

    public static final DEFAULT_UPDATE_STRATEGY = JsonUtils.replaceUpdateStrategy()

    static JsonNode readNode(String content) {
        if (content == null) {
            return null
        }
        else if (content.isEmpty()) {
            return NullNode.instance
        }
        else {
            return DEFAULT_MAPPER.readTree(content)
        }
    }

    static ArrayNode readArrayNode(String content) {
        if (content == null) {
            return null
        }
        else if (content.isEmpty()) {
            return DEFAULT_MAPPER.createArrayNode()
        }
        else {
            return DEFAULT_MAPPER.readTree(content) as ArrayNode
        }
    }

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

    static Json.Provider newMemoryJsonProvider(String content = "") {
        return DEFAULT_CZ2128.memoryProvider(content)
    }

    static Json.Provider newFileJsonProvider(String content) {
        def tempFile = Files.createTempFile('test-', '.json')
        tempFile.toFile().write(content)
        return DEFAULT_CZ2128.fileProvider(tempFile)
    }

    static JsonParser createJsonParser(String content) {
        return content == null ? null : DEFAULT_MAPPER.getFactory().createParser(content)
    }

    static void isEquals(JsonProvider actualProvider, String expectedStr) {
        def actual = actualProvider.readJsonNode()
        def expected = expectedStr == null ? NullNode.instance : DEFAULT_MAPPER.readTree(expectedStr)
        assert actual == expected
    }
}
