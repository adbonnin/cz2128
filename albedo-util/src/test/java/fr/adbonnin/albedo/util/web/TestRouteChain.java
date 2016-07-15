package fr.adbonnin.albedo.util.web;

public class TestRouteChain implements RouteChain {

    public int callCount;

    public boolean callNext;

    @Override
    public void serve(Request request, Response response, Route next) {
        ++callCount;

        if (callNext) {
            next.serve(request, response);
        }
    }
}
