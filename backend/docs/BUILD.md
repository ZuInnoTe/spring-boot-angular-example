## Application
tbd

## Integrate d
## Update Dependencies
It is very important to keep your dependencies up-to-date to make your project secure and maintainable.

You can check if there is an update to your dependencies by running:
```
./gradlew dependencyUpdates
```

The command has also other features (e.g. generate a report in different formats). Please check directly [its documentation](https://github.com/ben-manes/gradle-versions-plugin).

Note: If your dependency has reached end-of-life (EOL), ie it is not maintained anymore, then this will command will NOT inform you. You need then check regularly if you dependencies have reached end-of-life and find alternatives yourself.