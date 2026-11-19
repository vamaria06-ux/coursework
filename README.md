# REST API Data Aggregator

## Описание проекта

Консольное Java-приложение для последовательного получения данных из нескольких открытых REST API, преобразования ответов в единую модель и сохранения результатов в форматах JSON и CSV.

Проект реализован в рамках первого этапа курсовой работы.

## Используемые технологии

- Java 27
- Maven
- Java HttpClient
- Jackson
- Apache Commons CSV
- SLF4J и Logback

## Источники данных

### Open-Meteo

API для получения погодных данных.

Адрес: https://open-meteo.com/en/docs

Параметры: `latitude`, `longitude`.

Получаемые данные: температура, единица измерения и время наблюдения.

### Open Library

API для поиска книг.

Адрес: https://openlibrary.org/dev/docs/api/search

Параметр: `q` — поисковый запрос.

Получаемые данные: название книги, автор и год первой публикации.

### Rick and Morty API

API для получения информации о персонажах.

Адрес: https://rickandmortyapi.com/documentation

Параметры: `name`, `status`.

Получаемые данные: имя, статус, вид и происхождение персонажа.

## Архитектура

Приложение разделено на компоненты:

- `ApiHttpClient` — HTTP-запросы.
- `DataSource` — общий интерфейс источников.
- `WeatherSource`, `BookSource`, `CharacterSource` — реализации источников.
- `WeatherMapper`, `BookMapper`, `CharacterMapper` — преобразование JSON в единую модель.
- `DataRecord` — общая модель данных.
- `SourceRegistry` — реестр источников.
- `DataAggregator` — последовательный опрос источников.
- `DataStorage` — интерфейс файлового хранилища.
- `JsonStorage`, `CsvStorage` — сохранение и чтение данных.
- `CliApplication` — обработка параметров командной строки.
- `ApplicationFactory` — создание и связывание зависимостей.

Новые источники добавляются через реализацию интерфейса `DataSource` и регистрацию в `ApplicationFactory`.

## Сборка

Требуется JDK 27 и Maven.

В корне проекта:

```bash
mvn clean package
```

Исполняемый файл после сборки:

`target/coursework-1.0-SNAPSHOT.jar`

## Запуск

### Получение и сохранение данных в JSON

```bash
java -jar target/coursework-1.0-SNAPSHOT.jar fetch --request "open-meteo?latitude=59.93&longitude=30.31" --format json --mode overwrite --file data/results.json
```

### Получение данных из нескольких API

```bash
java -jar target/coursework-1.0-SNAPSHOT.jar fetch --request "open-meteo?latitude=59.93&longitude=30.31" --request "open-library?q=harry%20potter" --request "rick-morty?name=morty&status=alive" --format csv --mode overwrite --file data/results.csv
```

### Дозапись

```bash
java -jar target/coursework-1.0-SNAPSHOT.jar fetch --request "open-meteo?latitude=55.75&longitude=37.62" --format csv --mode append --file data/results.csv
```

### Чтение всего файла

```bash
java -jar target/coursework-1.0-SNAPSHOT.jar read --format json --file data/results.json
```

### Чтение данных конкретного API

```bash
java -jar target/coursework-1.0-SNAPSHOT.jar read --format csv --file data/results.csv --source open-library
```

## Параметры командной строки

Команда `fetch`:

| Параметр | Описание |
|---|---|
| `--request` | Название API и параметры запроса. Может указываться несколько раз |
| `--format` | Формат `json` или `csv` |
| `--mode` | `overwrite` или `append` |
| `--file` | Путь к выходному файлу |

Команда `read`:

| Параметр | Описание |
|---|---|
| `--format` | Формат файла |
| `--file` | Путь к файлу |
| `--source` | Необязательный фильтр по источнику |

## Обработка ошибок

Приложение проверяет параметры командной строки и сообщает об ошибках через логгер.

Коды завершения:

- `0` — успешное выполнение.
- `1` — ошибка ввода-вывода.
- `2` — некорректные аргументы.
- `130` — прерывание операции.

## Особенности реализации

Опрос источников выполняется последовательно, в одном основном потоке.

При сохранении JSON и CSV поддерживаются два режима: перезапись и добавление записей к существующим данным.

Чтение может выполняться как для всех записей, так и с фильтрацией по API.
