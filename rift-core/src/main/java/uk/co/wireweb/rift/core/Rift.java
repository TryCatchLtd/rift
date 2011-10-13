package uk.co.wireweb.rift.core;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.wireweb.rift.core.internal.AnnotationScanner;
import uk.co.wireweb.rift.core.internal.Configuration;
import uk.co.wireweb.rift.core.internal.RequestUtility;
import uk.co.wireweb.rift.core.spi.InterceptHandler;
import uk.co.wireweb.rift.core.spi.RequestContext;
import uk.co.wireweb.rift.core.spi.RequestHandler;
import uk.co.wireweb.rift.core.spi.WebpageContext;

/**
 * @author Daniel Johansson
 *
 * @since 4 Nov 2010
 */
public class Rift implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Rift.class);

    public static final String RIFT_DEBUG_CLASS = "uk.co.wireweb.rift.debug.RiftDebug";

    private Configuration configuration;

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (this.configuration != null) {
            final long start = System.currentTimeMillis();

            try {
                final RequestContext requestContext = new RequestContext(this.configuration.getProperties(), this.configuration.getWebApplication(), this.configuration.getInstantiator(), request, response, this.configuration.getServletContext());

                for (final InterceptHandler interceptHandler : this.configuration.getInterceptHandlers()) {
                    if (!response.isCommitted()) {
                        interceptHandler.handle(requestContext);
                    }
                }

                for (final RequestHandler requestHandler : this.configuration.getRequestHandlers()) {
                    if (!response.isCommitted()) {
                        requestHandler.handle(requestContext);
                    }
                }
            } catch (Throwable throwable) {
                if (this.configuration.isDebug()) {
                    try {
                        request.setAttribute("exception", throwable);
                        final Class<?> riftDebugClass = Class.forName(RIFT_DEBUG_CLASS);
                        final Class<? extends Annotation> annotationClass = RequestUtility.getRequestMethodAnnotation(request);
                        final Method method = new AnnotationScanner().findMethodsOnClass(riftDebugClass).annotatedWith(annotationClass).asSingleResult();

                        if (method != null) {
                            final WebpageContext webpageContext = new WebpageContext(this.configuration.getServletContext(), request, response, this.configuration.getWebApplication(), this.configuration.getProperties());
                            final Object riftDebug = this.configuration.getInstantiator().getInstance(riftDebugClass);
                            method.invoke(riftDebug, webpageContext);
                        }
                    } catch (Exception exception) {
                        LOGGER.error("[Rift] Exception caught while creating debug page, what now? Let the container handle this.", exception);
                    }
                }

                LOGGER.error("[Rift] Exception caught while handling request", throwable);
            }

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("[Rift] Served path [{}] in [{}ms]", request.getServletPath(), (System.currentTimeMillis() - start));
            }
        }

        if (!response.isCommitted()) {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.configuration = (Configuration) filterConfig.getServletContext().getAttribute("internal-rift-configuration");

        if (this.configuration == null) {
            LOGGER.warn("[Rift] Rift appears to not have been loaded, are you sure you added the RiftServletContextListener to your web.xml");
        }

        if ((this.configuration != null) && (this.configuration.getWebApplication() != null)) {
            this.configuration.getWebApplication().init(this.configuration.getProperties());
        }
    }
}
