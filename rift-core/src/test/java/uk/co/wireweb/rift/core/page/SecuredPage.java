package uk.co.wireweb.rift.core.page;

import uk.co.wireweb.rift.core.spi.annotation.SecurePage;
import uk.co.wireweb.rift.core.spi.annotation.Webpage;

/**
 * @author Daniel Johansson
 *
 * @since 29 Apr 2011
 */
@Webpage(serves = "/secure.page")
@SecurePage(requiredRoles = "SECURE", notLoggedInRedirect = "/rifttest/denied.jsp", accessDeniedRedirect = "/rifttest/denied.jsp")
public class SecuredPage {

}
