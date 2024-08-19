package eu.zuinnote.example.springwebdemo.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import eu.zuinnote.example.springwebdemo.configuration.application.ApplicationConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
}
