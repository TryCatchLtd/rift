package uk.co.wireweb.rift.core.spi;

import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Daniel Johansson
 *
 * @since 13 Jun 2011
 */
public class RequestContext {

    private final WebApplication webApplication;

    private final Instantiator instantiator;

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final ServletContext servletContext;

    private final Map<String, String> properties;

    public RequestContext(final Map<String, String> properties, final WebApplication webApplication, final Instantiator instantiator, final HttpServletRequest request, final HttpServletResponse response, final ServletContext servletContext) {
        this.properties = properties;
        this.webApplication = webApplication;
        this.instantiator = instantiator;
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
    }

    public Instantiator getInstantiator() {
        return this.instantiator;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public WebApplication getWebApplication() {
        return this.webApplication;
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }
}
