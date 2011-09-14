package uk.co.wireweb.rift.core.spi;

import java.lang.annotation.Annotation;

/**
 * @author Daniel Johansson
 *
 * @since 20 Nov 2010
 */
public interface ClassAnnotationHandler<T extends Annotation> {

    public void handle(final T annotation, final Class<?> annotatedClass);
}
