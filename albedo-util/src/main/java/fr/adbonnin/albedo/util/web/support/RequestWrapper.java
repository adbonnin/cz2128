package fr.adbonnin.albedo.util.web.support;

import fr.adbonnin.albedo.util.web.Request;
import fr.adbonnin.albedo.util.web.UnmodifiableEntries;

import java.net.URI;

import static java.util.Objects.requireNonNull;

public abstract class RequestWrapper implements Request {

    private final Request request;

    public RequestWrapper(Request request) {
        this.request = requireNonNull(request);
    }

    @Override
    public String method() {
        return request.method();
    }

    @Override
    public URI uri() {
        return request.uri();
    }

    @Override
    public UnmodifiableEntries pathVariables() {
        return request.pathVariables();
    }
}
