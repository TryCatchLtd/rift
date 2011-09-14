package uk.co.wireweb.rift.core.page;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import uk.co.wireweb.rift.core.spi.WebpageContext;
import uk.co.wireweb.rift.core.spi.annotation.Get;
import uk.co.wireweb.rift.core.spi.annotation.Parameter;
import uk.co.wireweb.rift.core.spi.annotation.Webpage;
import uk.co.wireweb.rift.core.spi.view.ForwardView;
import uk.co.wireweb.rift.core.spi.view.View;

/**
 * @author Daniel Johansson
 *
 * @since 30 Nov 2010
 */
@Webpage(serves = "/requestparameters.page")
public class RequestParametersPage {

    @Parameter
    private String testParameter;

    @Parameter("anotherParameter")
    private String mappedToAnotherParameter;

    @Parameter
    private int intParameter;

    @Parameter
    private boolean booleanParameter;

    @Parameter
    private long longParameter;

    @Parameter
    private short shortParameter;

    @Parameter
    private float floatParameter;

    @Parameter
    private double doubleParameter;

    @Parameter
    private String[] arrayParameter;

    @Parameter
    private List<String> listParameter;

    @Get
    public View get(final WebpageContext context) {
        final HttpServletResponse response = context.getResponse();

        response.setHeader("testParameter", this.testParameter);
        response.setHeader("anotherParameter", this.mappedToAnotherParameter);
        response.setHeader("intParameter", String.valueOf(this.intParameter));
        response.setHeader("booleanParameter", String.valueOf(this.booleanParameter));
        response.setHeader("longParameter", String.valueOf(this.longParameter));
        response.setHeader("shortParameter", String.valueOf(this.shortParameter));
        response.setHeader("floatParameter", String.valueOf(this.floatParameter));
        response.setHeader("doubleParameter", String.valueOf(this.doubleParameter));

        if (this.arrayParameter != null) {
            for (int i = 0; i < this.arrayParameter.length; ++i) {
                response.setHeader("arrayParameter" + i, this.arrayParameter[i]);
            }
        }

        if ((this.listParameter != null) && !this.listParameter.isEmpty()) {
            for (int i = 0; i < this.listParameter.size(); ++i) {
                response.setHeader("listParameter" + i, this.listParameter.get(i));
            }
        }

        return new ForwardView("/forwardto.jsp");
    }
}
