package uk.co.wireweb.rift.closure.interceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.wireweb.rift.core.spi.WebpageContext;
import uk.co.wireweb.rift.core.spi.annotation.Intercept;
import uk.co.wireweb.rift.core.spi.annotation.Interceptor;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;

/**
 * @author Daniel Johansson
 *
 * @since 11 Feb 2011
 */
@Interceptor
public class ClosureInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClosureInterceptor.class);

    private static final Map<String, String> JAVASCRIPT_CACHE = new HashMap<String, String>();

    @Intercept
    public void intercept(final WebpageContext context) throws IOException {
        final HttpServletResponse response = context.getResponse();
        final String path = context.getRequest().getServletPath();

        if ((path != null) && path.toLowerCase().endsWith(".js")) {
            if (!JAVASCRIPT_CACHE.containsKey(path.toLowerCase())) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("[Rift] Running closure compile on path [{}]", path);
                }

                try {
                    Compiler.setLoggingLevel(Level.OFF);
                    final String contents = this.loadFile(context.getServletContext().getResourceAsStream(path));
                    final JSSourceFile extern = JSSourceFile.fromCode("externs.js", "function alert(x) {}");
                    final JSSourceFile input = JSSourceFile.fromCode(path, contents);
                    final Compiler compiler = new Compiler();
                    final CompilerOptions compilerOptions = new CompilerOptions();
                    CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(compilerOptions);

                    compiler.compile(extern, input, compilerOptions);
                    final String compiledContents = compiler.toSource();
                    final float reduction = 100 - (((float) compiledContents.length() / (float) contents.length()) * 100);
                    final String reductionPercentage = String.format("%.2f", reduction);

                    LOGGER.info("Compiled [{}], reduced size from [{}b] to [{}b], that's a [{}%] reduction in size", new Object[] { path, contents.length(), compiledContents.length(), reductionPercentage });

                    if ((compiledContents.length() > 0) && (reduction > 5.0f)) {
                        JAVASCRIPT_CACHE.put(path.toLowerCase(), compiledContents);
                    } else {
                        JAVASCRIPT_CACHE.put(path.toLowerCase(), contents);
                    }
                } catch (Exception exception) {
                    LOGGER.error("{} caught while running closure compile on path [{}]", new Object[] { exception.getClass().getSimpleName(), path }, exception);
                }
            } else {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("[Rift] Javascript [{}] is already compiled, returning from cache", path);
                }
            }

            final String responseContent = JAVASCRIPT_CACHE.get(path.toLowerCase());
            response.setContentLength(responseContent.length());
            response.getWriter().write(responseContent);
            response.flushBuffer();
        }
    }

    private String loadFile(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder stringBuilder = new StringBuilder();

        String line;

        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        bufferedReader.close();

        return stringBuilder.toString();
    }
}
