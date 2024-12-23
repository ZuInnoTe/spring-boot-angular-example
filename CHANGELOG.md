# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.0.8] - 2024-12-23
### Changed
* Backend: update to [Spring Boot 3.4.1](https://spring.io/blog/2024/12/19/spring-boot-3-4-1-available-now), Hibernate 6.6.4.Final, Spring Modulith 1.3.1
* Backend: enable [Gradle Configuration Cache](https://docs.gradle.org/current/userguide/configuration_cache.html#config_cache:usage:parallel)
* Frontend: update to nodejs 22 LTS for the build process
* Frontend: Update [Angular 19.0.5](https://github.com/angular/angular/releases/tag/19.0.5)

## [0.0.7] - 2024-11-20
### Changed
* Backend: Update to Gradle 8.11.1, Gradle Plugin CycloneDX 1.10.0, Dependencies: H2 2.3.232, Hibernate 6.6.1.Final, [Spring Boot 3.3.4](https://spring.io/blog/2024/09/19/spring-boot-3-3-4-available-now), Spring Modulith 1.2.3
* Backend: Automated redirect from HTTP to HTTPs using [requireChannel/requireSecure in SpringBoot](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html) for any request. Note: This is complementary to a content-security-policy (CSP) with upgrade-insecure-requests. Both should be used.
* Frontend: Documentation: Change links to the new Angular.dev web page
* Frontend: Update [Angular 19.0.0](https://github.com/angular/angular/releases/tag/19.0.0)
* Frontend: add support for [Typedoc](https://typedoc.org/)

## [0.0.6] - 2024-07-23
### Added
* Backend: Sanitze data transfer object (DTOs) (e.g. product,order) to avoid that they contain malicious HTML/scripts. This is based on the [OWASP HTML Java Sanitizer](https://owasp.org/www-project-java-html-sanitizer/). See also [the documentation](./backend/docs/ARCHITECTURE.md). Note: The frontend in additions uses [Angular mechanism for sanitization](https://angular.dev/best-practices/security) and [content-security-policies](https://angular.dev/best-practices/security#content-security-policy) (CSP) without unsafe-* and [trusted types](https://angular.dev/best-practices/security#enforcing-trusted-types).
### Changed
* Frontend: Update to [Angular 18.1.x](https://github.com/angular/angular/releases/tag/18.1.1)
* Backend: Update to [Spring Boot 3.3.2](https://spring.io/blog/2024/07/18/spring-boot-3-3-2-available-now), [Spring Modulith 1.2.2](https://spring.io/blog/2024/06/21/spring-modulith-1-1-6-and-1-2-1-released)
* Backend: Update to [Gradle 8.9](https://docs.gradle.org/8.9/release-notes.html), Spring Gradle Dependency Plugin 1.1.6, h2 2.3.230


## [0.0.5] - 2024-06-24
### Added
* Backend: Make options for CSP script-src and style-src nonce more configurable. See [documentation](./backend/docs/CONFIGURE.md).
### Changed
* Backend: Register the BouncyCastleProvider for security algorithms (needed for SAML authentication). See [./backend/src/main/java/eu/zuinnote/example/springwebdemo/SpringwebdemoApplication.java](./backend/src/main/java/eu/zuinnote/example/springwebdemo/SpringwebdemoApplication.java).



## [0.0.4] - 2024-06-15
### Added
* Backend: Added [CycloneDX Gradle Plugin](https://github.com/CycloneDX/cyclonedx-gradle-plugin) to support [Software Bill of Material](https://en.wikipedia.org/wiki/Software_supply_chain) (SBOM). See also how you can [expose SBOMs in Spring Actuator](https://spring.io/blog/2024/05/24/sbom-support-in-spring-boot-3-3)
* Backend: Upgraded to [Gradle 8.8](https://docs.gradle.org/8.8/release-notes.html)
### Changed
* Backend: Change content security policy (CSP) to use [upgrade-insecure-requests](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy/upgrade-insecure-requests), because [block-all-mixed-content](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy/block-all-mixed-content) has been deprecated.
* Backend: Refactor SecurityConfiguration to avoid redundant code
* Backend/Frontend: CSP remove unsafe-inline from script-src and style-src and replace it with a [nonce-based security mechanism](https://angular.dev/best-practices/security#content-security-policy). This includes in the Backend a filter [SPACspNonceFilter.java](./backend/src/main/java/eu/zuinnote/example/springwebdemo/configuration/SPACspNonceFilter.java) that inejcts the nonce in the headers when requesting the Angular root component ("/") or directly an Angular frontend component ("/ui/*).


## [0.0.3] - 2024-05-25
### Changed
* Backend: Updated Spring Boot to 3.3.0, Spring Modulith 1.2.0, Hibernate 6.5.2.Final, Disruptor 4.0.0
* Frontend: Upgrade to Angular 18

## [0.0.2] - 2024-04-22
### Changed
* Backend: Updated Spring Boot to 3.2.5, Bouncycastle to 1.78.1

## [0.0.1] - 2024-03-30

### Added
* Initial version of the application with documentation



