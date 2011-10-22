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

    private final int status;

    /**
     * Constructs a forwarding view to a certain path.
     * 
     * @param path the path to forward the request to, this is usually a JSP or similar.
     */
    public ForwardView(final String path) {
        this(path, HttpServletResponse.SC_OK);
    }

    /**
     * Constructs a forwarding view to a certain path with a specific status code.
     * 
     * @param path the path to forward the request to, this is usually a JSP or similar.
     * @param statusCode a valid HTTP status code which can be found on the {@link HttpServletResponse} interface.
     */
    public ForwardView(final String path, final int statusCode) {
        this.path = path;
        this.status = statusCode;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(this.status);
        request.getRequestDispatcher(this.path).forward(request, response);
    }
}
