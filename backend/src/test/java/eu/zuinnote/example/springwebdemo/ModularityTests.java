package eu.zuinnote.example.springwebdemo;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/***
 *  Tests for Spring Modulith and create documentation of modules
 *
 */
public class ModularityTests {
    ApplicationModules modules = ApplicationModules.of(SpringwebdemoApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        new Documenter(modules).writeDocumentation();
    }
}
