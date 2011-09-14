package uk.co.wireweb.rift.guice;

import java.util.Map;
import java.util.Set;

import com.google.inject.Module;

/**
 * @author Daniel Johansson
 *
 * @since 14 Jun 2011
 */
public interface RiftGuiceModule {

    public Set<Module> getModules(final Map<String, String> properties);

}
