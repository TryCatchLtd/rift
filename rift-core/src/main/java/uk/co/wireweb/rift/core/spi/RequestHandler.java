package uk.co.wireweb.rift.core.spi;

/**
 * @author Daniel Johansson
 *
 * @since 13 Jun 2011
 */
public interface RequestHandler {

    public void handle(final RequestContext requestContext) throws Exception;
}
