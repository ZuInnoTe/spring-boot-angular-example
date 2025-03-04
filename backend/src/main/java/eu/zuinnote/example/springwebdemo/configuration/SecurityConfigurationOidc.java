package eu.zuinnote.example.springwebdemo.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import eu.zuinnote.example.springwebdemo.configuration.application.ApplicationConfig;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("oidc")
@Log4j2
public class SecurityConfigurationOidc {
    @Autowired ApplicationConfig config;
    @Autowired GeneralSecurityConfiguration generalSecurityConfiguration;

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
        // set HTTP security headers
        this.generalSecurityConfiguration.setGeneralHttpSecurityConfiguration(http);
        // automatically redirect from HTTP to HTTPS
        this.generalSecurityConfiguration.setRequireSecure(http);
        return http.build();
    }

    /*
     * Custom OIDC claim to Spring GrantedAuthority  so that they can be used natively in Spring.
     *
     * Extracts OIDC claims from the IdToken, from the UserInfo Endpoint and the user attributes
     *
     * See: https://docs.spring.io/spring-security/reference/servlet/oauth2/login/advanced.html#oauth2login-advanced-map-authorities
     *
     */
    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(
                    authority -> {
                        if (OidcUserAuthority.class.isInstance(authority)) {
                            OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;

                            OidcIdToken idToken = oidcUserAuthority.getIdToken();
                            OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();

                            // map all claims from the IdToken
                            for (String idTokenClaim :
                                    this.config.getOidc().getMapper().getJwtIdTokenClaims()) {
                                Object claim = idToken.getClaim(idTokenClaim);
                                mappedAuthorities.addAll(
                                        this.parseClaim("IdToken", idTokenClaim, claim));
                            }
                            // map all claims from the UserInfo Endpoint
                            for (String userEndpointClaim :
                                    this.config.getOidc().getMapper().getUserClaims()) {
                                Object claim = userInfo.getClaim(userEndpointClaim);
                                mappedAuthorities.addAll(
                                        this.parseClaim(
                                                "UserInfo Endpoint", userEndpointClaim, claim));
                            }

                        } else if (OAuth2UserAuthority.class.isInstance(authority)) {
                            OAuth2UserAuthority oauth2UserAuthority =
                                    (OAuth2UserAuthority) authority;

                            Map<String, Object> userAttributes =
                                    oauth2UserAuthority.getAttributes();

                            // Map the attributes found in userAttributes
                            for (String userAttribute :
                                    this.config.getOidc().getMapper().getUserAttributes()) {
                                Object claim = userAttributes.get(userAttribute);
                                mappedAuthorities.addAll(
                                        this.parseClaim("User Attributes", userAttribute, claim));
                            }
                        }
                    });
            if (this.log.isDebugEnabled()) {
                for (GrantedAuthority grantedAuthority : mappedAuthorities) {
                    this.log.debug("Found authority {}", grantedAuthority.getAuthority());
                }
            }
            return mappedAuthorities;
        };
    }

    /* Parses a claim and converts it to a set of GrantedAuthority
     *
     * @type from where claim comes from (e.g. IdToken, UserInfoEndpoint, UserAtttribute)
     * @claim name of the claim (e.g. scope)
     * @claimValue value of the claim
     *
     */
    private Set<GrantedAuthority> parseClaim(String type, String claim, Object claimValue) {
        Set<GrantedAuthority> result = new HashSet<>();
        String authorityPrefix = this.config.getOidc().getMapper().getAuthoritiesPrefix();
        if (claimValue != null) {
            if (claimValue instanceof String) {
                HashMap<String, String> separatorMap =
                        this.config.getOidc().getMapper().getClaimsSeparatorMap();
                if ((separatorMap != null)
                        && separatorMap.containsKey(
                                claim)) { // check if we should parse a list from the claim
                    String separator =
                            this.config.getOidc().getMapper().getClaimsSeparatorMap().get(claim);

                    result.addAll(
                            Arrays.asList(claimValue.toString().split(separator)).stream()
                                    .map(s -> authorityPrefix + s)
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toCollection(HashSet::new)));
                } else {
                    result.add(new SimpleGrantedAuthority(authorityPrefix + claimValue.toString()));
                }

            } else if (claimValue
                    instanceof Collection) { // claim is already a list so simply converted them to
                // GrantedAuthority
                result.addAll(
                        ((Collection<?>) claimValue)
                                .stream()
                                        .map(Object::toString)
                                        .map(s -> authorityPrefix + s)
                                        .map(SimpleGrantedAuthority::new)
                                        .collect(Collectors.toCollection(HashSet::new)));
            } else { // unknown type of claim cannot be processed
                this.log.error("Claim {} in {} has an unknown type", claim, type);
            }
        }
        return result;
    }
}
