package fr.adbonnin.albedo.util.web.support;

import fr.adbonnin.albedo.util.collect.IterableMap;
import fr.adbonnin.albedo.util.collect.IterableHashMap;
import fr.adbonnin.albedo.util.web.PartialFilter;
import fr.adbonnin.albedo.util.web.Request;

import java.net.URI;

public class SimpleRequest implements Request {

    private String method;

    private URI uri;

    private IterableMap<String, String> pathVariables;

    private PartialResponseFilter partialResponse;

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
        return pathVariables;
    }

    public SimpleRequest pathVariables(IterableMap<String, String> pathVariables) {
        this.pathVariables = pathVariables;
        return this;
    }

    @Override
    public PartialFilter partialResponse() {
        return partialResponse;
    }

    public SimpleRequest partialResponse(String fields) {
        this.partialResponse = PartialResponseFilter.build(fields);
        return this;
    }
}
