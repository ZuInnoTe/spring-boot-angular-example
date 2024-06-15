package eu.zuinnote.example.springwebdemo.singlepage;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Redirects client-side routing requests of Single Page Applications, such as Angular Router, to
 * the client and do not process them on the server
 */
@Controller
@Log4j2
public class SpringWebSPAController {

    SpringWebSPAController() {}

    /*** This controller is triggered if an Angular UI component (via Angular route that we configured on /ui/**) is called. The goal is that the Angular component is shown and not a call to the backend is executed, e.g. if user calls in browser https://localhost:8080/ui/inventory directly to go directly to the inventory component) */
    @RequestMapping({"/ui/**"})
    public String delegateToClient() {
        return "forward:/index.html";
    }
}
