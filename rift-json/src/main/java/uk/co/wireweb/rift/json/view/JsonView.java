package uk.co.wireweb.rift.json.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.co.wireweb.rift.core.spi.view.View;

import com.google.gson.JsonObject;

/**
 * @author Daniel Johansson
 *
 * @since 19 May 2011
 */
public class JsonView implements View {

    private final JsonObject jsonObject;

    public JsonView(final JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setContentLength(this.jsonObject.getAsString().length());
        response.getWriter().write(this.jsonObject.getAsString());
        response.getWriter().flush();
    }
}
