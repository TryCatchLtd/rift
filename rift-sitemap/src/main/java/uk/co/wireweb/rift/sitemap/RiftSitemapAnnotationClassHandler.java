package uk.co.wireweb.rift.sitemap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.wireweb.rift.core.internal.ClassHandler;
import uk.co.wireweb.rift.core.spi.PluginContext;
import uk.co.wireweb.rift.core.spi.annotation.Webpage;
import uk.co.wireweb.rift.sitemap.annotation.Sitemap;

/**
 * @author Daniel Johansson
 *
 * @since 3 Dec 2010
 */
public class RiftSitemapAnnotationClassHandler implements ClassHandler {

    private final static List<String> urls = new ArrayList<String>();

    public void handle(final Sitemap annotation, final Class<?> annotatedClass) {
        if (annotatedClass.isAnnotationPresent(Webpage.class)) {
            final Webpage webpageAnnotation = annotatedClass.getAnnotation(Webpage.class);
            final Sitemap sitemapAnnotation = annotatedClass.getAnnotation(Sitemap.class);
            final List<String> excludes = Arrays.asList(sitemapAnnotation.excludes());

            for (final String url : webpageAnnotation.serves()) {
                if (!urls.contains(url) && !excludes.contains(url)) {
                    urls.add(url);
                }
            }
        }
    }

    public static List<String> getUrls() {
        return urls;
    }

    @Override
    public void handle(final PluginContext pluginContext, final Class<?> clazz) {
        if (clazz.isAnnotationPresent(Webpage.class)) {
            final Webpage webpageAnnotation = clazz.getAnnotation(Webpage.class);
            final Sitemap sitemapAnnotation = clazz.getAnnotation(Sitemap.class);
            final List<String> excludes = Arrays.asList(sitemapAnnotation.excludes());

            for (final String url : webpageAnnotation.serves()) {
                if (!urls.contains(url) && !excludes.contains(url)) {
                    urls.add(url);
                }
            }
        }
    }
}
