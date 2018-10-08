package fr.adbonnin.cz2128.schema;

import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.xtra.predicate.Predicate;

public interface Validator extends Predicate<JsonNode> {
}
