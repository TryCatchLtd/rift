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
@Webpage(serves = "/contextrelativeredirect.page")
public class ContextRelativeRedirectPage {

    @Get
    public View get() {
        return new RedirectView("/forwardtojsp.page", true);
    }
}
