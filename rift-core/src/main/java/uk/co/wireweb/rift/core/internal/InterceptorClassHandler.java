package uk.co.wireweb.rift.core.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.wireweb.rift.core.spi.PluginContext;
import uk.co.wireweb.rift.core.spi.annotation.Interceptor;

/**
 * @author Daniel Johansson
 *
 * @since 14 Jun 2011
 */
public class InterceptorClassHandler implements ClassHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterceptorClassHandler.class);

    private Map<String, List<Class<?>>> interceptors = new HashMap<String, List<Class<?>>>();

    @Override
    public void handle(PluginContext pluginContext, Class<?> clazz) {
        if (clazz.isAnnotationPresent(Interceptor.class)) {
            final Interceptor interceptorAnnotation = clazz.getAnnotation(Interceptor.class);

            List<Class<?>> interceptorsList = this.interceptors.get(interceptorAnnotation.path());

            if (interceptorsList == null) {
                interceptorsList = new ArrayList<Class<?>>();
            }

            interceptorsList.add(clazz);

            LOGGER.trace("[Rift] Found @Interceptor [{}]", clazz.getSimpleName());
            this.interceptors.put(interceptorAnnotation.path(), interceptorsList);
        }
    }

    public Map<String, List<Class<?>>> getInterceptors() {
        return this.interceptors;
    }

    public void setInterceptors(Map<String, List<Class<?>>> interceptors) {
        this.interceptors = interceptors;
    }
}
