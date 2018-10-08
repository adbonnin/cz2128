package fr.adbonnin.cz2128.schema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.cz2128.schema.Validator;

import static java.util.Objects.requireNonNull;

public class EqualsToValidator implements Validator {

    private final JsonNode other;

    public EqualsToValidator(JsonNode other) {
        this.other = requireNonNull(other);
    }

    @Override
    public boolean evaluate(JsonNode value) {
        return value == null || other.equals(value);
    }
}

