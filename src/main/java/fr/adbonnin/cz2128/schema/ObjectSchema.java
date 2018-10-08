package fr.adbonnin.cz2128.schema;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectSchema extends Schema {

    private Map<String, Schema> fields = new ConcurrentHashMap<>();

    public ObjectSchema() {
        super(XtraSchema.objectType());
    }

    @Override
    public boolean isField() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public Schema getArrayOf() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setArrayOf(SchemaType arrayOf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setArrayOf(Schema arrayOf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public Map<String, Schema> getFields() {
        return new HashMap<>(this.fields);
    }

    @Override
    public void addFields(Map<String, Schema> fields) {
        this.fields.putAll(fields);
    }

    @Override
    public Schema getField(String name) {
        return this.fields.get(name);
    }

    @Override
    public void addField(String name, Schema field) {
        this.fields.put(name, field);
    }
}

