package fr.adbonnin.cz2128.fixture

import com.fasterxml.jackson.core.type.TypeReference

class Pony {

    String name

    String color

    boolean equals(o) {

        if (is(o)) {
            return true
        }

        if (!(o instanceof Pony)) {
            return false
        }

        final Pony other = (Pony) o
        return name == other.name
    }

    int hashCode() {
        return name == null ? 0 : name.hashCode()
    }

    static TYPE_REF = new TypeReference<Pony>() {}
}
