package uk.co.wireweb.rift.core.interceptor;

import uk.co.wireweb.rift.core.spi.WebpageContext;
import uk.co.wireweb.rift.core.spi.annotation.Intercept;
import uk.co.wireweb.rift.core.spi.annotation.Interceptor;

/**
 * @author Daniel Johansson
 *
 * @since 21 Nov 2010
 */
@Interceptor(path = "/intercept/.*")
public class ResponseModificationInterceptor {

    @Intercept
    public void intercept(final WebpageContext context) {
        context.getResponse().setHeader("InterceptorModified", "true");
    }
}
