package uk.co.wireweb.rift.core.internal;

import java.lang.annotation.Annotation;

import javax.servlet.http.HttpServletRequest;

import uk.co.wireweb.rift.core.spi.annotation.Delete;
import uk.co.wireweb.rift.core.spi.annotation.Get;
import uk.co.wireweb.rift.core.spi.annotation.Post;
import uk.co.wireweb.rift.core.spi.annotation.Put;

/**
 * @author Daniel Johansson
 *
 * @since 13 Oct 2011
 */
public class RequestUtility {

    public static final String REQUEST_METHOD_POST = "POST";

    public static final String REQUEST_METHOD_PUT = "PUT";

    public static final String REQUEST_METHOD_DELETE = "DELETE";

    public static Class<? extends Annotation> getRequestMethodAnnotation(final HttpServletRequest request) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            return Post.class;
        } else if ("PUT".equalsIgnoreCase(request.getMethod())) {
            return Put.class;
        } else if ("DELETE".equalsIgnoreCase(request.getMethod())) {
            return Delete.class;
        } else {
            return Get.class;
        }
    }
}
