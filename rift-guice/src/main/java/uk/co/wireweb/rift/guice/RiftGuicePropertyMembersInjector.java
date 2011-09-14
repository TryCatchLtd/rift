package uk.co.wireweb.rift.guice;

import java.lang.reflect.Field;

import com.google.inject.MembersInjector;

/**
 * @author Daniel Johansson
 *
 * @since 15 Jun 2011
 */
public class RiftGuicePropertyMembersInjector<T> implements MembersInjector<T> {

    private final Field field;

    private final String property;

    public RiftGuicePropertyMembersInjector(final Field field, final String property) {
        this.field = field;
        this.property = property;
        field.setAccessible(true);
    }

    @Override
    public void injectMembers(final T instance) {
        try {
            this.field.set(instance, this.property);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
