package uk.co.wireweb.rift.core.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.wireweb.rift.core.spi.PluginContext;
import uk.co.wireweb.rift.core.spi.WebApplication;

/**
 * @author Daniel Johansson
 *
 * @since 14 Jun 2011
 */
public class WebApplicationClassHandler implements ClassHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebApplicationClassHandler.class);

    @SuppressWarnings("unchecked")
    @Override
    public void handle(final PluginContext pluginContext, final Class<?> clazz) {
        if (WebApplication.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
            if (pluginContext.getWebApplicationClass() != null) {
                LOGGER.warn("[Rift] Found multiple classes implementing WebApplication, there can only be one WebApplication on the classpath. The previously registered class [{}] will be kept.", pluginContext.getWebApplicationClass().getName());
            } else {
                LOGGER.trace("[Rift] Found WebApplication implementation class [{}]", clazz.getName());
                pluginContext.setWebApplicationClass((Class<WebApplication>) clazz);
            }
        }
    }
}
