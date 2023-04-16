# Birthday for Friends API Server
The API server provides REST APIs for managing friends and send email/notification for friend's birthday.

## Stack Dependencies
- Spring Boot 3.0.5
- Java 17
- MySQL 8.x.x

### Update Configuration
##### File to update: ./src/main/resources/application.properties
Database configuration:

```
database.host=[your db url]
database.name=[your db name]
database.username=[you db username]
database.password=[your db password]
```

### Installing/Testing

Run the below command to build/run unit-test on the project:

```
mvn clean install
```

### Run up project

Run up the project on default port 8080

```
mvn spring-boot:run
```
