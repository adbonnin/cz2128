package fr.adbonnin.cz2128.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.adbonnin.cz2128.repository.RepositoryTest;

import java.util.Map;
import java.util.Objects;

public class Foo implements Map.Entry<String, String> {

    private final String key;

    private String value;

    public Foo(@JsonProperty("key") String key,
               @JsonProperty("value") String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String setValue(String value) {
        final String oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    @Override
    public int hashCode() {
        return key == null ? 0 : key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Foo)) {
            return false;
        }

        final Foo other = (Foo) obj;
        return Objects.equals(key, other.getKey());
    }

    @Override
    public String toString() {
        return "Foo{" +
            "key='" + key + "', " +
            "value='" + value + '\'' +
            '}';
    }
}
