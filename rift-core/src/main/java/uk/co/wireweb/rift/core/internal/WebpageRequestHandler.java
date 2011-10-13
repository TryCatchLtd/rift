package uk.co.wireweb.rift.core.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.wireweb.rift.core.spi.Identity;
import uk.co.wireweb.rift.core.spi.RequestContext;
import uk.co.wireweb.rift.core.spi.RequestHandler;
import uk.co.wireweb.rift.core.spi.WebpageContext;
import uk.co.wireweb.rift.core.spi.annotation.Parameter;
import uk.co.wireweb.rift.core.spi.annotation.SecurePage;
import uk.co.wireweb.rift.core.spi.view.RedirectView;
import uk.co.wireweb.rift.core.spi.view.View;

/**
 * @author Daniel Johansson
 *
 * @since 13 Jun 2011
 */
public class WebpageRequestHandler implements RequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebpageRequestHandler.class);

    private static final String PAGE_REQUEST_ATTRIBUTE = "page";

    private final WebpageClassHandler webpageClassHandler;

    public WebpageRequestHandler(final WebpageClassHandler webpageClassHandler) {
        this.webpageClassHandler = webpageClassHandler;
    }

    @Override
    public void handle(final RequestContext requestContext) throws Exception {
        final HttpServletRequest request = requestContext.getRequest();
        final HttpServletResponse response = requestContext.getResponse();

        if (!response.isCommitted()) {
            final WebpageContext webpageContext = new WebpageContext(requestContext.getServletContext(), request, response, requestContext.getWebApplication(), requestContext.getProperties());
            final Class<?> pageClass = this.getPage(request.getServletPath());

            if (pageClass != null) {
                final Identity identity = webpageContext.getIdentity();
                final SecurePage securePage = pageClass.getAnnotation(SecurePage.class);

                if ((securePage == null) || (this.identityIsLoggedInAndHasRequiredRoles(securePage, identity))) {
                    final Class<? extends Annotation> annotationClass = RequestUtility.getRequestMethodAnnotation(request);
                    final Method method = new AnnotationScanner().findMethodsOnClass(pageClass).annotatedWith(annotationClass).asSingleResult();

                    if (method != null) {
                        if ("void".equalsIgnoreCase(method.getReturnType().getSimpleName()) || View.class.equals(method.getReturnType())) {
                            final int parameterTypes = method.getParameterTypes().length;

                            if ((parameterTypes == 0) || ((parameterTypes == 1) && WebpageContext.class.equals(method.getParameterTypes()[0])) || ((parameterTypes == 2) && HttpServletRequest.class.equals(method.getParameterTypes()[0]) && HttpServletResponse.class.equals(method.getParameterTypes()[1]))) {
                                final Object pageInstance = requestContext.getInstantiator().getInstance(pageClass);

                                try {
                                    this.populatePageInstanceWithRequestParameters(request, pageInstance);

                                    Object result = null;

                                    if (parameterTypes == 0) {
                                        result = method.invoke(pageInstance);
                                    } else if (parameterTypes == 1) {
                                        result = method.invoke(pageInstance, webpageContext);
                                    } else if (parameterTypes == 2) {
                                        LOGGER.warn("[Rift] Method with signature {}(HttpServletRequest, HttpServletResponse) has been deprecated, please use the new signature {}(Webpagecontext) on class {}", new Object[] { method.getName(), method.getName(), pageClass.getSimpleName() });
                                        result = method.invoke(pageInstance, request, response);
                                    }

                                    if ((result != null) && (result instanceof View)) {
                                        request.setAttribute(PAGE_REQUEST_ATTRIBUTE, pageInstance);

                                        final View view = (View) result;
                                        view.execute(request, response);
                                    }
                                } finally {
                                    final Method preDestroyMethod = new AnnotationScanner().findMethodsOnClass(pageClass).annotatedWith(PreDestroy.class).asSingleResult();

                                    if (preDestroyMethod != null) {
                                        preDestroyMethod.invoke(pageInstance);
                                    }
                                }
                            } else {
                                LOGGER.warn("[Rift] Annotated method [{}] on class [{}] has the wrong parameter types, it needs to be either empty or HttpServletRequest and HttpServletResponse", method.getName(), pageClass.getName());
                            }
                        } else {
                            LOGGER.warn("[Rift] Annotated method [{}] on class [{}] has the wrong return type, it needs to be either Void or View", method.getName(), pageClass.getName());
                        }
                    }
                } else if (securePage != null) {
                    if (!webpageContext.getIdentity().isLoggedIn()) {
                        LOGGER.debug("[Rift] Identity is not logged in, redirecting to [{}]", securePage.notLoggedInRedirect());
                        new RedirectView(securePage.notLoggedInRedirect(), false).execute(request, response);
                    } else {
                        LOGGER.debug("[Rift] Identity lacking required roles, redirecting to [{}]", securePage.accessDeniedRedirect());
                        new RedirectView(securePage.accessDeniedRedirect(), false).execute(request, response);
                    }
                }
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void populatePageInstanceWithRequestParameters(final HttpServletRequest request, final Object pageInstance) throws Exception {
        final Map<String, Field> allDeclaredFields = this.getAllDeclaredNonFinalNonStaticFields(pageInstance.getClass());

        if (!allDeclaredFields.isEmpty()) {
            for (final Map.Entry<String, Field> entry : allDeclaredFields.entrySet()) {
                final Field field = entry.getValue();

                if (field.isAnnotationPresent(Parameter.class)) {
                    final Class<?> fieldType = field.getType();
                    final Parameter parameterAnnotation = field.getAnnotation(Parameter.class);

                    String parameterName = field.getName();

                    if ((parameterAnnotation.value() != null) && (parameterAnnotation.value().length() > 0)) {
                        parameterName = parameterAnnotation.value();
                    }

                    Object value = null;

                    // TODO: Make this nicer
                    if (List.class.equals(fieldType) || Set.class.equals(fieldType) || fieldType.isArray()) {
                        final String[] parameterValues = request.getParameterValues(parameterName);

                        if ((parameterValues != null) && (parameterValues.length > 0)) {
                            Object instance = null;
                            Class<?> type = fieldType;

                            if (List.class.equals(fieldType) || Set.class.equals(fieldType)) {
                                instance = new ArrayList(parameterValues.length);
                                final Type[] types = fieldType.getGenericInterfaces();

                                if ((types != null) && (types.length > 0)) {
                                    type = types[0].getClass();
                                }
                            } else if (fieldType.isArray()) {
                                instance = Array.newInstance(fieldType.getComponentType(), parameterValues.length);
                            }

                            int index = 0;

                            for (final String parameterValue : parameterValues) {
                                if (List.class.equals(fieldType) || Set.class.equals(fieldType)) {
                                    final List list = (List) instance;
                                    list.add(this.convertValueToCorrectType(type, parameterValue));
                                } else if (fieldType.isArray()) {
                                    Array.set(instance, index, this.convertValueToCorrectType(type, parameterValue));
                                }

                                ++index;
                            }

                            field.setAccessible(true);
                            field.set(pageInstance, instance);
                        }
                    } else {
                        final String parameterValue = request.getParameter(parameterName);
                        value = this.convertValueToCorrectType(fieldType, parameterValue);

                        field.setAccessible(true);
                        field.set(pageInstance, value);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Object> T convertValueToCorrectType(final Class<T> clazz, final String parameterValue) {
        Object value = null;

        if ("int".equals(clazz.toString())) {
            value = Integer.parseInt(parameterValue);
        } else if ("long".equals(clazz.toString())) {
            value = Long.parseLong(parameterValue);
        } else if ("boolean".equals(clazz.toString())) {
            value = Boolean.parseBoolean(parameterValue);
        } else if ("float".equals(clazz.toString())) {
            value = Float.parseFloat(parameterValue);
        } else if ("double".equals(clazz.toString())) {
            value = Double.parseDouble(parameterValue);
        } else if ("short".equals(clazz.toString())) {
            value = Short.parseShort(parameterValue);
        } else {
            value = parameterValue;
        }

        return (T) value;
    }

    private Map<String, Field> getAllDeclaredNonFinalNonStaticFields(final Class<?> type) {
        final Map<String, Field> fields = new HashMap<String, Field>();

        if (type.getSuperclass() != null) {
            fields.putAll(this.getAllDeclaredNonFinalNonStaticFields(type.getSuperclass()));
        }

        for (final Field field : type.getDeclaredFields()) {
            if (!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                fields.put(field.getName(), field);
            }
        }

        return fields;
    }

    private boolean identityIsLoggedInAndHasRequiredRoles(final SecurePage securePage, final Identity identity) {
        if (!identity.isLoggedIn()) {
            return false;
        }

        for (final String role : securePage.requiredRoles()) {
            if ((role.length() > 0) && !identity.hasRole(role)) {
                return false;
            }
        }

        return true;
    }

    public Class<?> getPage(final String servePath) {
        final Map<String, Class<?>> pages = this.webpageClassHandler.getPages();
        final Class<?> pageClass = pages.get(servePath);

        if (pageClass == null) {
            for (final Map.Entry<String, Class<?>> entry : pages.entrySet()) {
                if (entry.getKey().contains("*")) {
                    final int positionOfWildCard = entry.getKey().indexOf('*');
                    final String pathWithoutWildcard = entry.getKey().substring(0, positionOfWildCard);

                    if (servePath.startsWith(pathWithoutWildcard)) {
                        return entry.getValue();
                    }
                }
            }
        }

        return pageClass;
    }
}
