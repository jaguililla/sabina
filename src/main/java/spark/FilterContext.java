package spark;

public final class FilterContext extends Context {
    public final Filter filter;

    FilterContext (Filter aFilter, Request aRequest, Response aResponse) {
        super (aFilter, aRequest, aResponse);
        filter = aFilter;
    }

    public String getAcceptType () {
        return filter.getAcceptType ();
    }

    public String getPath () {
        return filter.getPath ();
    }
}
