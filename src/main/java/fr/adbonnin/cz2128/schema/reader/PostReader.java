package fr.adbonnin.cz2128.schema.reader;

import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.cz2128.schema.Schema;

public interface PostReader {

    /**
     *
     * @param value
     * @param node (ObjectNode)
     */
    void postRead(Schema value, JsonNode node, Iterable<PostReader> postReaders);
}

