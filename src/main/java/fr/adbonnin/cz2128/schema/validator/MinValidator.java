package fr.adbonnin.cz2128.schema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.cz2128.schema.Validator;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MinValidator implements Validator {

    private final long minValue;

    private final boolean inclusive;

    public MinValidator(long minValue, boolean inclusive) {
        this.minValue = minValue;
        this.inclusive = inclusive;
    }

    @Override
    public boolean evaluate(JsonNode value) {

        if (value == null) {
            return true;
        }

        if (value.isDouble()) {
            final double doubleValue = value.doubleValue();
            if (doubleValue == Double.POSITIVE_INFINITY) {
                return true;
            }
            else if (Double.isNaN(doubleValue) || doubleValue == Double.NEGATIVE_INFINITY) {
                return false;
            }
        }
        else if (value.isFloat()) {
            final float floatValue = value.floatValue();
            if (floatValue == Float.POSITIVE_INFINITY) {
                return true;
            }
            else if (Float.isNaN(floatValue) || floatValue == Float.NEGATIVE_INFINITY) {
                return false;
            }
        }

        if (value.isBigDecimal()) {
            return value.decimalValue().compareTo(BigDecimal.valueOf(minValue)) != -1;
        }
        else if (value.isBigInteger()) {
            return value.bigIntegerValue().compareTo(BigInteger.valueOf(minValue)) != -1;
        }
        else if (value.isNumber()) {
            return value.longValue() >= minValue;
        }
        else if (value.isTextual()) {
            try {
                final BigDecimal decimalValue = new BigDecimal(value.textValue());
                return decimalValue.compareTo(BigDecimal.valueOf(minValue)) != -1;
            }
            catch (NumberFormatException e) {
                return false;
            }
        }
        else {
            return false;
        }
    }
}

