package uk.co.wireweb.rift.guice;

import uk.co.wireweb.rift.core.spi.Plugin;
import uk.co.wireweb.rift.core.spi.PluginContext;

/**
 * @author Daniel Johansson
 *
 * @since 13 Apr 2011
 */
public class RiftGuicePlugin implements Plugin {

    @Override
    public void init(final PluginContext pluginContext) {
        final RiftGuiceClassHandler riftGuiceClassHandler = new RiftGuiceClassHandler();
        pluginContext.registerClassHandler(riftGuiceClassHandler);
        pluginContext.setInstantiator(new RiftGuiceInstantiator(riftGuiceClassHandler, pluginContext.getProperties()));
    }

    @Override
    public void destroy() {

    }
}
