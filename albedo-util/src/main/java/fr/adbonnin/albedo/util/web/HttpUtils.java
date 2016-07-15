package fr.adbonnin.albedo.util.web;

import fr.adbonnin.albedo.util.web.support.RuntimeURISyntaxException;
import org.weborganic.furi.Parameters;
import org.weborganic.furi.URIParameters;
import org.weborganic.furi.URITemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class HttpUtils {

    public static final String DELETE = "DELETE";

    public static final String GET = "GET";

    public static final String HEAD = "HEAD";

    public static final String OPTIONS = "OPTIONS";

    public static final String PATCH = "PATCH";

    public static final String POST = "POST";

    public static final String PUT = "PUT";

    public static final String TRACE = "TRACE";

    public static URI newUri(String uri) {
        requireNonNull(uri);

        try {
            return new URI(uri);
        }
        catch (URISyntaxException e) {
            throw new RuntimeURISyntaxException(e);
        }
    }

    public static String expand(String uriTemplate, Map<String, Object> pathVariables) {
        return URITemplate.expand(uriTemplate, buildParameters(pathVariables));
    }

    private static Parameters buildParameters(Map<String, Object> pathVariables) {
        requireNonNull(pathVariables);

        final Parameters parameters = new URIParameters();

        for (Map.Entry<String, Object> entry : pathVariables.entrySet()) {
            final String name = entry.getKey();
            final Object value = entry.getValue();

            if (value instanceof String[]) {
                parameters.set(name, (String[]) value);
            }
            else if (value != null) {
                parameters.set(name, value.toString());
            }
        }

        return parameters;
    }

    private HttpUtils() { /* Cannot be instantiated */ }
}
