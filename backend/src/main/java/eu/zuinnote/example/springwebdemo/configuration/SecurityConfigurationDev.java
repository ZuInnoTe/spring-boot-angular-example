package eu.zuinnote.example.springwebdemo.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import eu.zuinnote.example.springwebdemo.configuration.application.ApplicationConfig;
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
import org.springframework.util.StringUtils;

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
        this.log.info(
                "Configuring application security for development - Do NOT use in production");
        this.generalSecurityConfiguration.setGeneralHttpSecurityConfiguration(http);

        http.authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
                .formLogin(withDefaults());

        return http.build();
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
