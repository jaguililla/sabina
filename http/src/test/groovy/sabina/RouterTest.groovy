package sabina

import java.util.function.BiConsumer
import java.util.function.Consumer

import org.testng.annotations.Test
import sabina.route.RouteMatch
import sabina.route.RouteMatcher
import sabina.Router.Handler
import sabina.Router.VoidHandler

import static sabina.HttpMethod.*

/**
 * This test only checks that matcher is called with the correct parameters and that callback
 * wrappers return proper values.
 *
 * <p>It is mostly to have a nice coverage, as this is not likely to fail.
 *
 * @author jam
 */
@Test public class RouterTest {
    private BiConsumer<Class<?>, BiConsumer<?, Request>> faultCallback

    private Router testRouter = new Router () {
        @Override public RouteMatcher getMatcher () {
            return new RouteMatcher () {
                @Override public void processRoute (Route target) {
                    throw new UnsupportedOperationException ()
                }

                @Override public <T extends Exception> void processFault (
                    Class<T> fault, BiConsumer<? extends Exception, Request> handler) {
                    faultCallback.accept (fault, handler)
                }

                @Override public RouteMatch findTarget (
                    HttpMethod httpMethod, String path, String acceptType) {

                    throw new UnsupportedOperationException ()
                }

                @Override public List<RouteMatch> findTargets (
                    HttpMethod httpMethod, String path, String acceptType) {

                    throw new UnsupportedOperationException ()
                }

                @Override public <T extends Exception> BiConsumer<T, Request> findHandler(
                    Class<T> exceptionClass) {

                    throw new UnsupportedOperationException ()
                }
            }
        }
    }

    public void "all wrap void methods return the proper value in their generated callbacks" () {
        Route route = new Route (GET, { "" } as Handler)
        Request request = new Request (new RouteMatch(route, "/"), null, null)

        testRouter.wrap ({} as VoidHandler).apply (request).equals ("")
    }

    public void "adding an exception handler passes the correct value to the matcher" () {
        faultCallback = { exception, handler ->
            assert exception.equals (RuntimeException)
            assert handler != null
        }

        testRouter.exception (RuntimeException, {} as BiConsumer<RuntimeException, Request>)
    }

    public void "adding a handler without an exception is allowed" () {
        faultCallback = { exception, handler ->
            assert exception == null
            assert handler != null
        }

        testRouter.exception (null, {} as BiConsumer<? extends Exception, Request>)
    }

    @Test (expectedExceptions = IllegalArgumentException)
    public void "registering a 'null' exception handler fails" () {
        testRouter.exception (RuntimeException, null)
    }
}
