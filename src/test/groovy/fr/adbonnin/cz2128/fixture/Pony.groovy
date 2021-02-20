package fr.adbonnin.cz2128.fixture

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
}
