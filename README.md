# Rift
A Java web framework built on top of the standard Servlet API.

## Getting Started
It's very simple to get started using Rift. All in all you need to add a few lines to your web.xml file, include the rift jars on your classpath and off you go.

### web.xml
Start by adding the Rift servlet context listener.

	<listener>
		<listener-class>uk.co.wireweb.rift.core.RiftServletContextListener</listener-class>
	</listener>

After this we'll need the Rift filter which is the entry point for all things Rift.

	<filter>
		<filter-name>Rift</filter-name>
		<filter-class>uk.co.wireweb.rift.core.Rift</filter-class>
	</filter>
	
And a mapping.

	<filter-mapping>
		<filter-name>Rift</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>


That's it for web.xml.

### Creating your first page
Now it gets interesting. Every page you want to create with Rift is backed by a class.

#### MyFirstPage.java

	@Webpage(serves = "/myfirst.page")
	public class MyFirstPage {
	
		@Parameter
		private String name;
		
		@Get
		public View get(final WebpageContext contex) {
			return new ForwardView("/WEB-INF/view/myfirstpage.jsp");
		}

		public String getName() {
			return this.name;
		}
	}

This class now represents what's on the `/myfirst.page` URL. In order for this to work we need to now create the `/WEB-INF/view/myfirstpage.jsp` which renders the actual view. You'll notice
that Rift doesn't actually get involved with the markup, there are no tag libraries as of yet. As you also might have noticed above, there is a private member annotated with `@Parameter`,
this tells Rift that it should automatically try and populate it with any incoming request parameter value for a parameter called `name`.

You can optionally tell Rift what the name of the
request parameter is. Lets say you've gone down the route of using a really odd request parameter name such as `users_name` and you wanted to map that to a more convenient Java bean name.
All you need to do is change the annotation to be `@Parameter("users_name")`, this will tell Rift that the request parameter is actually called `users_name` but it will still be mapped
to the class member `name`.

Another really clever thing about `@Parameter` worth mentioning here is that you can set the field type to be any of the primitives and Rift will automatically try to convert the incoming
request parameter value for you. At the moment there is no nice way for handling any exceptions that might be thrown during this process such as `NumberFormatException`. Rift will also
handle the case where a request parameter is an array, set or list. More on this later on.

#### /WEB-INF/view/myfirstpage.jsp

	Hello ${page.name}!

Just create the `myfirstpage.jsp` file and add the above line to it. Some magic will now happen, Rift automatically adds an attribute to the request called `page`, this attribute is
the actual class that represents your page, in this case it would be the `MyFirstPage` instance. Therefore we can now use `${page.name}` in our page to print out the value of the member
`name`. Remember to add a getter to your class in order for this to work since JSTL requires things to be valid beans.



