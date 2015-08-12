package sabina

import org.junit.AfterClass
import org.junit.BeforeClass
import org.testng.annotations.Test
import sabina.Router.BiHandler
import sabina.Router.BiVoidHandler
import sabina.Router.Handler
import sabina.Router.VoidHandler
import sabina.route.RouteMatch
import sabina.route.RouteMatcher
import sabina.route.RouteMatcherFactory

import java.util.function.Consumer

import static sabina.HttpMethod.*
import static sabina.Route.DEFAULT_ACCEPT_TYPE
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

        @Override public <T extends Exception> void processFault (Fault<T> handler) {
            throw new UnsupportedOperationException ()
        }

        @Override public RouteMatch findTarget (
            HttpMethod httpMethod, String path, String acceptType) {

            throw new UnsupportedOperationException ()
        }

        @Override public List<RouteMatch> findTargets (
            HttpMethod httpMethod, String path, String acceptType) {

            throw new UnsupportedOperationException ()
        }

        @Override public Fault<? extends Exception> findHandler (
            Class<? extends Exception> exceptionClass) {

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
        checkMethod ({ after ("/", "text", {} as VoidHandler) }, AFTER, "/", "text")

        checkMethod ({ after ({} as BiVoidHandler) }, AFTER)
        checkMethod ({ after ("/", {} as BiVoidHandler) }, AFTER, "/")
        checkMethod ({ after ("/", "text", {} as BiVoidHandler) }, AFTER, "/", "text")
    }

    public void "adding before filters process the correct routes" () {
        checkMethod ({ before ({} as VoidHandler) }, BEFORE)
        checkMethod ({ before ("/", {} as VoidHandler) }, BEFORE, "/")
        checkMethod ({ before ("/", "text", {} as VoidHandler) }, BEFORE, "/", "text")

        checkMethod ({ before ({} as BiVoidHandler) }, BEFORE)
        checkMethod ({ before ("/", {} as BiVoidHandler) }, BEFORE, "/")
        checkMethod ({ before ("/", "text", {} as BiVoidHandler) }, BEFORE, "/", "text")
    }

    public void "adding delete routes process the correct routes" () {
        checkMethod ({ delete ("/", {} as Handler) }, DELETE, "/")
        checkMethod ({ delete ("/", {} as VoidHandler) }, DELETE, "/")
        checkMethod ({ delete ("/", "text", {} as Handler) }, DELETE, "/", "text")
        checkMethod ({ delete ("/", "text", {} as VoidHandler) }, DELETE, "/", "text")

        checkMethod ({ delete ("/", {} as BiHandler) }, DELETE, "/")
        checkMethod ({ delete ("/", {} as BiVoidHandler) }, DELETE, "/")
        checkMethod ({ delete ("/", "text", {} as BiHandler) }, DELETE, "/", "text")
        checkMethod ({ delete ("/", "text", {} as BiVoidHandler) }, DELETE, "/", "text")
    }

    public void "adding get routes process the correct routes" () {
        checkMethod ({ get ("/", {} as Handler) }, GET, "/")
        checkMethod ({ get ("/", {} as VoidHandler) }, GET, "/")
        checkMethod ({ get ("/", "text", {} as Handler) }, GET, "/", "text")
        checkMethod ({ get ("/", "text", {} as VoidHandler) }, GET, "/", "text")

        checkMethod ({ get ("/", {} as BiHandler) }, GET, "/")
        checkMethod ({ get ("/", {} as BiVoidHandler) }, GET, "/")
        checkMethod ({ get ("/", "text", {} as BiHandler) }, GET, "/", "text")
        checkMethod ({ get ("/", "text", {} as BiVoidHandler) }, GET, "/", "text")
    }

    public void "adding head routes process the correct routes" () {
        checkMethod ({ head ("/", {} as Handler) }, HEAD, "/")
        checkMethod ({ head ("/", {} as VoidHandler) }, HEAD, "/")
        checkMethod ({ head ("/", "text", {} as Handler) }, HEAD, "/", "text")
        checkMethod ({ head ("/", "text", {} as VoidHandler) }, HEAD, "/", "text")

        checkMethod ({ head ("/", {} as BiHandler) }, HEAD, "/")
        checkMethod ({ head ("/", {} as BiVoidHandler) }, HEAD, "/")
        checkMethod ({ head ("/", "text", {} as BiHandler) }, HEAD, "/", "text")
        checkMethod ({ head ("/", "text", {} as BiVoidHandler) }, HEAD, "/", "text")
    }

    public void "adding options routes process the correct routes" () {
        checkMethod ({ options ("/", {} as Handler) }, OPTIONS, "/")
        checkMethod ({ options ("/", {} as VoidHandler) }, OPTIONS, "/")
        checkMethod ({ options ("/", "text", {} as Handler) }, OPTIONS, "/", "text")
        checkMethod ({ options ("/", "text", {} as VoidHandler) }, OPTIONS, "/", "text")

        checkMethod ({ options ("/", {} as BiHandler) }, OPTIONS, "/")
        checkMethod ({ options ("/", {} as BiVoidHandler) }, OPTIONS, "/")
        checkMethod ({ options ("/", "text", {} as BiHandler) }, OPTIONS, "/", "text")
        checkMethod ({ options ("/", "text", {} as BiVoidHandler) }, OPTIONS, "/", "text")
    }

    public void "adding patch routes process the correct routes" () {
        checkMethod ({ patch ("/", {} as Handler) }, PATCH, "/")
        checkMethod ({ patch ("/", {} as VoidHandler) }, PATCH, "/")
        checkMethod ({ patch ("/", "text", {} as Handler) }, PATCH, "/", "text")
        checkMethod ({ patch ("/", "text", {} as VoidHandler) }, PATCH, "/", "text")

        checkMethod ({ patch ("/", {} as BiHandler) }, PATCH, "/")
        checkMethod ({ patch ("/", {} as BiVoidHandler) }, PATCH, "/")
        checkMethod ({ patch ("/", "text", {} as BiHandler) }, PATCH, "/", "text")
        checkMethod ({ patch ("/", "text", {} as BiVoidHandler) }, PATCH, "/", "text")
    }

    public void "adding post routes process the correct routes" () {
        checkMethod ({ post ("/", {} as Handler) }, POST, "/")
        checkMethod ({ post ("/", {} as VoidHandler) }, POST, "/")
        checkMethod ({ post ("/", "text", {} as Handler) }, POST, "/", "text")
        checkMethod ({ post ("/", "text", {} as VoidHandler) }, POST, "/", "text")

        checkMethod ({ post ("/", {} as BiHandler) }, POST, "/")
        checkMethod ({ post ("/", {} as BiVoidHandler) }, POST, "/")
        checkMethod ({ post ("/", "text", {} as BiHandler) }, POST, "/", "text")
        checkMethod ({ post ("/", "text", {} as BiVoidHandler) }, POST, "/", "text")
    }

    public void "adding put routes process the correct routes" () {
        checkMethod ({ put ("/", {} as Handler) }, PUT, "/")
        checkMethod ({ put ("/", {} as VoidHandler) }, PUT, "/")
        checkMethod ({ put ("/", "text", {} as Handler) }, PUT, "/", "text")
        checkMethod ({ put ("/", "text", {} as VoidHandler) }, PUT, "/", "text")

        checkMethod ({ put ("/", {} as BiHandler) }, PUT, "/")
        checkMethod ({ put ("/", {} as BiVoidHandler) }, PUT, "/")
        checkMethod ({ put ("/", "text", {} as BiHandler) }, PUT, "/", "text")
        checkMethod ({ put ("/", "text", {} as BiVoidHandler) }, PUT, "/", "text")
    }

    public void "adding trace routes process the correct routes" () {
        checkMethod ({ trace ("/", {} as Handler) }, TRACE, "/")
        checkMethod ({ trace ("/", {} as VoidHandler) }, TRACE, "/")
        checkMethod ({ trace ("/", "text", {} as Handler) }, TRACE, "/", "text")
        checkMethod ({ trace ("/", "text", {} as VoidHandler) }, TRACE, "/", "text")

        checkMethod ({ trace ("/", {} as BiHandler) }, TRACE, "/")
        checkMethod ({ trace ("/", {} as BiVoidHandler) }, TRACE, "/")
        checkMethod ({ trace ("/", "text", {} as BiHandler) }, TRACE, "/", "text")
        checkMethod ({ trace ("/", "text", {} as BiVoidHandler) }, TRACE, "/", "text")
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

    private void checkMethod (
        Runnable methodLambda,
        HttpMethod method,
        String path = Route.ALL_PATHS,
        String acceptType = method == AFTER || method == BEFORE?
            "text/html" : DEFAULT_ACCEPT_TYPE) {

        routeCallback = {
            assert it.method == method
            assert it.path.equals (path)
            assert it.acceptType.equals (acceptType)
            assert it.handler != null
        }

        methodLambda.run ()
    }
}
