package fr.adbonnin.albedo.util.io.serializer;

import fr.adbonnin.albedo.util.Identifiable;

import java.util.Objects;

public class IdentifiableA implements Identifiable {

    public Object id;

    public Object value1;

    public IdentifiableA(Object id, Object value1) {
        this.id = id;
        this.value1 = value1;
    }

    public IdentifiableA(Object id) {
        this(id, null);
    }

    public IdentifiableA() {
        this(null, null);
    }

    @Override
    public Object id() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof IdentifiableA)) {
            return false;
        }

        final IdentifiableA other = (IdentifiableA) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return this.id == null ? 0 : this.id.hashCode();
    }
}
