package uk.co.wireweb.rift.core.spi;


/**
 * @author Daniel Johansson
 *
 * @since 12 Jun 2011
 */
public interface Plugin {

    public void init(final PluginContext pluginContext);

    public void destroy();
}
