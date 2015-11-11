package sabina.backend.undertow;

import javax.servlet.Filter;

import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.InstanceHandle;
import sabina.servlet.MatcherFilter;

/**
 * @author jam
 */
final class MatcherFilterInfo extends FilterInfo implements Cloneable {
    private final MatcherFilter matcherFilter;

    public MatcherFilterInfo (final String name, final MatcherFilter aMatcher) {
        super (name, aMatcher.getClass ());
        matcherFilter = aMatcher;
    }

    @Override public FilterInfo clone () {
        MatcherFilterInfo info = new MatcherFilterInfo (getName (), matcherFilter);
        info.setAsyncSupported (isAsyncSupported ());
        return info;
    }

    @Override public InstanceFactory<? extends Filter> getInstanceFactory () {
        return (InstanceFactory<MatcherFilter>)() -> new InstanceHandle<MatcherFilter> () {
            @Override public MatcherFilter getInstance () {
                return new MatcherFilter (
                    matcherFilter.router,
                    matcherFilter.backend,
                    matcherFilter.hasOtherHandlers);
            }

            @Override public void release () {}
        };
    }
}
