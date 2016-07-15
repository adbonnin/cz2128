package fr.adbonnin.albedo.util.web;

import fr.adbonnin.albedo.util.web.support.SimpleRequest;
import org.junit.Before;
import org.junit.Test;

import static fr.adbonnin.albedo.util.web.HttpUtils.GET;
import static fr.adbonnin.albedo.util.web.HttpUtils.newUri;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RouteDispatcherTest {

    private RouteDispatcher dispatcher;

    @Before
    public void before() {
        dispatcher = new RouteDispatcher();
    }

    @Test
    public void testServeRoute() {

        final TestRoute route = new TestRoute();
        dispatcher.GET("/test", route);
        assertEquals(0, route.callCount);

        final TestRoute next = new TestRoute();
        assertEquals(0, next.callCount);

        final SimpleRequest uri = new SimpleRequest()
            .method(GET)
            .uri(newUri("http://localhost/test"));

        dispatcher.serve(uri, null, next);
        assertEquals(1, route.callCount);
    }

    @Test
    public void testServePathVariableRoute() {

        final TestRoute route = new TestRoute() {

            @Override
            public void serve(Request request, Response response) {
                super.serve(request, response);
                assertEquals("value", request.pathVariables().first("var"));
            }
        };

        dispatcher.GET("/test/{var}", route);

        final TestRoute next = new TestRoute();

        final SimpleRequest uri = new SimpleRequest()
            .method(GET)
            .uri(newUri("http://localhost/test/value"));

        dispatcher.serve(uri, null, next);
        assertEquals(1, route.callCount);
    }
}
