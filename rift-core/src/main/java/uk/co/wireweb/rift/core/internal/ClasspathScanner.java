package uk.co.wireweb.rift.core.internal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.wireweb.rift.core.spi.Plugin;
import uk.co.wireweb.rift.core.spi.PluginContext;

/**
 * @author Daniel Johansson
 *
 * @since 12 Jun 2011
 */
public class ClasspathScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathScanner.class);

    private final List<ClassHandler> classHandlers = new ArrayList<ClassHandler>();

    private final List<String> parsedPath = new ArrayList<String>();

    private final Set<String> classnamesOnClasspath = new HashSet<String>();

    private final ServletContext servletContext;

    public ClasspathScanner(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void registerClassHandler(final ClassHandler classHandler) {
        this.classHandlers.add(classHandler);
    }

    public void registerClassHandlers(final List<ClassHandler> classHandlers) {
        this.classHandlers.addAll(classHandlers);
    }

    public void parseForAndHandleClasses(final PluginContext pluginContext) {
        for (final String className : this.getFullyQualifiedClassnamesOnClasspath()) {
            LOGGER.trace("[Rift] Running classhandlers for class [{}]", className);

            for (final ClassHandler classHandler : this.classHandlers) {
                try {
                    final Class<?> clazz = Class.forName(className, false, this.getClass().getClassLoader());
                    classHandler.handle(pluginContext, clazz);
                } catch (Throwable exception) {

                }
            }
        }
    }

    public Set<String> getFullyQualifiedClassnamesOnClasspath() {
        if (this.classnamesOnClasspath.isEmpty()) {
            final Set<String> classpaths = new HashSet<String>();

            try {
                classpaths.add(this.servletContext.getResource("/").getPath());
            } catch (MalformedURLException exception) {

            }

            classpaths.add(this.servletContext.getRealPath("/WEB-INF/classes"));
            classpaths.add(this.servletContext.getRealPath("/WEB-INF/lib"));

            for (final String path : classpaths) {
                if (path != null) {
                    LOGGER.trace("[Rift] Searching through classpath [{}]", path);

                    if (!this.parsedPath.contains(path)) {
                        final File file = new File(path);

                        if (file.isDirectory()) {
                            this.classnamesOnClasspath.addAll(this.getClasses(file, file));
                        } else {
                            if (file.getAbsolutePath().toLowerCase().endsWith(".jar")) {
                                this.classnamesOnClasspath.addAll(this.parseJarFile(file));
                            }
                        }

                        this.parsedPath.add(path);
                    }
                }
            }
        }

        return this.classnamesOnClasspath;
    }

    @SuppressWarnings("unchecked")
    public List<Class<Plugin>> getPlugins() {
        final List<Class<Plugin>> plugins = new ArrayList<Class<Plugin>>();

        for (final String className : this.getFullyQualifiedClassnamesOnClasspath()) {
            try {
                final Class<?> clazz = Class.forName(className, false, this.getClass().getClassLoader());

                if (Plugin.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
                    plugins.add((Class<Plugin>) clazz);
                }
            } catch (Throwable exception) {

            }
        }

        return plugins;
    }

    private List<String> getClasses(final File rootDirectory, final File directory) {
        final List<String> classNames = new ArrayList<String>();
        final File list[] = directory.listFiles();
        final String rootDirectoryPath = rootDirectory.getAbsolutePath();

        for (final File file : list) {
            if (file.isDirectory()) {
                classNames.addAll(this.getClasses(rootDirectory, file));
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".java") || file.getAbsolutePath().toLowerCase().endsWith(".class")) {
                final String filePath = file.getAbsolutePath();
                final String className = filePath.substring(rootDirectoryPath.length());

                classNames.add(this.toFullyQualifiedPackageAndClass(className));
            } else if (file.getAbsolutePath().toLowerCase().endsWith(".jar")) {
                classNames.addAll(this.parseJarFile(file));
            }
        }

        return classNames;
    }

    private List<String> parseJarFile(final File file) {
        LOGGER.trace("[Rift] Parsing JAR file [{}]", file.getAbsolutePath());

        final List<String> classNames = new ArrayList<String>();

        if (!this.parsedPath.contains(file.getAbsolutePath()) && (file != null)) {
            try {
                final JarFile jarFile = new JarFile(file);

                if (jarFile != null) {
                    final Enumeration<JarEntry> enumeration = jarFile.entries();

                    while (enumeration.hasMoreElements()) {
                        final JarEntry entry = enumeration.nextElement();

                        if (!entry.isDirectory() && (entry.getName().endsWith(".class") || entry.getName().endsWith(".java"))) {
                            final String className = this.toFullyQualifiedPackageAndClass(entry.getName());
                            classNames.add(className);
                        }
                    }

                    this.parsedPath.add(file.getAbsolutePath());
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        return classNames;
    }

    private String toFullyQualifiedPackageAndClass(final String input) {
        int lastDotIndex = -1;

        for (int i = input.length() - 1; i >= 0; --i) {
            if (input.charAt(i) == '.') {
                lastDotIndex = i;
                break;
            }
        }

        if (lastDotIndex == -1) {
            return input;
        }

        String output = input.substring(0, lastDotIndex);
        output = output.replaceAll("/", ".");
        output = output.replaceAll("\\\\", ".");

        if (output.charAt(0) == '.') {
            output = output.substring(1);
        }

        return output;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }
}
