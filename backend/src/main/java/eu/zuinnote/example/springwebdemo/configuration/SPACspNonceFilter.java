package eu.zuinnote.example.springwebdemo.configuration;

import eu.zuinnote.example.springwebdemo.configuration.application.ApplicationConfig;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public final class SPACspNonceFilter implements Filter {

    private static final String NONCE_PREFIX = "nonce-";
    private static final int NONCE_SIZE = 64;
    private final SecureRandom secureRandom = new SecureRandom();
    private Pattern[] cspNonceFilterPathPatterns;

    @Autowired ApplicationConfig config;

    private Pattern[] cacheFilterPathPatterns() {
        if (this.cspNonceFilterPathPatterns == null) {
            // precompile path patterns to improve performance
            String[] cspNonceFilterPath = config.getHttps().getHeaders().getCspNonceFilterPath();
            if ((cspNonceFilterPath != null) && (cspNonceFilterPath.length > 0)) {
                this.cspNonceFilterPathPatterns = new Pattern[cspNonceFilterPath.length];
                for (int i = 0; i < cspNonceFilterPath.length; i++) {
                    this.cspNonceFilterPathPatterns[i] = Pattern.compile(cspNonceFilterPath[i]);
                }
            } else {
                this.cspNonceFilterPathPatterns = new Pattern[0];
            }
        }
        return this.cspNonceFilterPathPatterns;
    }

    @Override
    public void doFilter(
            final ServletRequest request,
            final ServletResponse response,
            final FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        // generate a new secure nonce every request to the frontend (either to root frontend
        // application or specific application  components)
        // try to filter all frontend endpoints of the SPA (NOT! the backend), e.g. /, /ui/*
        if ((this.cacheFilterPathPatterns().length > 0) && (req.getServletPath() != null)) {
            boolean matchPath = false;
            for (Pattern currentFilterPathPattern : this.cspNonceFilterPathPatterns) {

                if (currentFilterPathPattern.matcher(req.getServletPath()).matches()) {
                    matchPath = true;
                    break;
                }
            }

            ContentCachingResponseWrapper resWrapper = new ContentCachingResponseWrapper(res);
            if (matchPath) {
                PrintWriter resWriter = resWrapper.getWriter();
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        new ClassPathResource(
                                                        config.getHttps()
                                                                .getHeaders()
                                                                .getCspSPAPage())
                                                .getInputStream())); // read the single HTML page of
                // the SPA (e.g.
                // public/index.html)

                byte[] nonceArray = new byte[NONCE_SIZE];
                secureRandom.nextBytes(nonceArray);
                // create a string out of it
                String nonce = Base64.getEncoder().encodeToString(nonceArray);
                String line = "";
                while ((line = reader.readLine()) != null) {
                    line =
                            line.replace(
                                    config.getHttps().getHeaders().getCspNonceFilterValue(),
                                    nonce); // replace what is configured with the nonce in the SPA
                    // html page (e.g. replace all occurrences of
                    // ${cspNonce]})
                    resWriter.write(line);
                }
                // update CSP
                String cspHeaderValue =
                        config.getHttps()
                                .getHeaders()
                                .getCsp()
                                .replace("script-src", "script-src '" + NONCE_PREFIX + nonce + "'")
                                .replace("style-src", "style-src '" + NONCE_PREFIX + nonce + "'");
                resWrapper.setHeader("Content-Security-Policy", cspHeaderValue);

                resWrapper.setHeader("Content-Type", "text/html");

                resWrapper.copyBodyToResponse();
                filterChain.doFilter(req, resWrapper);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
