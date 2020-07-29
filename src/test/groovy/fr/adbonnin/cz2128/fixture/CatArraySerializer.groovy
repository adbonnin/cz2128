package fr.adbonnin.cz2128.fixture

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class CatArraySerializer extends JsonSerializer<Cat> {

    @Override
    void serialize(Cat value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray()
        gen.writeNumber(value.id)
        gen.writeNumber(value.name)
        gen.writeEndArray()
    }
}
