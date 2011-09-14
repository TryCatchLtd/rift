package uk.co.wireweb.rift.core.page;

import uk.co.wireweb.rift.core.spi.annotation.Get;
import uk.co.wireweb.rift.core.spi.annotation.Webpage;
import uk.co.wireweb.rift.core.spi.view.RedirectView;
import uk.co.wireweb.rift.core.spi.view.View;

/**
 * @author Daniel Johansson
 *
 * @since 19 Nov 2010
 */
@Webpage(serves = "/fullurlredirect.page")
public class FullUrlRedirectPage {

    @Get
    public View get() {
        return new RedirectView("http://localhost:8888/rifttest/forwardtojsp.page");
    }
}
