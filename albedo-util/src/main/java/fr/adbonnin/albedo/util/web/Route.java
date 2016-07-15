package fr.adbonnin.albedo.util.web;

public interface Route {

    void serve(Request request, Response response);
}
