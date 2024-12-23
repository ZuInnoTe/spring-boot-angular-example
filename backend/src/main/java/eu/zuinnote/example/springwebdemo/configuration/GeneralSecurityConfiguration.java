package eu.zuinnote.example.springwebdemo.configuration;

import eu.zuinnote.example.springwebdemo.configuration.application.ApplicationConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Log4j2
public class GeneralSecurityConfiguration {

    @Autowired ApplicationConfig config;

    /**
     * We configure here general http security configuration, such as Csrf and HTTP security headers
     *
     * @param http
     * @throws Exception
     */
    public void setGeneralHttpSecurityConfiguration(HttpSecurity http) throws Exception {
        this.setCsrf(http);
        this.setHttpSecurityHeaders(http);
    }

    /*
     * Automatic redirect from HTTP to HTTPS
     *
     * @param http
     * @throws Exception
     */
    public void setRequireSecure(HttpSecurity http) throws Exception {
        http.requiresChannel((requiresChannel) -> requiresChannel.anyRequest().requiresSecure());
    }

    private void setHttpSecurityHeaders(HttpSecurity http) throws Exception {
        // HTTP Security Filters
        // security headers
        // csp
        http.headers(
                headers ->
                        headers.contentSecurityPolicy(
                                csp ->
                                        csp.policyDirectives(
                                                config.getHttps().getHeaders().getCsp())));
        // permission policy
        http.headers(
                        headers ->
                                headers.permissionsPolicy(
                                        permissions ->
                                                permissions.policy(
                                                        config.getHttps()
                                                                .getHeaders()
                                                                .getPermissionPolicy())))
                // referrer policy
                .headers(
                        headers ->
                                headers.referrerPolicy(
                                        referrer ->
                                                referrer.policy(
                                                        ReferrerPolicy.get(
                                                                config.getHttps()
                                                                        .getHeaders()
                                                                        .getReferrerPolicy()))))
                // Cross Origin Embedder Policy (COEP)
                .headers(
                        headers ->
                                headers.addHeaderWriter(
                                        new StaticHeadersWriter(
                                                "Cross-Origin-Embedder-Policy",
                                                config.getHttps().getHeaders().getCoep())))
                // Cross Origin Opener Policy (COOP)
                .headers(
                        headers ->
                                headers.addHeaderWriter(
                                        new StaticHeadersWriter(
                                                "Cross-Origin-Opener-Policy",
                                                config.getHttps().getHeaders().getCoop())))

                // Cross Origin Resource Policy (CORP)
                .headers(
                        headers ->
                                headers.addHeaderWriter(
                                        new StaticHeadersWriter(
                                                "Cross-Origin-Resource-Policy",
                                                config.getHttps().getHeaders().getCorp())));
    }

    private void setCsrf(HttpSecurity http) throws Exception {
        // Cross Site Request Forgery (CSRF)
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();

        // set the name of the attribute the CsrfToken will be populated on
        requestHandler.setCsrfRequestAttributeName("_csrf");
        // https://docs.spring.io/spring-security/reference/5.8/migration/servlet/exploits.html
        // Use only the handle() method of XorCsrfTokenRequestAttributeHandler and the
        // default implementation of resolveCsrfTokenValue() from CsrfTokenRequestHandler
        http.csrf(
                        (csrf) ->
                                csrf.csrfTokenRepository(tokenRepository)
                                        .csrfTokenRequestHandler(requestHandler))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);
    }

    private static final class CsrfCookieFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(
                HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            // Render the token value to a cookie by causing the deferred token to be loaded
            csrfToken.getToken();

            filterChain.doFilter(request, response);
        }
    }
}
