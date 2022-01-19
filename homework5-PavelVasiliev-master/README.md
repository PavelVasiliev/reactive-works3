# Reactive Java Homework #5

![GitHub Classroom Workflow](../../workflows/GitHub%20Classroom%20Workflow/badge.svg?branch=master)

## Поиск уникальных слов с помощью Kafka Streams

### Формулировка

Используяю возможности Kafka Streams, считать данные из топика `input-topic`, найти уникальные слова (DISTINCT) и
вывести их в результирующий топик `distinct-words`. Т.е. в топике `distinct-words` каждое слово должно встречаться
только один раз.

Для разбиения строки на слова использовать regex `((\\b[^\\s]+\\b)((?<=\\.\\w).)?)`.

### Сборка и прогон тестов

```shell
./gradlew clean build
```

### Запуск Kafka + ksql-server

```shell
$ docker compose up
[+] Running 6/6
 ⠿ Network rxjava-homework5_default  Created                                                                                                                                              0.3s
 ⠿ Container zookeeper               Started                                                                                                                                              0.9s
 ⠿ Container kafka                   Started                                                                                                                                              2.0s
 ⠿ Container kowl                    Started                                                                                                                                              3.3s
 ⠿ Container ksqldb-server           Started 
 ⠿ Container ksqldb-cli              Started

$ docker exec -it ksql-cli ksql http://ksqldb-server:8088
```

### Прием домашнего задания

Как только тесты будут успешно пройдены, в Github Classroom на dashboard появится отметка об успешной сдаче. Так же в
самом репозитории появится бейдж со статусом сборки.