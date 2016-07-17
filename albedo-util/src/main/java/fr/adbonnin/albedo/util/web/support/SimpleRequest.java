package fr.adbonnin.albedo.util.web.support;

import fr.adbonnin.albedo.util.collect.IterableMap;
import fr.adbonnin.albedo.util.collect.IterableHashMap;
import fr.adbonnin.albedo.util.web.Request;

import java.net.URI;

public class SimpleRequest implements Request {

    private String method;

    private URI uri;

    private final IterableMap<String, String> values = new IterableHashMap<>();

    @Override
    public String method() {
        return this.method;
    }

    public SimpleRequest method(String method) {
        this.method = method;
        return this;
    }

    @Override
    public URI uri() {
        return uri;
    }

    public SimpleRequest uri(URI uri) {
        this.uri = uri;
        return this;
    }

    @Override
    public IterableMap<String, String> pathVariables() {
        return values;
    }
}
