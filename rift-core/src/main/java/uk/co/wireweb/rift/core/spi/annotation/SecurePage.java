package uk.co.wireweb.rift.core.spi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Daniel Johansson
 *
 * @since 29 Apr 2011
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SecurePage {

    String[] requiredRoles() default "";

    String notLoggedInRedirect();

    String accessDeniedRedirect() default "";
}
