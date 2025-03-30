package eu.zuinnote.example.springwebdemo.configuration.application.https;

import org.springframework.validation.annotation.Validated;

@Validated
public class HttpsConfig {
    private boolean disableRedirectHttps = false;
    private HttpsHeaderConfig headers;

    public boolean getDisableRedirectHttps() {
        return disableRedirectHttps;
    }

    public void setDisableRedirectHttps(boolean disableRedirectHttps) {
        this.disableRedirectHttps = disableRedirectHttps;
    }

    public HttpsHeaderConfig getHeaders() {
        return headers;
    }

    public void setHeaders(HttpsHeaderConfig headers) {
        this.headers = headers;
    }
}
