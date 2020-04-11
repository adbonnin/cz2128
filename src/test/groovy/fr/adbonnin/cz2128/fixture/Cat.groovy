package fr.adbonnin.cz2128.fixture

class Cat {

    Integer id

    String name

    boolean equals(o) {

        if (is(o)) {
            return true
        }

        if (!(o instanceof Cat)) {
            return false
        }

        final Cat other = (Cat) o
        return id == other.id
    }

    int hashCode() {
        return id == null ? 0 : id.hashCode()
    }
}
