package spark;

public final class RouteContext extends Context {
    public final Route route;

    RouteContext (Route aRoute, Request aRequest, Response aResponse) {
        super (aRoute, aRequest, aResponse);
        route = aRoute;
    }

    public String getAcceptType () {
        return route.getAcceptType ();
    }

    public String getPath () {
        return route.getPath ();
    }
}
