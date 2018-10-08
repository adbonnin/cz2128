package fr.adbonnin.cz2128.schema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.cz2128.schema.Validator;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

public class MinDecimalValidator implements Validator {

    private final BigDecimal minValue;

    private final boolean inclusive;

    public MinDecimalValidator(BigDecimal minValue, boolean inclusive) {
        this.minValue = requireNonNull(minValue);
        this.inclusive = inclusive;
    }

    @Override
    public boolean evaluate(JsonNode value) {
        return false;
    }
}

