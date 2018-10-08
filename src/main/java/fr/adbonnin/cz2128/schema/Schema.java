package fr.adbonnin.cz2128.schema;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fr.adbonnin.xtra.predicate.XtraPredicates.evaluateAnd;
import static java.util.Objects.requireNonNull;

public abstract class Schema implements Validator {

    private final SchemaType type;

    private final List<Validator> validators = new ArrayList<>();

    private JsonNode defaultValue;

    public Schema() {
        this(null);
    }

    public Schema(SchemaType type) {
        this.type = requireNonNull(type);
    }

    public abstract boolean isField();

    public abstract boolean isArray();

    public abstract Schema getArrayOf();

    public abstract void setArrayOf(SchemaType arrayOf);

    public abstract void setArrayOf(Schema arrayOf);

    public abstract boolean isObject();

    public abstract Map<String, Schema> getFields();

    public abstract void addFields(Map<String, Schema> fields);

    public abstract Schema getField(String name);

    public abstract void addField(String name, Schema field);

    public SchemaType getType() {
        return this.type;
    }

    public List<Validator> getValidators() {
        return this.validators;
    }

    public Validator getValidator(int index) {
        return this.validators.get(index);
    }

    public void addValidator(Validator validator) {
        requireNonNull(validator);
        this.validators.add(validator);
    }

    public boolean evaluateValidators(JsonNode node) {
        return evaluateAnd(node, this.validators.iterator());
    }

    public JsonNode getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(JsonNode defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean evaluate(JsonNode node) {
        return type.evaluate(node) && evaluateValidators(node);
    }
}

