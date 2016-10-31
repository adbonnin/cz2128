package fr.adbonnin.cz2128.base;

public class FooIdentifiable implements Identifiable {

    private final Object id;

    public FooIdentifiable(Object id) {
        this.id = id;
    }

    @Override
    public Object id() {
        return id;
    }
}
