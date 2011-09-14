package uk.co.wireweb.rift.debug;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.co.wireweb.rift.core.spi.WebpageContext;
import uk.co.wireweb.rift.core.spi.annotation.Get;
import uk.co.wireweb.rift.core.spi.annotation.Webpage;

/**
 * @author Daniel Johansson
 *
 * @since 14 Nov 2010
 */
@Webpage(serves = "/rift/debug")
public class RiftDebug {

    @Get
    public void get(final WebpageContext context) {
        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();
        final Throwable throwable = (Throwable) request.getAttribute("exception");

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<!doctype html>");
        stringBuilder.append("<html>");
        stringBuilder.append("<head>");
        stringBuilder.append("<title>Rift Debug Page</title>");
        stringBuilder.append("</head>");
        stringBuilder.append("<body>");

        if (throwable != null) {
            final StringWriter stringWriter = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            printWriter.flush();

            stringBuilder.append("<pre>").append(stringWriter.toString()).append("</pre>");
        }

        stringBuilder.append("</body>");
        stringBuilder.append("</html>");

        try {
            response.getWriter().write(stringBuilder.toString());
            response.flushBuffer();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
