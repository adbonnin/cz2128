package fr.adbonnin.albedo.util.web;

public class TestRoute  implements Route {

    public int callCount;

    @Override
    public void serve(Request request, Response response) {
        ++callCount;
    }
}
