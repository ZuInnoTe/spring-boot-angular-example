package eu.zuinnote.example.springwebdemo.singletons;

import eu.zuinnote.example.springwebdemo.utility.SanitizerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationServices {
    @Bean
    SanitizerService getSanitizerservice() {
        return new SanitizerService();
    }
}
