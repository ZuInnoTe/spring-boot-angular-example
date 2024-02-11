package eu.zuinnote.example.springwebdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringwebdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringwebdemoApplication.class, args);
	}

}
