package eu.zuinnote.example.springwebdemo.configuration.application.oidc;

import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.validation.annotation.Validated;

@Validated
public class OidcMapper {
    private String authoritiesPrefix;
    private ArrayList<String> jwtIdTokenClaims;
    private ArrayList<String> userClaims;
    private ArrayList<String> userAttributes;
    private HashMap<String, String> claimsSeparatorMap;

    public String getAuthoritiesPrefix() {
        return this.authoritiesPrefix;
    }

    public void setAuthoritiesPrefix(String authoritiesPrefix) {
        this.authoritiesPrefix = authoritiesPrefix;
    }

    public HashMap<String, String> getClaimsSeparatorMap() {
        return this.claimsSeparatorMap;
    }

    public void setClaimsSeparatorMap(HashMap<String, String> claimsSeparatorMap) {
        this.claimsSeparatorMap = claimsSeparatorMap;
    }

    public ArrayList<String> getJwtIdTokenClaims() {
        return this.jwtIdTokenClaims;
    }

    public void setJwtIdTokenClaims(ArrayList<String> jwtIdTokenClaims) {
        this.jwtIdTokenClaims = jwtIdTokenClaims;
    }

    public ArrayList<String> getUserClaims() {
        return this.userClaims;
    }

    public void setUserClaims(ArrayList<String> userClaims) {
        this.userClaims = userClaims;
    }

    public ArrayList<String> getUserAttributes() {
        return this.userAttributes;
    }

    public void setUserAttributes(ArrayList<String> userAttributes) {
        this.userAttributes = userAttributes;
    }
}
