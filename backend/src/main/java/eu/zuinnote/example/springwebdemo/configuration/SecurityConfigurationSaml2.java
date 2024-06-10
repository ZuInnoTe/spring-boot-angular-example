package eu.zuinnote.example.springwebdemo.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import eu.zuinnote.example.springwebdemo.configuration.application.ApplicationConfig;
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

/** SAML2 Security Configuration. Use this when you deploy to an environment */
@Configuration
@EnableWebSecurity
@Profile("saml2")
@Log4j2
public class SecurityConfigurationSaml2 {

    @Autowired ApplicationConfig config;
    @Autowired GeneralSecurityConfiguration generalSecurityConfiguration;

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

        this.generalSecurityConfiguration.setGeneralHttpSecurityConfiguration(http);
        return http.build();
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
