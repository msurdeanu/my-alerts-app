# MyAlerts

**MyAlerts** is a simple tool for creating real-time alerts for monitoring your own services.
You can easily create alerts, schedule them using cron-jobs and trigger notifications when they are failing.

Uses Java as programming language and Vaadin as UI framework.

This tool is perfect for small teams, mainly composed by developers. Any developer with basic Javascript knowledge can create and maintain alerts created with MyAlerts.
Since alert definition is in pure Javascript, the power of the tool is huge and offers the freedom to do almost everything you want.

## Technology stack

* Java 11 as programming language. GraalVM distribution is required to run this application.
* [GraalVM](https://www.graalvm.org/) - use JavaScript as additional language for defining alerts
* Spring Boot 2.6.x as dependency injection framework
* [Vaadin 23](https://vaadin.com/) as UI framework
* SQLite as relational database for persisting data like test scenarios and their results.

## Features

* Modern and responsive UI.
* **Authentication** and **authorization**. Multiple roles inside the application (simple users and administrators).
* Dedicated **page** for **creating, editing or removing alerts**. Code editor for alert definition with autocomplete activated by default.
* **Alert history** - ability to keep more results for each alert.
* Ability to **encrypt / decrypt sensitive stuff** in alerts using symmetric algorithms: AES.
* **Multiple channels** for sending alerts in case of an issue.
* **Settings page**. Provides ability to change application behavior at runtime without restarting application.
* **Statistics page**. Simple page with multiple statistics about current running instance.

## Running the application
There are two ways to run the application: using `mvn spring-boot:run` or by running the `Application` class directly from your IDE.

You can use any IDE of your preference, but I suggest Intellij IDEA.

## Structure

Vaadin web applications are full-stack and include both client-side and server-side code in the same project.

| Directory                                                 | Description                                           |
|:----------------------------------------------------------|:------------------------------------------------------|
| `frontend/`                                               | Client-side source directory                          |
| &nbsp;&nbsp;&nbsp;&nbsp;`themes/`                         | Themes directory (CSS)                                |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`simple/` | Default theme                                         |
| `src/main/java/org/myalerts`                              | Server-side source directory                          |
| &nbsp;&nbsp;&nbsp;&nbsp;`component/`                      | Package with all Vaadin custom components             |
| &nbsp;&nbsp;&nbsp;&nbsp;`config/`                         | Package with different Spring configuration beans     |
| &nbsp;&nbsp;&nbsp;&nbsp;`converter/`                      | Package with multiple persistence converters          |
| &nbsp;&nbsp;&nbsp;&nbsp;`dialect/`                        | Package with stuff related with SQLite database       |
| &nbsp;&nbsp;&nbsp;&nbsp;`domain/`                         | Package with all classes part of application domain   |
| &nbsp;&nbsp;&nbsp;&nbsp;`provider/`                       | Java package with a set of providers                  |
| &nbsp;&nbsp;&nbsp;&nbsp;`repository/`                     | Contains a set of JpaRepositories                     |
| &nbsp;&nbsp;&nbsp;&nbsp;`service/`                        | Contains a set of services exposed by the application |
| &nbsp;&nbsp;&nbsp;&nbsp;`Application.java`                | Server entrypoint                                     |
