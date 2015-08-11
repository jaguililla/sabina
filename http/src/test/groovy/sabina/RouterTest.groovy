package sabina

import java.util.function.BiConsumer
import java.util.function.Consumer

import org.testng.annotations.Test
import sabina.route.RouteMatch
import sabina.route.RouteMatcher
import sabina.Router.Handler
import sabina.Router.BiHandler
import sabina.Router.VoidHandler
import sabina.Router.BiVoidHandler

import static sabina.HttpMethod.*
import static sabina.Route.DEFAULT_ACCEPT_TYPE

/**
 * This test only checks that matcher is called with the correct parameters and that callback
 * wrappers return proper values.
 *
 * <p>It is mostly to have a nice coverage, as this is not likely to fail.
 *
 * @author jam
 */
@Test public class RouterTest {
    private Consumer<Route> routeCallback
    private Consumer<Fault<? extends Exception>> faultCallback

    private Router testRouter = new Router () {
        @Override public RouteMatcher getMatcher () {
            return new RouteMatcher () {
                @Override public void processRoute (Route target) {
                    routeCallback.accept (target)
                }

                @Override public <T extends Exception> void processFault (Fault<T> handler) {
                    faultCallback.accept (handler)
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
        }
    }

    public void "all wrap void methods return the proper value in their generated callbacks" () {
        Route route = new Route (GET, { "" } as Handler)
        Request request = new Request (new RouteMatch(route, "/"), null, null)

        testRouter.wrap ({} as VoidHandler).apply (request).equals ("")

        BiVoidHandler voidHandler = { req, res -> req.toString() } as BiVoidHandler
        testRouter.wrap (voidHandler).apply (request).equals ("")

        BiHandler handler = { req, res -> "result" } as BiHandler
        testRouter.wrap (handler).apply (request).equals ("result")

    }

    public void "adding after filters process the correct routes" () {
        checkMethod ({ it.after ({} as VoidHandler) }, AFTER)
        checkMethod ({ it.after ("/", {} as VoidHandler) }, AFTER, "/")
        checkMethod ({ it.after ("/", "text", {} as VoidHandler) }, AFTER, "/", "text")

        checkMethod ({ it.after ({} as BiVoidHandler) }, AFTER)
        checkMethod ({ it.after ("/", {} as BiVoidHandler) }, AFTER, "/")
        checkMethod ({ it.after ("/", "text", {} as BiVoidHandler) }, AFTER, "/", "text")
    }

    public void "adding before filters process the correct routes" () {
        checkMethod ({ it.before ({} as VoidHandler) }, BEFORE)
        checkMethod ({ it.before ("/", {} as VoidHandler) }, BEFORE, "/")
        checkMethod ({ it.before ("/", "text", {} as VoidHandler) }, BEFORE, "/", "text")

        checkMethod ({ it.before ({} as BiVoidHandler) }, BEFORE)
        checkMethod ({ it.before ("/", {} as BiVoidHandler) }, BEFORE, "/")
        checkMethod ({ it.before ("/", "text", {} as BiVoidHandler) }, BEFORE, "/", "text")
    }

    public void "adding delete routes process the correct routes" () {
        checkMethod ({ it.delete ("/", {} as Handler) }, DELETE, "/")
        checkMethod ({ it.delete ("/", {} as VoidHandler) }, DELETE, "/")
        checkMethod ({ it.delete ("/", "text", {} as Handler) }, DELETE, "/", "text")
        checkMethod ({ it.delete ("/", "text", {} as VoidHandler) }, DELETE, "/", "text")

        checkMethod ({ it.delete ("/", {} as BiHandler) }, DELETE, "/")
        checkMethod ({ it.delete ("/", {} as BiVoidHandler) }, DELETE, "/")
        checkMethod ({ it.delete ("/", "text", {} as BiHandler) }, DELETE, "/", "text")
        checkMethod ({ it.delete ("/", "text", {} as BiVoidHandler) }, DELETE, "/", "text")
    }

    public void "adding get routes process the correct routes" () {
        checkMethod ({ it.get ("/", {} as Handler) }, GET, "/")
        checkMethod ({ it.get ("/", {} as VoidHandler) }, GET, "/")
        checkMethod ({ it.get ("/", "text", {} as Handler) }, GET, "/", "text")
        checkMethod ({ it.get ("/", "text", {} as VoidHandler) }, GET, "/", "text")

        checkMethod ({ it.get ("/", {} as BiHandler) }, GET, "/")
        checkMethod ({ it.get ("/", {} as BiVoidHandler) }, GET, "/")
        checkMethod ({ it.get ("/", "text", {} as BiHandler) }, GET, "/", "text")
        checkMethod ({ it.get ("/", "text", {} as BiVoidHandler) }, GET, "/", "text")
    }

    public void "adding head routes process the correct routes" () {
        checkMethod ({ it.head ("/", {} as Handler) }, HEAD, "/")
        checkMethod ({ it.head ("/", {} as VoidHandler) }, HEAD, "/")
        checkMethod ({ it.head ("/", "text", {} as Handler) }, HEAD, "/", "text")
        checkMethod ({ it.head ("/", "text", {} as VoidHandler) }, HEAD, "/", "text")

        checkMethod ({ it.head ("/", {} as BiHandler) }, HEAD, "/")
        checkMethod ({ it.head ("/", {} as BiVoidHandler) }, HEAD, "/")
        checkMethod ({ it.head ("/", "text", {} as BiHandler) }, HEAD, "/", "text")
        checkMethod ({ it.head ("/", "text", {} as BiVoidHandler) }, HEAD, "/", "text")
    }

    public void "adding options routes process the correct routes" () {
        checkMethod ({ it.options ("/", {} as Handler) }, OPTIONS, "/")
        checkMethod ({ it.options ("/", {} as VoidHandler) }, OPTIONS, "/")
        checkMethod ({ it.options ("/", "text", {} as Handler) }, OPTIONS, "/", "text")
        checkMethod ({ it.options ("/", "text", {} as VoidHandler) }, OPTIONS, "/", "text")

        checkMethod ({ it.options ("/", {} as BiHandler) }, OPTIONS, "/")
        checkMethod ({ it.options ("/", {} as BiVoidHandler) }, OPTIONS, "/")
        checkMethod ({ it.options ("/", "text", {} as BiHandler) }, OPTIONS, "/", "text")
        checkMethod ({ it.options ("/", "text", {} as BiVoidHandler) }, OPTIONS, "/", "text")
    }

    public void "adding patch routes process the correct routes" () {
        checkMethod ({ it.patch ("/", {} as Handler) }, PATCH, "/")
        checkMethod ({ it.patch ("/", {} as VoidHandler) }, PATCH, "/")
        checkMethod ({ it.patch ("/", "text", {} as Handler) }, PATCH, "/", "text")
        checkMethod ({ it.patch ("/", "text", {} as VoidHandler) }, PATCH, "/", "text")

        checkMethod ({ it.patch ("/", {} as BiHandler) }, PATCH, "/")
        checkMethod ({ it.patch ("/", {} as BiVoidHandler) }, PATCH, "/")
        checkMethod ({ it.patch ("/", "text", {} as BiHandler) }, PATCH, "/", "text")
        checkMethod ({ it.patch ("/", "text", {} as BiVoidHandler) }, PATCH, "/", "text")
    }

    public void "adding post routes process the correct routes" () {
        checkMethod ({ it.post ("/", {} as Handler) }, POST, "/")
        checkMethod ({ it.post ("/", {} as VoidHandler) }, POST, "/")
        checkMethod ({ it.post ("/", "text", {} as Handler) }, POST, "/", "text")
        checkMethod ({ it.post ("/", "text", {} as VoidHandler) }, POST, "/", "text")

        checkMethod ({ it.post ("/", {} as BiHandler) }, POST, "/")
        checkMethod ({ it.post ("/", {} as BiVoidHandler) }, POST, "/")
        checkMethod ({ it.post ("/", "text", {} as BiHandler) }, POST, "/", "text")
        checkMethod ({ it.post ("/", "text", {} as BiVoidHandler) }, POST, "/", "text")
    }

    public void "adding put routes process the correct routes" () {
        checkMethod ({ it.put ("/", {} as Handler) }, PUT, "/")
        checkMethod ({ it.put ("/", {} as VoidHandler) }, PUT, "/")
        checkMethod ({ it.put ("/", "text", {} as Handler) }, PUT, "/", "text")
        checkMethod ({ it.put ("/", "text", {} as VoidHandler) }, PUT, "/", "text")

        checkMethod ({ it.put ("/", {} as BiHandler) }, PUT, "/")
        checkMethod ({ it.put ("/", {} as BiVoidHandler) }, PUT, "/")
        checkMethod ({ it.put ("/", "text", {} as BiHandler) }, PUT, "/", "text")
        checkMethod ({ it.put ("/", "text", {} as BiVoidHandler) }, PUT, "/", "text")
    }

    public void "adding trace routes process the correct routes" () {
        checkMethod ({ it.trace ("/", {} as Handler) }, TRACE, "/")
        checkMethod ({ it.trace ("/", {} as VoidHandler) }, TRACE, "/")
        checkMethod ({ it.trace ("/", "text", {} as Handler) }, TRACE, "/", "text")
        checkMethod ({ it.trace ("/", "text", {} as VoidHandler) }, TRACE, "/", "text")

        checkMethod ({ it.trace ("/", {} as BiHandler) }, TRACE, "/")
        checkMethod ({ it.trace ("/", {} as BiVoidHandler) }, TRACE, "/")
        checkMethod ({ it.trace ("/", "text", {} as BiHandler) }, TRACE, "/", "text")
        checkMethod ({ it.trace ("/", "text", {} as BiVoidHandler) }, TRACE, "/", "text")
    }

    public void "adding an exception handler passes the correct value to the matcher" () {
        faultCallback = {
            assert it.exception.equals (RuntimeException)
            assert it.handler != null
        }

        testRouter.exception (RuntimeException, {} as BiConsumer<? extends Exception, Request>)
    }

    public void "adding a handler without an exception is allowed" () {
        faultCallback = {
            assert it.exception == null
            assert it.handler != null
        }

        testRouter.exception (null, {} as BiConsumer<? extends Exception, Request>)
    }

    @Test (expectedExceptions = IllegalArgumentException)
    public void "registering a 'null' exception handler fails" () {
        testRouter.exception (RuntimeException, null)
    }

    private void checkMethod (
        Consumer<Router> methodLambda,
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

        methodLambda.accept (testRouter)
    }
}
