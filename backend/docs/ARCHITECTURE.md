# Introduction
We give here only a small excerpt of the architecture of the backend of the Spring Boot application. You are encouraged to read the [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) and related documentation.

# JDK
We use the latest Long-Term-Support (LTS) releases with the latest patch level.

Currently we have configured Java JDK LTS 21.

# Spring Boot
We use the latest Spring Boot version with the latest patch level.

Spring Boot provides reasonable default configurations for production-grade Spring applications.

# Spring Modulith
[Spring Modulith](https://docs.spring.io/spring-modulith/reference/index.html) allows to modularize the application and reduce the coupling between code parts through use of events (not shown in the example). This makes it easier to refactor or replace logical application modules in the application faster and easier reducing the technical debt significantly.

Furthermore, it can generate documentation about the dependencies between application modules helping you to reduce the coupling between application modules to make them easier to maintain.

# Spring Actuator
[Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) contains a lot of out of the box tools to make monitoring your application easier.

For example, it exposes a health endpoint where other software components, such as monitoring solution or load balancer can check if your application is working as expected.

# Spring Data (Database)
[Spring Data](https://docs.spring.io/spring-data/jpa/reference/) provides a lot of functionality to ease working with the Jakarta Persistency API (JPA). You can code access to data in your code database independent using Java objects. You can then independent of your code configure the application to use a wide range of databases where the application during runtimes transparently stores/retrieves the data without the need to create custom database code.

There is much to read and understand about Spring Data.

However, you should also look how to tune its performance, e.g. using [FetchMode.Lazy](https://www.baeldung.com/hibernate-lazy-eager-loading) or configure [@BatchSize](https://www.baeldung.com/hibernate-fetchmode) on the entities/join relations.

# Configuration
There are many ways to enable configuration of the application using configuration files (see [here](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)).

We serialize all application-specific configuration in one central Java class (using @EnableConfigurationProperties), see [../src/main/java/eu/zuinnote/example/springwebdemo/configuration/application/ApplicationConfig.java](../src/main/java/eu/zuinnote/example/springwebdemo/configuration/application/ApplicationConfig.java). 

This makes it much easier to use always the same configuration properties for the same things and to validate configuration during startup.

This central Java class instantiated with the configuration values provided in a configuration file can then be easily injected in any Spring class by the following:
```
@Autowired ApplicationConfig config;
```

See also [CONFIGURE.md](./CONFIGURE.md)

# Single Page Application
The frontend is a Single Page Application (SPA) written in Angular. In Angular (and also in many other frameworks) the concept of routes exists, i.e. the URL is changed to redirect the user at the frontend to different frontend views (components). This leads to a situation, where the backend interprets those as requests to the backend, which is incorrect.

Hence, we needed to configure a special controller that makes sure that routes to frontend components are only redirected to the frontend.

See [../src/main/java/eu/zuinnote/example/springwebdemo/singlepage/SpringWebSPAController.java](../src/main/java/eu/zuinnote/example/springwebdemo/singlepage/SpringWebSPAController.java).

# Singletons
Web applications serve potentially many different requests by many different users. Often these requests need to use shared functionality or external services (e.g. calling an AWS Lambda function). Instead of instantiating these objects (e.g. clients to call AWS Lambda) every request you should do this once in a singleton, which is injected into the controller that serves the request. This significantly increases performance and reduces memory needs of the application.

We show in [../src/main/java/eu/zuinnote/example/springwebdemo/singletons/ApplicationServices.java](../src/main/java/eu/zuinnote/example/springwebdemo/singletons/ApplicationServices.java) how those objects can be only instantiated once.

Then you can easily autowire them in your controller to use them:
```
@Autowired SanitizerService sanitizerService;
```

Important: Singletons must be thread-safe as they are accessed by many different requests in parallel.

# Logging
We explicitly activated Spring Boot with log4j2. This allows to configure a lot of options for log4j2 (see [documentation](https://logging.apache.org/log4j/2.x/manual/configuration.html)).

By default we configured asynchronous logging for all loggers as according to the documentation this can increas logging performance 6x-48x. This is based on the [disruptor](https://lmax-exchange.github.io/disruptor/) library which is included in this application.

We defined this in [../src/main/resources/log4j2.component.properties](../src/main/resources/log4j2.component.properties)

It can make sense to configure certain loggers for certain classes/packages as synchronous (e.g. for audit logging). See the log4j2 documentation how to do this.
# Security
## General
We use Spring Boot Security defaults with [Spring Http Firewall](https://docs.spring.io/spring-security/reference/servlet/exploits/firewall.html) Strict mode (default).
## Input/Output Sanitization
We provide input/output sanitization mechanisms based on the [OWASP HTML Java Sanitizer](https://owasp.org/www-project-java-html-sanitizer/). This sanitization removes malicious HTML/scripts from any text input from the user/output to the user. The idea is that you can sanitize any user input (e.g. new orders) before storing it in the database and to sanitize anything that comes from the database before returning it to the users. By doing so you can protect your users and your business from any harm coming from cross-site-scripting attacks (e.g. in our demo case malicious orders from customers or leaking of orders of other customers).

One crucial aspect is where you do the sanitization. We configure the possible policies in a central singleton (see [../src/main/java/eu/zuinnote/example/springwebdemo/utility/SanitizerService.java](../src/main/java/eu/zuinnote/example/springwebdemo/utility/SanitizerService.java)). Then we provide in each DTO (e.g. [Product](../src/main/java/eu/zuinnote/example/springwebdemo/inventory/Product.java) or [Order](../src/main/java/eu/zuinnote/example/springwebdemo/order/Order.java)) a method sanitize that you can call when sanitization is needed (e.g. [InventoryController](../src/main/java/eu/zuinnote/example/springwebdemo/controller/InventoryController.java) or [OrderController](../src/main/java/eu/zuinnote/example/springwebdemo/controller/OrderController.java)). If you want to be very secure then you should not have a dedicated method sanitize in the DTO but sanitize in the controller, the get and setMethod directly. 

Currently we use the default policy to not allow any HTML or scripts in text fields. You can customize it to allow certain things (e.g. allow HTML formatting, but no scripts). See the OWASP HTML Sanitizer documentation for [prepackaged and custom policies](https://github.com/OWASP/java-html-sanitizer/tree/main?tab=readme-ov-file#prepackaged-policies).

***IMPORTANT: YOU ALWAYS NEED TO SANITZE IN BACKEND AND FRONTEND*** (see for frontend the [documentation](../../frontend/docs/ARCHITECTURE.md)).

Additionally, you should prevent SQL injection attacks. Normally Spring Data/JPA takes this into account, but if you do anything custom you may want to use the new [Spring JDBCClient](https://www.baeldung.com/spring-6-jdbcclient-api). This one automatically uses PreparedStatements to avoid SQL injection attacks (see [documentation](https://docs.spring.io/spring-boot/reference/data/sql.html#data.sql.jdbc-client)).
## Cross-Site Request Forgery (CSRF) token
We activated Cross-Site Request Forgery (CSRF) Protection (see [../src/main/java/eu/zuinnote/example/springwebdemo/configuration/](../src/main/java/eu/zuinnote/example/springwebdemo/configuration/)):
* We opt-in for additional [BREACH](https://en.wikipedia.org/wiki/BREACH) protection.
* We opt-out of deferred loading of CSRF token, ie the CSRF token is refreshed each page request. The reason is that it can cause some issues with certain authentication methods (e.g. SAML2) after returning from the IDP website (afterwards it would work normal).
* We make the CSRF token available in a Javascript-accessible token (see [here](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript)). Otherwise [XMLHttpRequests](https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest) (XHR) would not be possible. However, they are needed so that the frontend can talk with the backend. 

More information can be found in the [Spring Security CSRF documentation](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html).

Note: This is also configured in the frontend (see [../../frontend/docs/ARCHITECTURE.md](../../frontend/docs/ARCHITECTURE.md)), so it includes the CSRF token automatically in all requests.