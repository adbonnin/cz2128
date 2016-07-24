package fr.adbonnin.albedo.util.web;

import fr.adbonnin.albedo.util.collect.UnmodifiableIterableMap;
import fr.adbonnin.albedo.util.web.support.RequestWrapper;
import org.weborganic.furi.URIPattern;
import org.weborganic.furi.URIResolveResult;
import org.weborganic.furi.URIResolver;

import java.net.URI;
import java.util.*;

import static fr.adbonnin.albedo.util.collect.IteratorUtils.asIterator;
import static fr.adbonnin.albedo.util.collect.IteratorUtils.next;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyIterator;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

public class RouteDispatcher implements RouteChain {

    private final Map<String, List<RouteHolder>> methods = new HashMap<>();

    public RouteDispatcher DELETE(String uriTemplate, Route route) {
        return route(HttpUtils.DELETE, uriTemplate, route);
    }

    public RouteDispatcher GET(String uriTemplate, Route route) {
        return route(HttpUtils.GET, uriTemplate, route);
    }

    public RouteDispatcher HEAD(String uriTemplate, Route route) {
        return route(HttpUtils.HEAD, uriTemplate, route);
    }

    public RouteDispatcher OPTIONS(String uriTemplate, Route route) {
        return route(HttpUtils.OPTIONS, uriTemplate, route);
    }

    public RouteDispatcher PATCH(String uriTemplate, Route route) {
        return route(HttpUtils.PATCH, uriTemplate, route);
    }

    public RouteDispatcher POST(String uriTemplate, Route route) {
        return route(HttpUtils.POST, uriTemplate, route);
    }

    public RouteDispatcher PUT(String uriTemplate, Route route) {
        return route(HttpUtils.PUT, uriTemplate, route);
    }

    public RouteDispatcher TRACE(String uriTemplate, Route route) {
        return route(HttpUtils.TRACE, uriTemplate, route);
    }

    public RouteDispatcher route(String method, String uriTemplate, Route route) {
        method = cleanMethod(method);

        List<RouteHolder> holders = methods.get(method);
        if (holders == null) {
            holders = new ArrayList<>();
            methods.put(method, holders);
        }

        holders.add(new RouteHolder(uriTemplate, route));
        return this;
    }

    @Override
    public void serve(Request request, Response response, Route next) {
        final String method = cleanMethod(request.method());
        final URI uri = requireNonNull(request.uri());

        final List<RouteHolder> holders = methods.get(method);
        if (holders == null) {
            next.serve(request, response);
            return;
        }

        final String path = uri.getPath();
        final URIResolver resolver = new URIResolver(path);
        for (RouteHolder holder : holders) {

            final URIResolveResult resolveResult = holder.resolve(resolver);
            if (resolveResult.getStatus() == URIResolveResult.Status.RESOLVED) {

                final URIResolveResultMap pathVariables = new URIResolveResultMap(resolveResult);
                final Request wrapped = wrapRequest(request, pathVariables);
                serveRoute(wrapped, response, holder.route());
                return;
            }
        }

        next.serve(request, response);
    }

    protected String cleanMethod(String method) {
        requireNonNull(method);
        return method.toLowerCase();
    }

    protected Request wrapRequest(Request request, UnmodifiableIterableMap<String, String> values) {
        return new DispatcherRequest(request, values);
    }

    protected void serveRoute(Request request, Response response, Route route) {
        route.serve(request, response);
    }

    private static class RouteHolder {

        private final URIPattern pattern;

        private final Route route;

        public RouteHolder(String uriTemplate, Route route) {
            requireNonNull(uriTemplate);
            requireNonNull(route);

            this.pattern = new URIPattern(uriTemplate);
            this.route = route;
        }

        public Route route() {
            return route;
        }

        public URIResolveResult resolve(URIResolver resolver) {
            return resolver.resolve(pattern);
        }
    }

    private static class URIResolveResultMap implements UnmodifiableIterableMap<String, String> {

        private final URIResolveResult resolveResult;

        public URIResolveResultMap(URIResolveResult resolveResult) {
            this.resolveResult = requireNonNull(resolveResult);
        }

        @Override
        public Set<String> keys() {
            return unmodifiableSet(resolveResult.names());
        }

        @Override
        public Iterator<String> values(String key) {
            final Object value = resolveResult.get(key);
            if (value instanceof String[]) {
                return asList((String[]) value).iterator();
            }
            else if (value != null) {
                return asIterator(value.toString());
            }
            else {
                return emptyIterator();
            }
        }

        @Override
        public String first(String key) {
            return values(key).next();
        }

        @Override
        public String first(String key, String defaultValue) {
            return next(values(key), defaultValue);
        }

        @Override
        public boolean empty() {
            return resolveResult.names().isEmpty();
        }
    }

    private static class DispatcherRequest extends RequestWrapper {

        private final UnmodifiableIterableMap<String, String> pathVariables;

        public DispatcherRequest(Request request, UnmodifiableIterableMap<String, String> pathVariables) {
            super(request);
            this.pathVariables = requireNonNull(pathVariables);
        }

        @Override
        public UnmodifiableIterableMap<String, String> pathVariables() {
            return pathVariables;
        }
    }
}
