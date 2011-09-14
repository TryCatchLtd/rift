package uk.co.wireweb.rift.core;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.wireweb.rift.core.internal.ClasspathScanner;
import uk.co.wireweb.rift.core.internal.Configuration;
import uk.co.wireweb.rift.core.internal.ConfigurationPropertiesLoader;
import uk.co.wireweb.rift.core.internal.InterceptorClassHandler;
import uk.co.wireweb.rift.core.internal.InterceptorRequestHandler;
import uk.co.wireweb.rift.core.internal.WebApplicationClassHandler;
import uk.co.wireweb.rift.core.internal.WebpageClassHandler;
import uk.co.wireweb.rift.core.internal.WebpageRequestHandler;
import uk.co.wireweb.rift.core.spi.Plugin;
import uk.co.wireweb.rift.core.spi.PluginContext;

/**
 * @author Daniel Johansson
 *
 * @since 12 Jun 2011
 */
public class RiftServletContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RiftServletContextListener.class);

    private final List<Plugin> plugins = new ArrayList<Plugin>();

    private String version;

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        try {
            final long start = System.currentTimeMillis();
            LOGGER.info("[Rift] Welcome to Rift " + this.getVersion());

            boolean debug = this.isRiftDebugOnClasspath();

            if (debug && LOGGER.isWarnEnabled()) {
                LOGGER.warn("[Rift] Found rift-debug on the classpath, if this application is running in production consider removing the rift-debug jar from the classpath");
            }

            // Create a classpath scanner
            final ClasspathScanner classpathScanner = new ClasspathScanner(servletContextEvent.getServletContext());

            // Init plugins
            final PluginContext pluginContext = this.initPlugins(classpathScanner);

            // Run classhandlers
            classpathScanner.parseForAndHandleClasses(pluginContext);

            // Create the configuration ???
            final Configuration configuration = new Configuration(debug, pluginContext);
            servletContextEvent.getServletContext().setAttribute("internal-rift-configuration", configuration);

            LOGGER.info("[Rift] Initialised in [{}ms]", (System.currentTimeMillis() - start));
        } catch (Exception exception) {
            LOGGER.error("[Rift] Exception caught while loading Rift", exception);
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        // Destroy plugins
        this.destroyPlugins();
    }

    private PluginContext initPlugins(final ClasspathScanner classpathScanner) {
        final long start = System.currentTimeMillis();
        LOGGER.trace("Loading Rift plugins");

        final List<Class<Plugin>> pluginClasses = classpathScanner.getPlugins();
        final PluginContext pluginContext = this.createPluginContext(classpathScanner.getServletContext());
        final WebpageClassHandler webpageClassHandler = new WebpageClassHandler();
        final WebpageRequestHandler webpageRequestHandler = new WebpageRequestHandler(webpageClassHandler);
        final InterceptorClassHandler interceptorClassHandler = new InterceptorClassHandler();
        final InterceptorRequestHandler interceptorRequestHandler = new InterceptorRequestHandler(interceptorClassHandler);

        pluginContext.registerClassHandler(webpageClassHandler);
        pluginContext.registerClassHandler(new WebApplicationClassHandler());
        pluginContext.registerRequestHandler(webpageRequestHandler);

        pluginContext.registerClassHandler(interceptorClassHandler);
        pluginContext.registerInterceptHandler(interceptorRequestHandler);

        for (final Class<Plugin> pluginClass : pluginClasses) {
            try {
                final Plugin plugin = pluginClass.newInstance();
                plugin.init(pluginContext);
                this.plugins.add(plugin);
            } catch (Exception exception) {
                LOGGER.error("[Rift] Could not load plugin [" + pluginClass.getName() + "]", exception);
            }
        }

        classpathScanner.registerClassHandlers(pluginContext.getClassHandlers());

        LOGGER.trace("Loaded [{}] Rift plugins in [{}ms]", this.plugins.size(), (System.currentTimeMillis() - start));

        return pluginContext;
    }

    private void destroyPlugins() {
        for (final Plugin plugin : this.plugins) {
            plugin.destroy();
        }

        this.plugins.clear();
    }

    @SuppressWarnings("unchecked")
    private PluginContext createPluginContext(final ServletContext servletContext) {
        final Map<String, String> properties = new ConfigurationPropertiesLoader().loadProperties(servletContext);
        final Enumeration<String> enumeration = servletContext.getInitParameterNames();
        final Map<String, String> parameters = new HashMap<String, String>();

        while (enumeration.hasMoreElements()) {
            final String key = enumeration.nextElement();
            final String value = servletContext.getInitParameter(key);

            parameters.put(key, value);
        }

        return new PluginContext(parameters, properties, servletContext);
    }

    public String getVersion() {
        if (this.version == null) {
            try {
                final Properties properties = new Properties();
                properties.load(Rift.class.getResourceAsStream("/rift.properties"));

                this.version = properties.getProperty("rift.version");
            } catch (Throwable throwable) {
                this.version = "unknown";
            }
        }

        return this.version;
    }

    private boolean isRiftDebugOnClasspath() {
        try {
            Class.forName(Rift.RIFT_DEBUG_CLASS, false, this.getClass().getClassLoader());

            return true;
        } catch (Exception exception) {
            return false;
        }
    }

}
