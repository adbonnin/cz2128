package fr.adbonnin.albedo.util.web;

public final class RouteUtils {

    public static Route newRoute(RouteChain... chains) {
        Route route = noop();

        if (chains != null) {
            for (int i = chains.length - 1; i >= 0; i--) {
                route = end(chains[i], route);
            }
        }

        return route;
    }

    public static Route end(final RouteChain chain, final Route next) {
        return chain == null ? next : new Route() {

            @Override
            public void serve(Request request, Response response) {
                chain.serve(request, response, next);
            }
        };
    }

    public static RouteChain then(final RouteChain first, final RouteChain second) {

        if (first == null) {
            return second == null ? next() : second;
        }
        else if (second == null) {
            return first;
        }

        return new RouteChain() {
            @Override
            public void serve(Request firstRequest, Response firstResponse, final Route next) {
                first.serve(firstRequest, firstResponse, new Route() {

                    @Override
                    public void serve(Request secondRequest, Response secondResponse) {
                        second.serve(secondRequest, secondResponse, next);
                    }
                });
            }
        };
    }

    public static Route noop() {
        return BaseRoute.NOOP;
    }

    enum BaseRoute implements Route {
        /** @see RouteUtils#noop() */
        NOOP {
            @Override
            public void serve(Request request, Response response) {}
        }
    }

    public static RouteChain next() {
        return BaseRouteChain.NEXT;
    }

    enum BaseRouteChain implements RouteChain {
        /** @see RouteUtils#next() */
        NEXT {
            @Override
            public void serve(Request request, Response response, Route next) {
                next.serve(request, response);
            }
        }
    }

    private RouteUtils() { /* Cannot be instantiated */ }
}
