package eu.zuinnote.example.springwebdemo.configuration.application.https;

import org.springframework.validation.annotation.Validated;

@Validated
public class HttpsHeaderConfig {
    private String permissionPolicy;
    private String csp;
    private String cspNonceFilterPath;
    private String cspNonceFilterValue;
    private String referrerPolicy;
    private String coep;
    private String coop;
    private String corp;

    public String getPermissionPolicy() {
        return permissionPolicy;
    }

    public void setPermissionPolicy(String permissionPolicy) {
        this.permissionPolicy = permissionPolicy;
    }

    public String getCsp() {
        return csp;
    }

    public void setCsp(String csp) {
        this.csp = csp;
    }

    public String getCspNonceFilterPath() {
        return cspNonceFilterPath;
    }

    public void setCspNonceFilterPath(String cspNonceFilterPath) {
        this.cspNonceFilterPath = cspNonceFilterPath;
    }

    public String getCspNonceFilterValue() {
        return cspNonceFilterValue;
    }

    public void setCspNonceFilterValue(String cspNonceFilterValue) {
        this.cspNonceFilterValue = cspNonceFilterValue;
    }

    public String getReferrerPolicy() {
        return this.referrerPolicy;
    }

    public void setReferrerPolicy(String referrerPolicy) {
        this.referrerPolicy = referrerPolicy;
    }

    public String getCoep() {
        return coep;
    }

    public void setCoep(String coep) {
        this.coep = coep;
    }

    public String getCoop() {
        return coop;
    }

    public void setCoop(String coop) {
        this.coop = coop;
    }

    public String getCorp() {
        return corp;
    }

    public void setCorp(String corp) {
        this.corp = corp;
    }
}
