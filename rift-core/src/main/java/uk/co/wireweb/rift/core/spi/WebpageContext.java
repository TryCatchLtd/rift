package uk.co.wireweb.rift.core.spi;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Daniel Johansson
 *
 * @since 12 Feb 2011
 */
public class WebpageContext {

    private static final String SESSION_ATTRIBUTE_IDENTITY = "riftIdentity";

    private final ServletContext servletContext;

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final Map<String, String> properties;

    private final WebApplication webApplication;

    public WebpageContext(final ServletContext servletContext, final HttpServletRequest request, final HttpServletResponse response, final WebApplication webApplication, final Map<String, String> properties) {
        this.servletContext = servletContext;
        this.request = request;
        this.response = response;
        this.webApplication = webApplication;
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    public <T extends WebApplication> T getWebApplication(final Class<T> webApplicationClass) {
        return (T) this.webApplication;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public Identity getIdentity() {
        final HttpSession session = this.request.getSession();
        Identity identity = (Identity) session.getAttribute(SESSION_ATTRIBUTE_IDENTITY);

        if (identity == null) {
            identity = new Identity();
            session.setAttribute(SESSION_ATTRIBUTE_IDENTITY, identity);
        }

        return identity;
    }

    public void invalidateIdentity() {
        final HttpSession session = this.request.getSession();
        session.setAttribute(SESSION_ATTRIBUTE_IDENTITY, null);
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public String getProperty(final String key) {
        return this.properties.get(key);
    }
}
