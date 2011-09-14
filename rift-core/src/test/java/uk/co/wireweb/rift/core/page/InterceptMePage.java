package uk.co.wireweb.rift.core.page;

import uk.co.wireweb.rift.core.spi.annotation.Get;
import uk.co.wireweb.rift.core.spi.annotation.Webpage;
import uk.co.wireweb.rift.core.spi.view.ForwardView;
import uk.co.wireweb.rift.core.spi.view.View;

/**
 * @author Daniel Johansson
 *
 * @since 21 Nov 2010
 */
@Webpage(serves = "/intercept/interceptme.page")
public class InterceptMePage {

    @Get
    public View get() {
        return new ForwardView("/forwardto.jsp");
    }
}
