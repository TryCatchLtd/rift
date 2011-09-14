package uk.co.wireweb.rift.core.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import uk.co.wireweb.rift.core.internal.ClassHandler;
import uk.co.wireweb.rift.core.internal.DefaultInstantiator;

/**
 * @author Daniel Johansson
 *
 * @since 13 Jun 2011
 */
public final class PluginContext {

    private final List<ClassHandler> classHandlers = new ArrayList<ClassHandler>();

    private final List<RequestHandler> requestHandlers = new ArrayList<RequestHandler>();

    private final List<InterceptHandler> interceptHandlers = new ArrayList<InterceptHandler>();

    private Class<WebApplication> webApplicationClass;

    private Instantiator instantiator = new DefaultInstantiator();

    private final Map<String, String> parameters;

    private final ServletContext servletContext;

    private final Map<String, String> properties;

    public PluginContext(final Map<String, String> parameters, final Map<String, String> properties, final ServletContext servletContext) {
        this.parameters = parameters;
        this.servletContext = servletContext;
        this.properties = properties;
    }

    public void registerClassHandler(final ClassHandler classHandler) {
        this.classHandlers.add(classHandler);
    }

    public void registerRequestHandler(final RequestHandler requestHandler) {
        this.requestHandlers.add(requestHandler);
    }

    public void registerInterceptHandler(final InterceptHandler interceptHandler) {
        this.interceptHandlers.add(interceptHandler);
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    /**
    * Get a init parameters value.
    * 
    * @param key the key of the parameter.
    * @return the value assigned to the specified key and null if not found.
    */
    public String getParameter(final String key) {
        if (this.parameters != null) {
            return this.parameters.get(key);
        }

        return null;
    }

    public List<ClassHandler> getClassHandlers() {
        return this.classHandlers;
    }

    public List<RequestHandler> getRequestHandlers() {
        return this.requestHandlers;
    }

    public List<InterceptHandler> getInterceptHandlers() {
        return this.interceptHandlers;
    }

    public Instantiator getInstantiator() {
        return this.instantiator;
    }

    public void setInstantiator(Instantiator instantiator) {
        this.instantiator = instantiator;
    }

    public Class<WebApplication> getWebApplicationClass() {
        return this.webApplicationClass;
    }

    public void setWebApplicationClass(Class<WebApplication> webApplicationClass) {
        this.webApplicationClass = webApplicationClass;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }
}
