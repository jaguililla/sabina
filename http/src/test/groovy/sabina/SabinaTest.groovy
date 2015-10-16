package sabina

import org.junit.AfterClass
import org.junit.BeforeClass
import org.testng.annotations.Test
import sabina.Router.Handler
import sabina.Router.VoidHandler
import sabina.route.RouteMatch
import sabina.route.RouteMatcher
import sabina.route.RouteMatcherFactory

import java.util.function.BiConsumer
import java.util.function.Consumer

import static sabina.HttpMethod.*
import static sabina.Sabina.*

/**
 * This test only checks that matcher is called with the correct parameters and that callback
 * wrappers return proper values.
 *
 * <p>It is mostly to have a nice coverage, as this is not likely to fail.
 *
 * <p>The tests to cover this functionality are the RouteMatcher ones (in which all this methods
 * delegate functionality.
 *
 * <p>The configuration tests (change host, port, etc.) are done in ServerTest.
 *
 * @author jam
 */
@Test public class SabinaTest {
    private Consumer<Route> routeCallback

    private RouteMatcher testRouteMatcher = new RouteMatcher () {
        @Override public void processRoute (Route target) {
            routeCallback.accept (target)
        }

        @Override public <T extends Exception> void processFault (
            Class<T> fault, BiConsumer<? extends Exception, Request> handler) {
            throw new UnsupportedOperationException ()
        }

        @Override public RouteMatch findTarget (HttpMethod httpMethod, String path) {
            throw new UnsupportedOperationException ()
        }

        @Override public List<RouteMatch> findTargets (HttpMethod httpMethod, String path) {
            throw new UnsupportedOperationException ()
        }

        @Override public <T extends Exception> BiConsumer<T, Request> findHandler(
            Class<T> exceptionClass) {

            throw new UnsupportedOperationException ()
        }
    }

    @BeforeClass
    public void setFakeRouteMatcher () {
        SERVER.routeMatcher (testRouteMatcher)
    }

    @AfterClass
    public void restoreRouteMatcher () {
        SERVER.routeMatcher (RouteMatcherFactory.create ())
    }

    public void "adding after filters process the correct routes" () {
        checkMethod ({ after ({} as VoidHandler) }, AFTER)
        checkMethod ({ after ("/", {} as VoidHandler) }, AFTER, "/")
    }

    public void "adding before filters process the correct routes" () {
        checkMethod ({ before ({} as VoidHandler) }, BEFORE)
        checkMethod ({ before ("/", {} as VoidHandler) }, BEFORE, "/")
    }

    public void "adding delete routes process the correct routes" () {
        checkMethod ({ delete ("/", {} as Handler) }, DELETE, "/")
        checkMethod ({ delete ("/", {} as VoidHandler) }, DELETE, "/")
    }

    public void "adding get routes process the correct routes" () {
        checkMethod ({ get ("/", {} as Handler) }, GET, "/")
        checkMethod ({ get ("/", {} as VoidHandler) }, GET, "/")
    }

    public void "adding head routes process the correct routes" () {
        checkMethod ({ head ("/", {} as Handler) }, HEAD, "/")
        checkMethod ({ head ("/", {} as VoidHandler) }, HEAD, "/")
    }

    public void "adding options routes process the correct routes" () {
        checkMethod ({ options ("/", {} as Handler) }, OPTIONS, "/")
        checkMethod ({ options ("/", {} as VoidHandler) }, OPTIONS, "/")
    }

    public void "adding patch routes process the correct routes" () {
        checkMethod ({ patch ("/", {} as Handler) }, PATCH, "/")
        checkMethod ({ patch ("/", {} as VoidHandler) }, PATCH, "/")
    }

    public void "adding post routes process the correct routes" () {
        checkMethod ({ post ("/", {} as Handler) }, POST, "/")
        checkMethod ({ post ("/", {} as VoidHandler) }, POST, "/")
    }

    public void "adding put routes process the correct routes" () {
        checkMethod ({ put ("/", {} as Handler) }, PUT, "/")
        checkMethod ({ put ("/", {} as VoidHandler) }, PUT, "/")
    }

    public void "adding trace routes process the correct routes" () {
        checkMethod ({ trace ("/", {} as Handler) }, TRACE, "/")
        checkMethod ({ trace ("/", {} as VoidHandler) }, TRACE, "/")
    }

    public void "setting a different host changes default server" () {
        host ("jamhost")
        assert host ().equals ("jamhost")
    }

    public void "setting a different port changes default server" () {
        port ("9999")
        assert port () == 9999
        port (9999)
        assert port () == 9999
    }

    private synchronized void checkMethod (
        Runnable methodLambda, HttpMethod method, String path = Route.ALL_PATHS) {

        routeCallback = {
            assert it.method == method
            assert it.path.equals (path)
            assert it.handler != null
        }

        methodLambda.run ()
    }
}
