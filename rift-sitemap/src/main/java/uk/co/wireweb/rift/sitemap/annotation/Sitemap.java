package uk.co.wireweb.rift.sitemap.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Daniel Johansson
 *
 * @since 3 Dec 2010
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Sitemap {
    String[] excludes() default {};
}
