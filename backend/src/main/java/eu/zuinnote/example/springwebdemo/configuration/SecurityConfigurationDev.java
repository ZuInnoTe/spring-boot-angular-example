package eu.zuinnote.example.springwebdemo.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import eu.zuinnote.example.springwebdemo.configuration.application.ApplicationConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/** Security Configuration for LOCAL development - not for deploying to any environment */
@Configuration
@EnableWebSecurity
@Profile("dev")
@Log4j2
public class SecurityConfigurationDev {

    @Autowired ApplicationConfig config;

    @Bean
    SecurityFilterChain app(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();

        // set the name of the attribute the CsrfToken will be populated on
        requestHandler.setCsrfRequestAttributeName("_csrf");
        // https://docs.spring.io/spring-security/reference/5.8/migration/servlet/exploits.html
        // Use only the handle() method of XorCsrfTokenRequestAttributeHandler and the
        // default implementation of resolveCsrfTokenValue() from CsrfTokenRequestHandler
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
                                        referrer ->
                                                referrer.policy(
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
        http.authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
                .formLogin(withDefaults())
                .csrf(
                        (csrf) ->
                                csrf.csrfTokenRepository(tokenRepository)
                                        .csrfTokenRequestHandler(requestHandler))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);
        ;
        return http.build();
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

    // the following is needed since Spring Boot 3.2:
    // https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.2-Release-Notes#auto-configured-user-details-service

    private static final String NOOP_PASSWORD_PREFIX = "{noop}";

    private static final Pattern PASSWORD_ALGORITHM_PATTERN = Pattern.compile("^\\{.+}.*$");

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(
            SecurityProperties properties, ObjectProvider<PasswordEncoder> passwordEncoder) {
        SecurityProperties.User user = properties.getUser();
        List<String> roles = user.getRoles();
        return new InMemoryUserDetailsManager(
                User.withUsername(user.getName())
                        .password(getOrDeducePassword(user, passwordEncoder.getIfAvailable()))
                        .roles(StringUtils.toStringArray(roles))
                        .build());
    }

    private String getOrDeducePassword(SecurityProperties.User user, PasswordEncoder encoder) {
        String password = user.getPassword();
        if (user.isPasswordGenerated()) {
            log.warn(
                    String.format(
                            "%n%nUsing generated security password: %s%n%nThis generated password"
                                + " is for development use only. Your security configuration must"
                                + " be updated before running your application in production.%n",
                            user.getPassword()));
        }
        if (encoder != null || PASSWORD_ALGORITHM_PATTERN.matcher(password).matches()) {
            return password;
        }
        return NOOP_PASSWORD_PREFIX + password;
    }
}
