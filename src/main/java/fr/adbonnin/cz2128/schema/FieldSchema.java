package fr.adbonnin.cz2128.schema;

import java.util.Map;

public class FieldSchema extends Schema {

    public FieldSchema() {
        super();
    }

    public FieldSchema(SchemaType type) {
        super(type);
    }

    @Override
    public boolean isField() {
        return true;
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
        return false;
    }

    @Override
    public Map<String, Schema> getFields() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addFields(Map<String, Schema> fields) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Schema getField(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addField(String name, Schema field) {
        throw new UnsupportedOperationException();
    }
}

