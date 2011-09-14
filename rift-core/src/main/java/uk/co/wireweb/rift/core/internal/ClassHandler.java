package uk.co.wireweb.rift.core.internal;

import uk.co.wireweb.rift.core.spi.PluginContext;

/**
 * @author Daniel Johansson
 *
 * @since 13 Jun 2011
 */
public interface ClassHandler {

    public void handle(final PluginContext pluginContext, final Class<?> clazz);
}
