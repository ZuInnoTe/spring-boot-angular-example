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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public final class SPACspNonceFilter implements Filter {

    private static final String NONCE_PREFIX = "nonce-";
    private static final int NONCE_SIZE = 64;
    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired ApplicationConfig config;

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
        if ((req.getServletPath() != null)
                && (("/".equals(req.getServletPath())
                        || (req.getServletPath().startsWith("/ui"))))) {

            ContentCachingResponseWrapper resWrapper = new ContentCachingResponseWrapper(res);

            PrintWriter resWriter = resWrapper.getWriter();
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    new ClassPathResource("public/index.html").getInputStream()));

            byte[] nonceArray = new byte[NONCE_SIZE];
            secureRandom.nextBytes(nonceArray);
            // create a string out of it
            String nonce = Base64.getEncoder().encodeToString(nonceArray);
            String line = "";
            while ((line = reader.readLine()) != null) {
                line = line.replace("${cspNonce}", nonce);
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

            filterChain.doFilter(request, resWrapper);
        } else {
            filterChain.doFilter(request, res);
        }
    }
}
