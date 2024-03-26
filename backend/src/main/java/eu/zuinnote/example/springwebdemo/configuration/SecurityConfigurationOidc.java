package eu.zuinnote.example.springwebdemo.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import eu.zuinnote.example.springwebdemo.configuration.application.ApplicationConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
@Profile("oidc")
@Log4j2
public class SecurityConfigurationOidc {
    @Autowired ApplicationConfig config;

    /***
     * We configure here usage of OIDC. Most of it is auto-configuration from the OIDC provider in Spring.
     *
     *
     **/
    @Bean
    SecurityFilterChain app(HttpSecurity http) throws Exception {
        this.log.info("Configuring application security for OIDC");
        // oidc
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2Login(withDefaults());
        // security headers
        // csp
        http.headers(
                        headers ->
                                headers.contentSecurityPolicy(
                                        csp ->
                                                csp.policyDirectives(
                                                        config.getHttps().getHeaders().getCsp())))
                // permission policy
                .headers(
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
                                        permissions ->
                                                permissions.policy(
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

        // Activate CSRF - we need for Angular currently the CSFR token also in a Javascript
        // accessible cookie
        // currently we opt in to BREACH protection and opt out to defer loading. The reason is that
        // otherwise we receive a CSFR 403 issue after the first post by Javascript after saml login
        // https://docs.spring.io/spring-security/reference/features/exploits/csrf.html

        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();

        // set the name of the attribute the CsrfToken will be populated on
        delegate.setCsrfRequestAttributeName("_csrf");

        // Use only the handle() method of XorCsrfTokenRequestAttributeHandler and the
        // default implementation of resolveCsrfTokenValue() from CsrfTokenRequestHandler
        CsrfTokenRequestHandler requestHandler = delegate::handle;

        http.csrf(
                        (csrf) ->
                                csrf.csrfTokenRepository(tokenRepository)
                                        .csrfTokenRequestHandler(requestHandler))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);
        return http.build();
    }

    // We have to add this filter to refresh the CSFR token every time - otherwise the first post
    // after OIDC login will fail
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
