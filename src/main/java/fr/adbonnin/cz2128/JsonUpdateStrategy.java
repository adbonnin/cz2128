package fr.adbonnin.cz2128;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface JsonUpdateStrategy {

    boolean update(JsonNode oldNode, JsonNode newNode, JsonGenerator generator) throws IOException;
}
