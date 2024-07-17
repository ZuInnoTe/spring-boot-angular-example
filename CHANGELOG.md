# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Changed
* Backend: Update to [Spring Boot 3.3.1](https://spring.io/blog/2024/06/20/spring-boot-3-3-1-available-now), [Spring Modulith 1.2.1](https://spring.io/blog/2024/06/21/spring-modulith-1-1-6-and-1-2-1-released)
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



