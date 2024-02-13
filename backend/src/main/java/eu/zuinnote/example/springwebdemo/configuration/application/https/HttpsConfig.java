package eu.zuinnote.example.springwebdemo.configuration.application.https;

import org.springframework.validation.annotation.Validated;

@Validated
public class HttpsConfig {
    private HttpsHeaderConfig headers;

    public HttpsHeaderConfig getHeaders() {
        return headers;
    }

    public void setHeaders(HttpsHeaderConfig headers) {
        this.headers = headers;
    }
}
