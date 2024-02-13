package eu.zuinnote.example.springwebdemo.utility;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class SanitizerService {

    public static final PolicyFactory NO_HTML = new HtmlPolicyBuilder().toFactory();
}
