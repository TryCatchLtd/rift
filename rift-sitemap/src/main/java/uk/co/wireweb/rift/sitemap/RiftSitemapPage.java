package uk.co.wireweb.rift.sitemap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.co.wireweb.rift.core.spi.annotation.Get;
import uk.co.wireweb.rift.core.spi.annotation.Webpage;
import uk.co.wireweb.rift.core.spi.view.View;

/**
 * @author Daniel Johansson
 *
 * @since 3 Dec 2010
 */
@Webpage(serves = "/sitemap.xml")
public class RiftSitemapPage {

    @Get
    public View get(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String baseUrl = "http://" + request.getHeader("host");
        final StringBuilder stringBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        stringBuilder.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\r\n");

        for (final String pageUrl : RiftSitemapAnnotationClassHandler.getUrls()) {
            final String fullyQualifiedUrl = baseUrl + pageUrl;

            stringBuilder.append("<url>").append("<loc>").append(fullyQualifiedUrl).append("</loc>").append("</url>\r\n");
        }

        stringBuilder.append("</urlset>\r\n");

        response.getWriter().write(stringBuilder.toString());
        response.getWriter().flush();

        return null;
    }
}
