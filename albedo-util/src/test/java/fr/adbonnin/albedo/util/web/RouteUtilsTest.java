package fr.adbonnin.albedo.util.web;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;


public class RouteUtilsTest {

    @Test
    public void testNewRouteNullEmptyChains() {
        final RouteChain[] chains = null;
        Assert.assertEquals(RouteUtils.noop(), RouteUtils.newRoute(chains));
        Assert.assertEquals(RouteUtils.noop(), RouteUtils.newRoute());
        Assert.assertEquals(RouteUtils.noop(), RouteUtils.newRoute((RouteChain) null));
        Assert.assertNotEquals(RouteUtils.noop(), RouteUtils.newRoute(new TestRouteChain()));
    }

    @Test
    public void testNewRoute() {
        final TestRouteChain first = new TestRouteChain();
        final TestRouteChain second = new TestRouteChain();

        final Route route = RouteUtils.newRoute(first, second);
        assertEquals(0, first.callCount);
        assertEquals(0, second.callCount);

        route.serve(null, null);
        assertEquals(1, first.callCount);
        assertEquals(0, second.callCount);

        first.callNext = true;
        route.serve(null, null);
        assertEquals(2, first.callCount);
        assertEquals(1, second.callCount);
    }

    @Test
    public void testThen() {
        final TestRouteChain first = new TestRouteChain();
        final TestRouteChain second = new TestRouteChain();
        final TestRoute chain = new TestRoute();

        assertEquals(RouteUtils.next(), RouteUtils.then(null, null));
        assertEquals(first, RouteUtils.then(first, null));
        assertEquals(second, RouteUtils.then(null, second));

        final RouteChain route = RouteUtils.then(first, second);
        assertEquals(0, first.callCount);
        assertEquals(0, second.callCount);

        route.serve(null, null, chain);
        assertEquals(1, first.callCount);
        assertEquals(0, second.callCount);
        assertEquals(0, chain.callCount);

        first.callNext = true;
        route.serve(null, null, chain);
        assertEquals(2, first.callCount);
        assertEquals(1, second.callCount);
        assertEquals(0, chain.callCount);

        second.callNext = true;
        route.serve(null, null, chain);
        assertEquals(3, first.callCount);
        assertEquals(2, second.callCount);
        assertEquals(1, chain.callCount);
    }
}
