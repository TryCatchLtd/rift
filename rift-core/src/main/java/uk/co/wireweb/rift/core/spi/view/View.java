package uk.co.wireweb.rift.core.spi.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Daniel Johansson
 *
 * @since 4 Nov 2010
 */
public interface View {

    /**
     * Executes the view.
     * 
     * @param request the {@link HttpServletRequest}
     * @param response the {@link HttpServletResponse}
     */
    public void execute(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException;
}
