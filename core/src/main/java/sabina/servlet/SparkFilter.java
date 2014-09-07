/*
 * Copyright © 2011 Per Wendel. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package sabina.servlet;

import static com.google.common.io.CharStreams.copy;
import static java.util.logging.Level.FINE;
import static java.util.logging.Logger.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.core.io.AbstractFileResolvingResource;
import org.springframework.core.io.ClassPathResource;
import sabina.Sabina;
import sabina.route.RouteMatcherFactory;
import sabina.webserver.MatcherFilter;

/**
 * Filter that can be configured to be used in a web.xml file.
 * Needs the init parameter 'applicationClass' set to the application class where
 * the adding of routes should be made.
 *
 * @author Per Wendel
 */
public abstract class SparkFilter implements Filter {
    private static final Logger LOG = getLogger (SparkFilter.class.getName ());

    private static final String SLASH_WILDCARD = "/*";
    private static final String SLASH = "/";
    private static final String FILTER_MAPPING_PARAM = "filterMappingUrlPattern";

    static String getRelativePath (HttpServletRequest request, String filterPath) {
        String path = request.getRequestURI ();
        path = path.substring (request.getContextPath ().length ());

        if (path.length () > 0) {
            path = path.substring (1);
        }

        if (!path.startsWith (filterPath) && filterPath.equals (path + SLASH)) {
            path += SLASH;
        }

        if (path.startsWith (filterPath)) {
            path = path.substring (filterPath.length ());
        }

        if (!path.startsWith (SLASH)) {
            path = SLASH + path;
        }

        return path;
    }

    static String getFilterPath (FilterConfig config) {
        String result = config.getInitParameter (FILTER_MAPPING_PARAM);
        if (result == null || result.equals (SLASH_WILDCARD)) {
            return "";
        }
        else if (!result.startsWith (SLASH) || !result.endsWith (SLASH_WILDCARD)) {
            throw new RuntimeException ("The " + FILTER_MAPPING_PARAM
                + " must start with \"/\" and end with \"/*\". It's: " + result); // NOSONAR
        }
        return result.substring (1, result.length () - 1);
    }

    public static final String APPLICATION_CLASS_PARAM = "applicationClass";

    private static List<AbstractResourceHandler> staticResourceHandlers = null;

    private static boolean staticResourcesSet = false;
    private static boolean externalStaticResourcesSet = false;

    private String filterPath;
    private MatcherFilter matcherFilter;

    @Override
    public void init (FilterConfig filterConfig) throws ServletException {
        Sabina.runFromServlet ();

        setup (filterConfig);

        filterPath = getFilterPath (filterConfig);
        matcherFilter = new MatcherFilter (RouteMatcherFactory.get (), true, false);
    }

    public abstract void setup (FilterConfig aFilterConfig);

    @Override
    public void doFilter (ServletRequest request, ServletResponse response, FilterChain chain)
        throws
        IOException,
        ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request; // NOSONAR

        final String relativePath = getRelativePath (httpRequest, filterPath);

        if (LOG.isLoggable (FINE))
            LOG.fine (relativePath);

        HttpServletRequestWrapper requestWrapper =
            new HttpServletRequestWrapper (httpRequest) {
                @Override
                public String getRequestURI () {
                    return relativePath;
                }
            };

        // handle static resources
        if (staticResourceHandlers != null) {
            for (AbstractResourceHandler staticResourceHandler : staticResourceHandlers) {
                AbstractFileResolvingResource resource =
                    staticResourceHandler.getResource (httpRequest);
                if (resource != null && resource.isReadable ()) {
                    InputStream input = resource.getInputStream ();
                    copy (new InputStreamReader (input), response.getWriter ());
                    return;
                }
            }
        }

        matcherFilter.doFilter (requestWrapper, response, chain);
    }

    /**
     * Configures location for static resources
     *
     * @param folder the location
     */
    public static void configureStaticResources (String folder) {
        if (!staticResourcesSet) {
            if (folder != null) {
                try {
                    ClassPathResource resource = new ClassPathResource (folder);
                    if (resource.getFile ().isDirectory ()) {
                        if (staticResourceHandlers == null) {
                            staticResourceHandlers = new ArrayList<> ();
                        }
                        staticResourceHandlers
                            .add (new ClassPathResourceHandler (folder, "index.html"));
                        LOG.info ("StaticResourceHandler configured with folder = " + folder);
                    }
                    else {
                        LOG.severe ("Static resource location must be a folder");
                    }
                }
                catch (IOException e) {
                    LOG.severe ("Error when creating StaticResourceHandler" + e.getMessage ());
                }
            }
            staticResourcesSet = true;
        }
    }

    /**
     * Configures location for static resources
     *
     * @param folder the location
     */
    public static void configureExternalStaticResources (String folder) {
        if (!externalStaticResourcesSet) {
            if (folder != null) {
                try {
                    ExternalResource resource = new ExternalResource (folder);
                    if (resource.getFile ().isDirectory ()) {
                        if (staticResourceHandlers == null) {
                            staticResourceHandlers = new ArrayList<> ();
                        }
                        staticResourceHandlers
                            .add (new ExternalResourceHandler (folder, "index.html"));
                        LOG.info ("External StaticResourceHandler configured with folder = "
                            + folder);
                    }
                    else {
                        LOG.severe ("External Static resource location must be a folder");
                    }
                }
                catch (IOException e) {
                    LOG.severe ("Error when creating external StaticResourceHandler"
                        + e.getMessage ());
                }
            }
            externalStaticResourcesSet = true;
        }
    }

    @Override
    public void destroy () {
        // ignore
    }
}
