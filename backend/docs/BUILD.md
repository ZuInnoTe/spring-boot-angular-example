# Introduction - Build

# Prerequisites
You need to have at least JDK 21 (LTS) installed.

Additionally, you need to fulfill the [needs of the frontend](../../frontend/docs/BUILD.md).

# Run application
You can run the application by entering the subfolder "backend" (if not already done) and executing
```
./gradlew bootRun
```

# Automated Code Formatting
During build the build script checks if the code is formatted according to standards defined in the build script. These standard are defined in the block "spotless".
You can find the possible options in the [Spotless for Gradle](https://github.com/diffplug/spotless/tree/main/plugin-gradle) webpage.

You can automatically fix the formatting of all of the code using
```
./gradlew spotlessApply
```
After it has been executed the build should not fail anymore due to code not formatted according to the defined standard.

# Generate Modulith Documentation & Module Testig
tbd

# Database
tbd

# Build application
tbd
# Run tests

tbd

# Manage Build Tool version - Gradle Wrapper

# Update Dependencies
It is very important to keep your dependencies up-to-date to make your project secure and maintainable.

You can check if there is an update to your dependencies by running:
```
./gradlew dependencyUpdates
```

The command has also other features (e.g. generate a report in different formats). Please check directly [its documentation](https://github.com/ben-manes/gradle-versions-plugin).

Note: If your dependency has reached end-of-life (EOL), ie it is not maintained anymore, then this will command will NOT inform you. You need then check regularly if you dependencies have reached end-of-life and find alternatives yourself.