package uk.co.wireweb.rift.core.internal;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.wireweb.rift.core.spi.InterceptHandler;
import uk.co.wireweb.rift.core.spi.RequestContext;
import uk.co.wireweb.rift.core.spi.WebpageContext;
import uk.co.wireweb.rift.core.spi.annotation.Intercept;
import uk.co.wireweb.rift.core.spi.view.View;

/**
 * @author Daniel Johansson
 *
 * @since 14 Jun 2011
 */
public class InterceptorRequestHandler implements InterceptHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterceptorRequestHandler.class);

    private final InterceptorClassHandler interceptorClassHandler;

    public InterceptorRequestHandler(final InterceptorClassHandler interceptorClassHandler) {
        this.interceptorClassHandler = interceptorClassHandler;
    }

    @Override
    public void handle(final RequestContext requestContext) throws Exception {
        final HttpServletRequest request = requestContext.getRequest();
        final HttpServletResponse response = requestContext.getResponse();
        final Map<String, List<Class<?>>> interceptors = this.interceptorClassHandler.getInterceptors();

        for (final Map.Entry<String, List<Class<?>>> entry : interceptors.entrySet()) {
            final Pattern pattern = Pattern.compile(entry.getKey());
            final Matcher matcher = pattern.matcher(request.getServletPath());

            if (matcher.find()) {
                for (final Class<?> clazz : entry.getValue()) {
                    final Object interceptorInstance = requestContext.getInstantiator().getInstance(clazz);
                    final Method method = new AnnotationScanner().findMethodsOnClass(clazz).annotatedWith(Intercept.class).asSingleResult();

                    if (method != null) {
                        if ("void".equalsIgnoreCase(method.getReturnType().getSimpleName()) || View.class.equals(method.getReturnType())) {
                            final int parameterTypes = method.getParameterTypes().length;

                            if ((parameterTypes == 0) || ((parameterTypes == 1) && WebpageContext.class.equals(method.getParameterTypes()[0])) || ((parameterTypes == 2) && HttpServletRequest.class.equals(method.getParameterTypes()[0]) && HttpServletResponse.class.equals(method.getParameterTypes()[1]))) {
                                try {
                                    Object result = null;

                                    if (parameterTypes == 0) {
                                        result = method.invoke(interceptorInstance);
                                    } else if (parameterTypes == 1) {
                                        final WebpageContext webpageContext = new WebpageContext(requestContext.getServletContext(), request, response, requestContext.getWebApplication(), requestContext.getProperties());
                                        result = method.invoke(interceptorInstance, webpageContext);
                                    } else if (parameterTypes == 2) {
                                        LOGGER.warn("[Rift] Method with signature {}(HttpServletRequest, HttpServletResponse) has been deprecated, please use the new signature {}(Webpagecontext) on class {}", new Object[] { method.getName(), method.getName(), clazz.getSimpleName() });
                                        result = method.invoke(interceptorInstance, request, response);
                                    }

                                    if ((result != null) && (result instanceof View)) {
                                        final View view = (View) result;
                                        view.execute(request, response);
                                    }
                                } finally {
                                    final Method preDestroyMethod = new AnnotationScanner().findMethodsOnClass(clazz).annotatedWith(PreDestroy.class).asSingleResult();

                                    if (preDestroyMethod != null) {
                                        preDestroyMethod.invoke(interceptorInstance);
                                    }
                                }
                            }
                        } else {
                            LOGGER.warn("[Rift] Method [{}] on class [{}] does not have the correct return type. The return type must be either void or View", method.getName(), clazz.getSimpleName());
                        }
                    }
                }
            }
        }
    }
}
