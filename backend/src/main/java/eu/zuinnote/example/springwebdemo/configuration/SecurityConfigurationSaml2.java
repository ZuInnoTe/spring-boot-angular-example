package eu.zuinnote.example.springwebdemo.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import eu.zuinnote.example.springwebdemo.configuration.application.ApplicationConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSAnyImpl;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.filter.OncePerRequestFilter;

/** SAML2 Security Configuration. Use this when you deploy to an environment */
@Configuration
@EnableWebSecurity
@Profile("saml2")
@Log4j2
public class SecurityConfigurationSaml2 {

    @Autowired ApplicationConfig config;

    @Autowired RelyingPartyRegistrationRepository relyingPartyRegistrationRepository;

    /***
     * We configure here usage of SAML2. Most of it is auto-configuration from the SAML2 provider in Spring.
     *
     *
     **/
    @Bean
    SecurityFilterChain app(HttpSecurity http) throws Exception {
        this.log.info("Configuring application security for SAML2");
        // saml2 mapping of SAML attributes => Spring Authorities
        // Spring authorities make it easy then to configure authorization with expressions
        // (https://docs.spring.io/spring-security/reference/servlet/authorization/expression-based.html) or in other contexts

        OpenSaml4AuthenticationProvider authenticationProvider =
                new OpenSaml4AuthenticationProvider();
        authenticationProvider.setResponseAuthenticationConverter(
                responseToken -> {
                    Saml2Authentication authentication =
                            OpenSaml4AuthenticationProvider
                                    .createDefaultResponseAuthenticationConverter()
                                    .convert(responseToken);
                    Set<String> authorities =
                            AuthorityUtils.authorityListToSet(authentication.getAuthorities());
                    for (Assertion assertion : responseToken.getResponse().getAssertions()) {
                        for (AttributeStatement attributeStatement :
                                assertion.getAttributeStatements()) {
                            for (Attribute attribute : attributeStatement.getAttributes()) {
                                if (config.getSaml2()
                                        .getSamlRoleAttributeName()
                                        .equals(attribute.getName())) {
                                    for (XMLObject attributeValue :
                                            attribute.getAttributeValues()) {
                                        String attributeValueString =
                                                getSAML2AttributeValue(attributeValue);
                                        String[] attributeValueArray =
                                                attributeValueString.split(
                                                        config.getSaml2()
                                                                .getSamlRoleAttributeSeparator());
                                        for (String authorityString : attributeValueArray) {
                                            log.debug(
                                                    String.format(
                                                            "Adding authority: %s",
                                                            authorityString));
                                            authorities.add(authorityString);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Saml2Authentication revisedAuthentication =
                            new Saml2Authentication(
                                    (AuthenticatedPrincipal) authentication.getPrincipal(),
                                    authentication.getSaml2Response(),
                                    AuthorityUtils.createAuthorityList(
                                            authorities.toArray(new String[authorities.size()])));
                    return revisedAuthentication;
                });

        // saml2
        if (config.getSaml2().getEnableMetadataEndpoint()) {
            DefaultRelyingPartyRegistrationResolver relyingPartyRegistrationResolver =
                    new DefaultRelyingPartyRegistrationResolver(
                            this.relyingPartyRegistrationRepository);
            Saml2MetadataFilter filter =
                    new Saml2MetadataFilter(
                            relyingPartyRegistrationResolver, new OpenSamlMetadataResolver());
            http.authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
                    .saml2Login(withDefaults())
                    .authenticationManager(new ProviderManager(authenticationProvider))
                    .addFilterBefore(filter, Saml2WebSsoAuthenticationFilter.class)
                    .saml2Logout(withDefaults());
        } else {
            http.authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
                    .saml2Login(withDefaults())
                    .authenticationManager(new ProviderManager(authenticationProvider))
                    .saml2Logout(withDefaults());
        }

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
    // after SAML login will fail
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

    /**
     * Unpack an XMLObject representing an Attribute Value in a SAML Response into a String
     *
     * @param attributeValue original attribute Value in SAML
     * @return attribute Value as string
     */
    private String getSAML2AttributeValue(XMLObject attributeValue) {
        return attributeValue == null
                ? null
                : attributeValue instanceof XSString
                        ? getStringAttributeValue((XSString) attributeValue)
                        : attributeValue instanceof XSAnyImpl
                                ? getAnyAttributeValue((XSAnyImpl) attributeValue)
                                : attributeValue.toString();
    }

    /***
     * Extract the String of a attribute value if it is an XML String
     *
     * @param attributeValue original attributeValue as XML String
     * @return attribute as string
     */
    private String getStringAttributeValue(XSString attributeValue) {
        return attributeValue.getValue();
    }

    /**
     * Extract the String of a attribute value if it is an XML object. We assume here then that the
     * value is contained as text between the tags
     *
     * @param attributeValue original attributeValue as XML any value
     * @return attribute as string
     */
    private String getAnyAttributeValue(XSAnyImpl attributeValue) {
        return attributeValue.getTextContent();
    }
}
