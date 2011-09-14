package uk.co.wireweb.rift.core.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.wireweb.rift.core.spi.Instantiator;

/**
 * @author Daniel Johansson
 *
 * @since 13 Apr 2011
 */
public class DefaultInstantiator implements Instantiator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultInstantiator.class);

    @Override
    public <T> T getInstance(Class<T> clazz) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        final T instance = clazz.newInstance();

        try {
            final Method postConstructMethod = new AnnotationScanner().findMethodsOnClass(clazz).annotatedWith(PostConstruct.class).asSingleResult();

            if (postConstructMethod != null) {
                postConstructMethod.invoke(instance);
            }
        } catch (InvocationTargetException exception) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("[Rift] InvocationTargetException caught while invoking @PostConstruct method on class [{}]", clazz.getSimpleName());
            }
        }

        return instance;
    }
}
