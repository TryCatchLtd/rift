package uk.co.wireweb.rift.core;

import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

/**
 * @author Daniel Johansson
 *
 * @since 17 Nov 2010
 */
public class RiftTest {

    @Test
    public void testForwardToJsp() throws Exception {
        final WebClient webClient = new WebClient();
        final HtmlPage page = webClient.getPage("http://localhost:8888/rifttest/forwardtojsp.page");

        Assert.assertEquals("ForwardTo", page.getTitleText());
    }

    @Test
    public void testForwardToJspUsingWildcard() throws Exception {
        final WebClient webClient = new WebClient();
        final HtmlPage page = webClient.getPage("http://localhost:8888/rifttest/forwardanything/some.page");

        Assert.assertEquals("ForwardTo", page.getTitleText());
    }

    @Test
    public void testPostConstruct() throws Exception {
        final WebClient webClient = new WebClient();
        final Page page = webClient.getPage("http://localhost:8888/rifttest/postconstruct.page");
        final String postConstruct = this.getResponseHeader("PostConstruct", page.getWebResponse().getResponseHeaders());

        Assert.assertEquals("true", postConstruct);
    }

    @Test
    public void testRedirectUsingContextRelative() throws Exception {
        final WebClient webClient = new WebClient();
        final HtmlPage page = webClient.getPage("http://localhost:8888/rifttest/contextrelativeredirect.page");

        Assert.assertEquals("ForwardTo", page.getTitleText());
    }

    @Test
    public void testFullUrlRedirect() throws Exception {
        final WebClient webClient = new WebClient();
        final HtmlPage page = webClient.getPage("http://localhost:8888/rifttest/fullurlredirect.page");

        Assert.assertEquals("ForwardTo", page.getTitleText());
    }

    @Test
    public void testResponseModifyingInterceptor() throws Exception {
        final WebClient webClient = new WebClient();
        final HtmlPage page = webClient.getPage("http://localhost:8888/rifttest/intercept/interceptme.page");
        final String interceptModified = this.getResponseHeader("InterceptorModified", page.getWebResponse().getResponseHeaders());

        Assert.assertEquals("ForwardTo", page.getTitleText());
        Assert.assertEquals("true", interceptModified);
    }

    @Test
    public void testRequestParametersPage() throws Exception {
        final WebClient webClient = new WebClient();
        final HtmlPage page = webClient.getPage("http://localhost:8888/rifttest/requestparameters.page?testParameter=someValue&anotherParameter=anotherValue&intParameter=4&booleanParameter=true&longParameter=60032&shortParameter=122&floatParameter=44.2&doubleParameter=999.123&arrayParameter=test1&arrayParameter=test2&listParameter=list1&listParameter=list2&listParameter=list3");
        final String testParameter = this.getResponseHeader("testParameter", page.getWebResponse().getResponseHeaders());
        final String anotherParameter = this.getResponseHeader("anotherParameter", page.getWebResponse().getResponseHeaders());
        final int intParameter = Integer.parseInt(this.getResponseHeader("intParameter", page.getWebResponse().getResponseHeaders()));
        final boolean booleanParameter = Boolean.parseBoolean(this.getResponseHeader("booleanParameter", page.getWebResponse().getResponseHeaders()));
        final long longParameter = Long.parseLong(this.getResponseHeader("longParameter", page.getWebResponse().getResponseHeaders()));
        final short shortParameter = Short.parseShort(this.getResponseHeader("shortParameter", page.getWebResponse().getResponseHeaders()));
        final float floatParameter = Float.parseFloat(this.getResponseHeader("floatParameter", page.getWebResponse().getResponseHeaders()));
        final double doubleParameter = Double.parseDouble(this.getResponseHeader("doubleParameter", page.getWebResponse().getResponseHeaders()));
        final String arrayParameter1 = this.getResponseHeader("arrayParameter0", page.getWebResponse().getResponseHeaders());
        final String arrayParameter2 = this.getResponseHeader("arrayParameter1", page.getWebResponse().getResponseHeaders());
        final String listParameter1 = this.getResponseHeader("listParameter0", page.getWebResponse().getResponseHeaders());
        final String listParameter2 = this.getResponseHeader("listParameter1", page.getWebResponse().getResponseHeaders());
        final String listParameter3 = this.getResponseHeader("listParameter2", page.getWebResponse().getResponseHeaders());

        Assert.assertEquals("ForwardTo", page.getTitleText());
        Assert.assertEquals("someValue", testParameter);
        Assert.assertEquals("anotherValue", anotherParameter);
        Assert.assertEquals(4, intParameter);
        Assert.assertEquals(true, booleanParameter);
        Assert.assertEquals(60032L, longParameter);
        Assert.assertEquals(122, shortParameter);
        Assert.assertEquals(44.2f, floatParameter);
        Assert.assertEquals(999.123, doubleParameter);
        Assert.assertEquals("test1", arrayParameter1);
        Assert.assertEquals("test2", arrayParameter2);
        Assert.assertEquals("list1", listParameter1);
        Assert.assertEquals("list2", listParameter2);
        Assert.assertEquals("list3", listParameter3);
    }

    @Test
    public void testSecurePage_redirectsToDeniedJsp_whenIdentityDoesNotHaveRequiredRole() throws Exception {
        final WebClient webClient = new WebClient();
        final HtmlPage page = webClient.getPage("http://localhost:8888/rifttest/secure.page");

        Assert.assertEquals("Denied", page.getTitleText());
    }

    private String getResponseHeader(final String header, final List<NameValuePair> headers) {
        for (final NameValuePair nameValuePair : headers) {
            if (header.equals(nameValuePair.getName())) {
                return nameValuePair.getValue();
            }
        }

        return null;
    }

    @BeforeClass
    public static void setUp() throws Exception {
        server = new Server(8888);

        final String baseUrl = RiftTest.class.getClassLoader().getResource(".").toExternalForm();
        final String webXmlUrlString = RiftTest.class.getClassLoader().getResource("web.xml").toExternalForm();
        final WebAppContext webAppContext = new WebAppContext(webXmlUrlString, "/rifttest");
        webAppContext.addEventListener(new RiftServletContextListener());
        webAppContext.setDescriptor(webXmlUrlString);
        webAppContext.setResourceBase(baseUrl);

        server.setHandler(webAppContext);
        server.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.stop();
    }

    private static Server server;

    private static final String LOG_PATTERN = "%-5p %-20c{1} %m%n";

    static {
        Logger.getRootLogger().setLevel(Level.WARN);
        Logger.getLogger("uk").addAppender(new ConsoleAppender(new PatternLayout(LOG_PATTERN)));
        Logger.getLogger("uk").setLevel(Level.TRACE);
        Logger.getLogger("org").addAppender(new ConsoleAppender(new PatternLayout(LOG_PATTERN)));
        Logger.getLogger("com").addAppender(new ConsoleAppender(new PatternLayout(LOG_PATTERN)));
    }
}
