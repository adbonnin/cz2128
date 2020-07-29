package fr.adbonnin.cz2128.fixture

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

class FlattenCatSerializer extends StdSerializer<SpaceCat> {

    FlattenCatSerializer() {
        super(SpaceCat.class)
    }

    @Override
    void serialize(SpaceCat value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray()
        gen.writeNumber(value.id)
        gen.writeString(value.name)
        gen.writeEndArray()
    }
}
