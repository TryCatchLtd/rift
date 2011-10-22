package uk.co.wireweb.rift.json.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.co.wireweb.rift.core.spi.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * @author Daniel Johansson
 *
 * @since 19 May 2011
 */
public class JsonView implements View {

    private final JsonElement jsonElement;

    public JsonView(final Object oject) {
        final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        this.jsonElement = gson.toJsonTree(oject);
    }

    public JsonView(final JsonElement jsonElement) {
        this.jsonElement = jsonElement;
    }

    @Override
    public void execute(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setContentLength(this.jsonElement.toString().length());
        response.getWriter().write(this.jsonElement.toString());
        response.getWriter().flush();
    }
}
