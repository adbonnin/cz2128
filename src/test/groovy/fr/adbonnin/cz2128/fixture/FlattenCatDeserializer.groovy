package fr.adbonnin.cz2128.fixture

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

class FlattenCatDeserializer extends StdDeserializer<SpaceCat> {

    FlattenCatDeserializer() {
        super(SpaceCat.class)
    }

    @Override
    SpaceCat deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser)
        if (!node.isArray()) {
            throw new IllegalArgumentException("Flatten cat are stored in arrays") // should not append
        }

        def cat = new SpaceCat()
        cat.setId(node.get(0).asInt())
        cat.setName(node.get(1).asText())
        return cat
    }
}
