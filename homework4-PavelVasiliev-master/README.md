# Reactive Java Homework #4

![GitHub Classroom Workflow](../../workflows/GitHub%20Classroom%20Workflow/badge.svg?branch=master)

## RSS news feed

### Формулировка

С помощью Spring WebFlux реализовать сервис для работы с RSS. Сервис должен поддерживать следующие операции:

1. `POST {{baseUrl}}/api/v1/rss/{{name}}/subscribe` – подписаться на RSS рассылку (поддерживаемый формат RSS 2.0). С
   помощью Spring WebClient получаем `channel` и все `items` из ленты и сохраняем информацию них в Базу. В `item`
   поля `title` и `description` могут приходить с разметкой HTML (обернуты в CDATA), поэтому перед сохранением в базу
   эти поля надо прогнать через HTML -> plain text конвертер (используется [Jsoup](https://jsoup.org)). Если подписка с
   таким именем уже существует, то вернуть 409 Conflict.
1. `GET {{baseUrl}}/api/v1/rss/{{name}}/updates` – получить обновления по существующей подписке. Из базы данных
   достается подписка с таким именем, по ссылке получаем всю ленту и отдаем только _новые_ записи. Т.е. делаем запрос в
   базу данных, получаем все `items` и сравниваем их с теми, которые получили в ответе. Если подписка с таким именем
   отсутствует, то вернуть 404 Not Found.
1. `DELETE {{baseUrl}}/api/v1/rss/{{name}}/updates` – отписаться от RSS рассылки и удалить все связанные с ней записи.
   Если подписка с таким именем отсутствует, то вернуть 404 Not Found.

### Требования и пояснения

1. Для работы с базой данных использовать Postgres
   и [R2DBC](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/#reference) драйвер.
1. Для обработки запросов использовать `RouterFunctions` и все шаги описывать в функциональном стиле.
1. Для локальной разработки можно поднять `Postgres` локально в Docker, для этого нужно выполнить `docker compose up`.
1. Для тестов используется `TestContainers`, поэтому для успешного прохождения тестов на локальной машине должен быть
   установлен Docker.
1. Для создания таблиц базы данных используется [Flyway](https://flywaydb.org/documentation/), но он не умеет работать с
   R2DBC драйвером, поэтому для него используется классический драйвер `Postgres`. Аналогичные настройки выполняются для
   тестов, но там перед созданием `DataSource` и `ConnectionFactory` ожидаем поднятия контейнера с `Postgres`.

### Сборка и прогон тестов

```shell
$ docker run hello-world 

Hello from Docker!
This message shows that your installation appears to be working correctly.

$ ./gradlew clean build
```

### Прием домашнего задания

Как только тесты будут успешно пройдены, в Github Classroom на dashboard появится отметка об успешной сдаче. Также в
самом репозитории появится бейдж со статусом сборки.

## Ссылки

1. [Web on Reactive Stack](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
1. [Спецификация RSS 2.0](https://www.internet-technologies.ru/articles/specifikaciya-rss-2-0.html)