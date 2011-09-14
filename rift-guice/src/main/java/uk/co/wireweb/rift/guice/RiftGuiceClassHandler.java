package uk.co.wireweb.rift.guice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.wireweb.rift.core.internal.ClassHandler;
import uk.co.wireweb.rift.core.spi.PluginContext;

import com.google.inject.Module;

/**
 * @author Daniel Johansson
 *
 * @since 13 Apr 2011
 */
public class RiftGuiceClassHandler implements ClassHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RiftGuiceClassHandler.class);

    private final Set<RiftGuiceModule> riftGuiceModules = new HashSet<RiftGuiceModule>();

    @Override
    public void handle(final PluginContext pluginContext, final Class<?> clazz) {
        if (RiftGuiceModule.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
            LOGGER.trace("[Rift-Guice] Added [] to the list of RiftGuiceModule classes", clazz.getName());

            try {
                final RiftGuiceModule riftGuiceModule = (RiftGuiceModule) clazz.newInstance();
                this.riftGuiceModules.add(riftGuiceModule);
            } catch (Throwable exception) {

            }
        }
    }

    public List<Module> getModules(final Map<String, String> properties) {
        final List<Module> modules = new ArrayList<Module>();

        for (final RiftGuiceModule riftGuiceModule : this.riftGuiceModules) {
            if (riftGuiceModule.getModules(properties) != null) {
                modules.addAll(riftGuiceModule.getModules(properties));
            }
        }

        return modules;
    }
}
