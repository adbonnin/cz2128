package fr.adbonnin.albedo.util.io.serializer;

public class IdentifiableB extends IdentifiableA {

    public Object value2;

    public IdentifiableB(Object id, Object value1, Object value2) {
        super(id, value1);
        this.value2 = value2;
    }

    public IdentifiableB() {
        this(null, null, null);
    }

    public IdentifiableB(Object id) {
        super(id);
    }
}
