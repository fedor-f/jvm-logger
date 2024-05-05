# JVM logger info

.jar files are located in the [artifacts directory](https://github.com/fedor-f/jvm-logger/tree/main/artifacts).<br>
To build project manually you will need to do the following steps:
1. Clone the repository.
2. Run ```mvn clean package``` in the root folder.
3. After building the project successfully you will get .jar files in the target folders of the cli-app and gui-app modules.

The whole projects consists of different modules. <br>
## 1. [jvm-logger-core](https://github.com/fedor-f/jvm-logger/tree/main/jvm-logger-core)
The module contains the event processing business logic including API package that helps developers to integrate the library with UI applications.
## 2. [cli-app](https://github.com/fedor-f/jvm-logger/tree/main/cli-app)
Command Line Interface application to collect JVM events.
## 3. [gui-app](https://github.com/fedor-f/jvm-logger/tree/main/gui-app)
JavaFX GUI application to collect JVM events.
