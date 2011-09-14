package uk.co.wireweb.rift.core.spi.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Daniel Johansson
 *
 * @since 13 Apr 2011
 */
public class ByteArrayView implements View {

    private final byte[] byteArray;

    private final String contentType;

    public ByteArrayView(final String contentType, final byte[] byteArray) {
        if (this.byteArray == null) {
            throw new IllegalArgumentException("byte array cannot be null");
        }

        this.contentType = contentType;
        this.byteArray = byteArray;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if ((this.contentType != null) && (this.contentType.length() > 0)) {
            response.setContentType(this.contentType);
        }

        response.setContentLength(this.byteArray.length);
        response.getOutputStream().write(this.byteArray);
        response.getOutputStream().flush();
    }
}
