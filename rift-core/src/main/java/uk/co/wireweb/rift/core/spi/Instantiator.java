package uk.co.wireweb.rift.core.spi;

/**
 * @author Daniel Johansson
 *
 * @since 13 Jun 2011
 */
public interface Instantiator {

    public <T> T getInstance(final Class<T> clazz) throws IllegalArgumentException, IllegalAccessException, InstantiationException;
}
