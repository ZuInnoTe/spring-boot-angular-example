package eu.zuinnote.example.springwebdemo.configuration.application;

import eu.zuinnote.example.springwebdemo.configuration.application.https.HttpsConfig;
import eu.zuinnote.example.springwebdemo.configuration.application.saml2.Saml2Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "application")
@Validated
public class ApplicationConfig {

    private HttpsConfig https;

    private Saml2Configuration saml2;

    public HttpsConfig getHttps() {
        return https;
    }

    public void setHttps(HttpsConfig https) {
        this.https = https;
    }

    public Saml2Configuration getSaml2() {
        return saml2;
    }

    public void setSaml2(Saml2Configuration saml2) {
        this.saml2 = saml2;
    }
}
