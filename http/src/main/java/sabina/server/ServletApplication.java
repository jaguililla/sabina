package sabina.server;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import sabina.Router;
import sabina.route.RouteMatcher;
import sabina.route.RouteMatcherFactory;

/**
 */
public abstract class ServletApplication implements Router, ServletContextListener {
    private RouteMatcher matcher;

    /**
     * Receives notification that the web application initialization
     * process is starting.
     * <p>
     * <p>All ServletContextListeners are notified of context
     * initialization before any filters or servlets in the web
     * application are initialized.
     *
     * @param sce the ServletContextEvent containing the ServletContext
     * that is being initialized
     */
    @Override public void contextInitialized (ServletContextEvent sce) {
        try {
            final MatcherFilter filter = new MatcherFilter (getMatcher (), "servlet", false);
            routes ();
            sce.getServletContext ().addFilter ("sabina", filter).addMappingForUrlPatterns
                (EnumSet.allOf (DispatcherType.class), false, "/*");
        }
        catch (Exception e) {
            e.printStackTrace ();
        }
    }

    /**
     * Receives notification that the ServletContext is about to be
     * shut down.
     * <p>
     * <p>All servlets and filters will have been destroyed before any
     * ServletContextListeners are notified of context
     * destruction.
     *
     * @param sce the ServletContextEvent containing the ServletContext
     * that is being destroyed
     */
    @Override public void contextDestroyed (ServletContextEvent sce) {
        // Not implemented
    }

    @Override public RouteMatcher getMatcher () {
        return matcher == null? matcher = RouteMatcherFactory.create () : matcher;
    }

    protected abstract void routes ();
}
