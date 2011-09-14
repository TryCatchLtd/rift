package uk.co.wireweb.rift.core.page;

import javax.annotation.PostConstruct;

import uk.co.wireweb.rift.core.spi.WebpageContext;
import uk.co.wireweb.rift.core.spi.annotation.Get;
import uk.co.wireweb.rift.core.spi.annotation.Webpage;
import uk.co.wireweb.rift.core.spi.view.ForwardView;
import uk.co.wireweb.rift.core.spi.view.View;

/**
 * @author Daniel Johansson
 *
 * @since 4 Nov 2010
 */
@Webpage(serves = { "/forwardtojsp.page", "/forwardanything/*" })
public class ForwardToJspPage {

    private boolean postConstructCalled;

    @Get
    public View get(final WebpageContext context) {
        if (this.postConstructCalled) {
            context.getResponse().setHeader("PostConstruct", "true");
        }

        return new ForwardView("/forwardto.jsp");
    }

    @PostConstruct
    public void init() {
        this.postConstructCalled = true;
    }
}
