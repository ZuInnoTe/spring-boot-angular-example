package eu.zuinnote.example.springwebdemo.configuration.application.saml2;

import org.springframework.validation.annotation.Validated;

@Validated
public class Saml2Configuration {
    private boolean enableMetadataEndpoint;
    private String samlRoleAttributeName;
    private String samlRoleAttributeSeparator;

    public boolean getEnableMetadataEndpoint() {
        return enableMetadataEndpoint;
    }

    public void setEnableMetadataEndpoint(boolean enableMetadataEndpoint) {
        this.enableMetadataEndpoint = enableMetadataEndpoint;
    }

    public String getSamlRoleAttributeName() {
        return this.samlRoleAttributeName;
    }

    public void setSamlRoleAttributeName(String samlRoleAttributeName) {
        this.samlRoleAttributeName = samlRoleAttributeName;
    }

    public String getSamlRoleAttributeSeparator() {
        return this.samlRoleAttributeSeparator;
    }

    public void setSamlRoleAttributeSeparatorString(String samlRoleAttributeSeparator) {
        this.samlRoleAttributeSeparator = samlRoleAttributeSeparator;
    }
}
