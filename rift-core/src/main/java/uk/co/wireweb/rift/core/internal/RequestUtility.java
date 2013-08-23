package uk.co.wireweb.rift.core.internal;

import uk.co.wireweb.rift.core.spi.annotation.Delete;
import uk.co.wireweb.rift.core.spi.annotation.Get;
import uk.co.wireweb.rift.core.spi.annotation.Post;
import uk.co.wireweb.rift.core.spi.annotation.Put;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;

/**
 * @author Daniel Johansson
 * @since 13 Oct 2011
 */
public class RequestUtility {

    private RequestUtility() {
    }

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
