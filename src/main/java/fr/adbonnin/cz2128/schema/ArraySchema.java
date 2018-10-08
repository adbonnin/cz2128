package fr.adbonnin.cz2128.schema;

import java.util.Map;

import static fr.adbonnin.cz2128.schema.XtraSchema.arrayType;
import static java.util.Objects.requireNonNull;

public class ArraySchema extends Schema {

    private Schema arrayOf;

    public ArraySchema(SchemaType type) {
        this(new FieldSchema(type));
    }

    public ArraySchema(Schema arrayOf) {
        super(arrayType());
        this.arrayOf = requireNonNull(arrayOf);
    }

    @Override
    public boolean isField() {
        return false;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public Schema getArrayOf() {
        return arrayOf;
    }

    @Override
    public void setArrayOf(SchemaType arrayOf) {
        this.setArrayOf(new FieldSchema(arrayOf));
    }

    @Override
    public void setArrayOf(Schema arrayOf) {
        this.arrayOf = requireNonNull(arrayOf);
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

