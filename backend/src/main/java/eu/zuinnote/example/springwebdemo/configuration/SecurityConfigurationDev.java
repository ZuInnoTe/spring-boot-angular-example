package eu.zuinnote.example.springwebdemo.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import eu.zuinnote.example.springwebdemo.configuration.application.ApplicationConfig;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/** Security Configuration for LOCAL development - not for deploying to any environment */
@Configuration
@EnableWebSecurity
@Profile("dev")
@Log4j2
public class SecurityConfigurationDev {
    @Autowired ApplicationConfig config;
    @Autowired GeneralSecurityConfiguration generalSecurityConfiguration;

    @Bean
    SecurityFilterChain app(HttpSecurity http) throws Exception {
        this.log.warn(
                "Configuring application security for development - Do NOT use in production");
        this.generalSecurityConfiguration.setGeneralHttpSecurityConfiguration(http);

        http.authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
                .formLogin(withDefaults());

        return http.build();
    }

    /**
     * This is only for development purposes! It generates a random password and outputs it to the
     * console
     */
    @Bean
    public UserDetailsService devUsers() {

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String password = UUID.randomUUID().toString();
        log.warn(
                String.format(
                        "%n%nUsing generated security password: %s%n%nThis generated password"
                                + " is for development use only. Your security configuration must"
                                + " be updated before running your application in production.%n",
                        password));
        UserDetails user =
                User.withUsername("user").password(encoder.encode(password)).roles("USER").build();
        return new InMemoryUserDetailsManager(user);
    }
}
