package fr.adbonnin.cz2128.schema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import fr.adbonnin.cz2128.schema.Validator;

import static fr.adbonnin.xtra.base.XtraObjects.require;

public class SizeValidator implements Validator {

    private final int min;

    private final int max;

    public SizeValidator(int min, int max) {
        require(min >= 0, "'min' must be positive ; min: " + min);
        require(max >= 0, "'max' must be positive ; max: " + max);
        require(min <= max, "'min' must be lower than or equals to 'max' ; min: " + min + ", max: " + max);

        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public boolean evaluate(JsonNode value) {

        if (value == null) {
            return true;
        }

        if (!value.isContainerNode()) {
            return false;
        }

        final int size = value.size();
        return size >= min && size <= max;
    }
}

