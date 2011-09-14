package uk.co.wireweb.rift.core.internal;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import uk.co.wireweb.rift.core.spi.Instantiator;
import uk.co.wireweb.rift.core.spi.InterceptHandler;
import uk.co.wireweb.rift.core.spi.PluginContext;
import uk.co.wireweb.rift.core.spi.RequestHandler;
import uk.co.wireweb.rift.core.spi.WebApplication;

/**
 * @author Daniel Johansson
 *
 * @since 13 Jun 2011
 */
public final class Configuration {

    private final List<InterceptHandler> interceptHandlers;

    private final List<RequestHandler> requestHandlers;

    private final Instantiator instantiator;

    private final WebApplication webApplication;

    private final ServletContext servletContext;

    private final Map<String, String> properties;

    private final boolean debug;

    public Configuration(final boolean debug, final PluginContext pluginContext) throws Exception {
        this.debug = debug;
        this.interceptHandlers = pluginContext.getInterceptHandlers();
        this.requestHandlers = pluginContext.getRequestHandlers();
        this.instantiator = pluginContext.getInstantiator();
        this.webApplication = this.instantiator.getInstance(pluginContext.getWebApplicationClass());
        this.servletContext = pluginContext.getServletContext();
        this.properties = pluginContext.getProperties();
    }

    public List<InterceptHandler> getInterceptHandlers() {
        return this.interceptHandlers;
    }

    public List<RequestHandler> getRequestHandlers() {
        return this.requestHandlers;
    }

    public Instantiator getInstantiator() {
        return this.instantiator;
    }

    public WebApplication getWebApplication() {
        return this.webApplication;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }
}
