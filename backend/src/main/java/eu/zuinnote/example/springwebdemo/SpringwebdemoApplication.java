package eu.zuinnote.example.springwebdemo;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableSpringDataWebSupport(
        pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class SpringwebdemoApplication {

    public static void main(String[] args) {
        java.security.Security.addProvider(
                new BouncyCastleProvider()); // Needed for SAML RIPEMD160 provider
        SpringApplication.run(SpringwebdemoApplication.class, args);
    }
}
