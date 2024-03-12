package eu.zuinnote.example.springwebdemo.configuration.application.oidc;

import org.springframework.validation.annotation.Validated;

@Validated
public class OidcConfiguration {
    private OidcMapper mapper;

    public OidcMapper getMapper() {
        return this.mapper;
    }

    public void setMapper(OidcMapper mapper) {
        this.mapper = mapper;
    }
}
