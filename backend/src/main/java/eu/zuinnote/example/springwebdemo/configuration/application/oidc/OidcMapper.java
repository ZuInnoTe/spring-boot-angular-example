package eu.zuinnote.example.springwebdemo.configuration.application.oidc;

import java.util.ArrayList;
import org.springframework.validation.annotation.Validated;

@Validated
public class OidcMapper {
    private String authoritiesPrefix;
    private ArrayList<String> jwtRoleClaims;

    public String getAuthoritiesPrefix() {
        return this.authoritiesPrefix;
    }

    public void setAuthoritiesPrefix(String authoritiesPrefix) {
        this.authoritiesPrefix = authoritiesPrefix;
    }

    public ArrayList<String> getJwtRoleClaims() {
        return this.jwtRoleClaims;
    }

    public void setJwtRoleClaims(ArrayList<String> jwtRoleClaims) {
        this.jwtRoleClaims = jwtRoleClaims;
    }
}
