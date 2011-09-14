package uk.co.wireweb.rift.core.spi;

import java.util.Map;

/**
 * @author Daniel Johansson
 *
 * @since 13 Jun 2011
 */
public interface WebApplication {

    public void init(final Map<String, String> properties);

    public void destroy();
}
