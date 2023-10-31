Данный проект является исходниками для веб-приложения, обеспечивающего работу системы управления предприятием зельеварения.

В проекте используется Java 17

# Deploy instructions:
Файл с настройками (src/main/resources/application.properties):
* spring.datasource.url= jdbc:postgresql://localhost:5432/studs (драйвер_субд:субд://IP-адрес:порт/имя_бд)
* spring.datasource.username=s371430 (имя владельца бд)
* spring.datasource.password=KDI1S1Q3UbLpAnZV (пароль владельца бд)

Для сборки проекта используйте Maven. В папке target появятся скомпилированные .class файлы, на основе .java файлов из src.
Для публикации проекта используйте команду jar для Maven - благодаря этому будет сгенерирован .jar файл

Для отправки проекта на сервер используйте аккаунт s371430 с паролем ADuw+2425 и команду scp (флаг -r нужен для отправки папки и её содержимого):
scp -P 2222 -r ISPotion s371430@helios.cs.ifmo.ru:/home/studs/s371430/

Для запуска проекта на сервере используйте аккаунт s371430 с паролем ADuw+2425 и ssh подключение к гелиосу на 2222 порту:
ssh ssh://s371430@helios.cs.ifmo.ru:2222
Созданный .jar файл можно запустить стандартными средствами:
java -jar target/ISPotion-0.0.1-SNAPSHOT.jar

На нарушение правил безопасности и прочее пока забил, ибо проект для учёбы, да и закрытый

Если нужно, сделаю для вас docker-контейнер - только напишите

# Read Me First
The following was discovered as part of building this project:

* The JVM level was changed from '11' to '17', review the [JDK Version Range](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-Versions#jdk-version-range) on the wiki for more details.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.0/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.0/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.1.0/reference/htmlsingle/#web)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)