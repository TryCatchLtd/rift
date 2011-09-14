package uk.co.wireweb.rift.core.spi.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Daniel Johansson
 *
 * @since 16 Nov 2010
 */
public class ForwardView implements View {

    private final String path;

    public ForwardView(final String path) {
        this.path = path;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(this.path).forward(request, response);
    }
}
