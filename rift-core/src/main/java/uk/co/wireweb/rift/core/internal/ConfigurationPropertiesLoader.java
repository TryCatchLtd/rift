package uk.co.wireweb.rift.core.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Johansson
 *
 * @since 14 Jun 2011
 */
public class ConfigurationPropertiesLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationPropertiesLoader.class);

    private static final String INIT_PARAM_CONFIG_FILES = "uk.co.wireweb.rift.configuration.files";

    private final Map<String, String> properties = new HashMap<String, String>();

    public Map<String, String> loadProperties(final ServletContext servletContext) {
        if (this.properties.isEmpty()) {
            final List<String> files = this.getFilesToLoad(servletContext.getInitParameter(INIT_PARAM_CONFIG_FILES));

            if (!files.isEmpty()) {
                for (final String fileName : files) {
                    LOGGER.trace("[Rift] Loading properties from [{}]", fileName);

                    final InputStream inputStream = ConfigurationPropertiesLoader.class.getClassLoader().getResourceAsStream(fileName);

                    if (inputStream != null) {
                        try {
                            final Properties properties = new Properties();
                            properties.load(inputStream);

                            if (!properties.isEmpty()) {
                                if (LOGGER.isTraceEnabled()) {
                                    LOGGER.trace("[Rift] Loading properties from file [{}]", fileName);
                                }

                                for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
                                    final String key = (String) entry.getKey();
                                    final String value = (String) entry.getValue();

                                    final String previousValue = this.properties.put(key, value);
                                    LOGGER.trace("[Rift] Loaded property [{}={}]", key, value);

                                    if (previousValue != null) {
                                        LOGGER.warn("[Rift] Property [{}] with value [{}] has been overridden with value [" + value + "], this usually indicates that you have multiple files with the same property keys", key, previousValue);
                                    }
                                }
                            }
                        } catch (Exception exception) {
                            LOGGER.error("[Rift] Exception caught loading properties file", exception);
                        } finally {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    LOGGER.error("[Rift] Failed closing inputstream for properties file [{}]", fileName);
                                }
                            }
                        }
                    }
                }
            }
        }

        return this.properties;
    }

    private List<String> getFilesToLoad(final String fileNames) {
        if (fileNames != null) {
            if (fileNames.contains(",")) {
                return Arrays.asList(fileNames.split(","));
            } else if ((fileNames != null) && (fileNames.length() > 0)) {
                return Arrays.asList(new String[] { fileNames });
            }
        }

        return new ArrayList<String>();
    }
}
