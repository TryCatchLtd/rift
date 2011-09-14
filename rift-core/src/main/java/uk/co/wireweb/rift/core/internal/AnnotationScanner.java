package uk.co.wireweb.rift.core.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Johansson
 *
 * @since 16 Nov 2010
 */
public class AnnotationScanner {

    private Class<?> classToLookIn;

    private Class<? extends Annotation> annotationToLookFor;

    public AnnotationScanner findMethodsOnClass(final Class<?> clazz) {
        this.classToLookIn = clazz;
        return this;
    }

    public AnnotationScanner annotatedWith(final Class<? extends Annotation> clazz) {
        this.annotationToLookFor = clazz;
        return this;
    }

    public List<Method> asList() {
        final List<Method> methodsToReturn = new ArrayList<Method>();

        if ((this.classToLookIn != null) && (this.annotationToLookFor != null)) {
            final Method[] methods = this.classToLookIn.getMethods();

            if ((methods != null) && (methods.length > 0)) {
                for (final Method method : methods) {
                    if (method.isAnnotationPresent(this.annotationToLookFor)) {
                        methodsToReturn.add(method);
                    }
                }
            }
        }

        return methodsToReturn;
    }

    public Method asSingleResult() {
        final List<Method> methods = this.asList();

        if (!methods.isEmpty()) {
            return methods.get(0);
        }

        return null;
    }
}
