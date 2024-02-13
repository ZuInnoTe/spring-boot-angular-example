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

    @RequestMapping({"/ui/**"})
    public String delegateToClient() {
        return "forward:/index.html";
    }
}
