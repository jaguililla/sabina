package sabina.backend.jetty;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.session.SessionHandler;
import sabina.servlet.MatcherFilter;

/**
 * Simple Jetty Handler.
 *
 * @author Per Wendel
 */
final class JettyHandler extends SessionHandler {
    private MatcherFilter filter;

    public JettyHandler (MatcherFilter filter) {
        this.filter = filter;
    }

    @Override public void doHandle (
        String target,
        Request baseRequest,
        HttpServletRequest request,
        HttpServletResponse response)
        throws IOException, ServletException {

        filter.doFilter (request, response, null);
        baseRequest.setHandled (filter.handled);
    }
}
