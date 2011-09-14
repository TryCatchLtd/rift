package uk.co.wireweb.rift.core.internal;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.wireweb.rift.core.spi.PluginContext;
import uk.co.wireweb.rift.core.spi.annotation.Webpage;

/**
 * @author Daniel Johansson
 *
 * @since 13 Jun 2011
 */
public class WebpageClassHandler implements ClassHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebpageClassHandler.class);

    private Map<String, Class<?>> pages = new HashMap<String, Class<?>>();

    @Override
    public void handle(final PluginContext pluginContext, final Class<?> clazz) {
        if (clazz.isAnnotationPresent(Webpage.class)) {
            final Webpage webpageAnnotation = clazz.getAnnotation(Webpage.class);

            for (final String path : webpageAnnotation.serves()) {
                LOGGER.trace("[Rift] Found @Webpage [{}]", clazz.getSimpleName());

                final Class<?> existingClass = this.pages.put(path, clazz);

                if (existingClass != null) {
                    LOGGER.warn("[Rift] Found multiple @Webpage classes with a serve path of [{}]", path);
                }
            }
        }
    }

    public Map<String, Class<?>> getPages() {
        return this.pages;
    }
}
