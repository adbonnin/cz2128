package fr.adbonnin.albedo.util.web;

public interface RouteChain {

    void serve(Request request, Response response, Route next);
}
