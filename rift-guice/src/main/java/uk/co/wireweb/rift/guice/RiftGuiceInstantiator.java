package uk.co.wireweb.rift.guice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.wireweb.rift.core.spi.Instantiator;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;

/**
 * @author Daniel Johansson
 *
 * @since 13 Apr 2011
 */
public class RiftGuiceInstantiator implements Instantiator {

    private Injector injector;

    private final RiftGuiceClassHandler riftGuiceClassHandler;

    private final Map<String, String> properties;

    public RiftGuiceInstantiator(final RiftGuiceClassHandler riftGuiceClassHandler, final Map<String, String> properties) {
        this.riftGuiceClassHandler = riftGuiceClassHandler;
        this.properties = properties;
    }

    @Override
    public <T> T getInstance(Class<T> clazz) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        if (this.injector == null) {
            final List<Module> modules = new ArrayList<Module>();

            if (this.riftGuiceClassHandler.getModules(this.properties) != null) {
                modules.addAll(this.riftGuiceClassHandler.getModules(this.properties));
            }

            modules.add(new AbstractModule() {
                @Override
                protected void configure() {
                    this.bindListener(Matchers.any(), new RiftGuicePropertyTypeListener(RiftGuiceInstantiator.this.properties));
                }
            });

            this.injector = Guice.createInjector(modules);
        }

        return this.injector.getInstance(clazz);
    }
}
