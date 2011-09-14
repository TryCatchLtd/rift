package uk.co.wireweb.rift.guice;

import java.lang.reflect.Field;
import java.util.Map;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * @author Daniel Johansson
 *
 * @since 15 Jun 2011
 */
public class RiftGuicePropertyTypeListener implements TypeListener {

    private final Map<String, String> properties;

    public RiftGuicePropertyTypeListener(final Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public <I> void hear(final TypeLiteral<I> typeLiteral, final TypeEncounter<I> typeEncounter) {
        for (final Field field : typeLiteral.getRawType().getDeclaredFields()) {
            if ((field.getType() == String.class) && field.isAnnotationPresent(RiftProperty.class)) {
                final String propertyKey = field.getAnnotation(RiftProperty.class).value();
                typeEncounter.register(new RiftGuicePropertyMembersInjector<I>(field, this.properties.get(propertyKey)));
            }
        }
    }
}
