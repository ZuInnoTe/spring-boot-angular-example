package eu.zuinnote.example.springwebdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestSpringwebdemoApplication {

    public static void main(String[] args) {
        SpringApplication.from(SpringwebdemoApplication::main)
                .with(TestSpringwebdemoApplication.class)
                .run(args);
    }
}
