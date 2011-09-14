package uk.co.wireweb.rift.core.page;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import uk.co.wireweb.rift.core.spi.WebpageContext;
import uk.co.wireweb.rift.core.spi.annotation.Get;
import uk.co.wireweb.rift.core.spi.annotation.Webpage;
import uk.co.wireweb.rift.core.spi.view.View;

@Webpage(serves = "/postconstruct.page")
public class PostConstructPage {

    private boolean postConstructInvoked;

    @Get
    public View get(final WebpageContext context) throws IOException {
        final HttpServletResponse response = context.getResponse();

        if (this.postConstructInvoked) {
            response.setHeader("PostConstruct", "true");
        }

        response.getWriter().flush();

        return null;
    }

    @PostConstruct
    public void init() {
        this.postConstructInvoked = true;
    }
}
