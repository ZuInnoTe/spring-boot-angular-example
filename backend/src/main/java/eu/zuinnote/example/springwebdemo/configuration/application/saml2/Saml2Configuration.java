package eu.zuinnote.example.springwebdemo.configuration.application.saml2;

import org.springframework.validation.annotation.Validated;

@Validated
public class Saml2Configuration {
    private boolean enableMetadataEndpoint;

    public boolean getEnableMetadataEndpoint() {
        return enableMetadataEndpoint;
    }

    public void setEnableMetadataEndpoint(boolean enableMetadataEndpoint) {
        this.enableMetadataEndpoint = enableMetadataEndpoint;
    }
}
