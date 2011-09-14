package uk.co.wireweb.rift.core.spi.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Daniel Johansson
 *
 * @since 19 Nov 2010
 */
public class RedirectView implements View {

    private final String path;

    private final boolean contextRelative;

    public RedirectView(final String path) {
        this(path, false);
    }

    public RedirectView(final String path, final boolean contextRelative) {
        this.path = path;
        this.contextRelative = contextRelative;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (this.contextRelative && (this.path.charAt(0) == '/')) {
            response.sendRedirect(request.getContextPath() + this.path);
        } else {
            response.sendRedirect(this.path);
        }
    }
}
