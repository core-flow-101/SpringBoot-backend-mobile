# Mobile Backend - Геймифицированная система обучения разработке

Backend приложение для мобильной платформы, где разработчики могут геймифицировать процесс обучения разработке и соревноваться между собой. Система позволяет пользователям работать с заданиями, зарабатывать баллы и отслеживать свой прогресс.

## Технологии

- **Spring Boot 3.2.0** - основной фреймворк
- **Spring Security** - безопасность и аутентификация
- **JWT (JSON Web Tokens)** - токены для аутентификации
- **Spring Data JPA** - работа с базой данных
- **PostgreSQL** - реляционная база данных
- **Liquibase** - управление миграциями базы данных
- **Docker & Docker Compose** - контейнеризация
- **Lombok** - упрощение кода

## Архитектура

Приложение состоит из следующих основных компонентов:

- **Аутентификация** - регистрация и вход пользователей с JWT токенами
- **Система задач** - управление заданиями пользователей
  - Текущие задачи пользователя (в работе)
  - Шаблоны задач (фиксированные задания)
  - Пользовательские задачи (созданные самим пользователем)
- **Система баллов** - начисление баллов за выполнение задач
- **Личный кабинет** - профиль пользователя с фото, логином, email и баллами
- **Лидерборд** - рейтинг пользователей по количеству набранных баллов

## Запуск приложения

### Вариант 1: С Docker Compose (рекомендуется)

```bash
docker-compose up -d
```

Приложение будет доступно по адресу: `http://localhost:8080`

### Вариант 2: Локально

1. Убедитесь, что PostgreSQL запущен на порту 5432
2. Создайте базу данных `mobile_auth`:
   ```sql
   CREATE DATABASE mobile_auth;
   ```
3. Запустите приложение:
   ```bash
   ./gradlew bootRun
   ```
   или на Windows:
   ```bash
   gradlew.bat bootRun
   ```

## API Endpoints

Все эндпоинты, кроме `/api/auth/**`, требуют аутентификации через JWT токен в заголовке `Authorization: Bearer <token>`.

---

## Аутентификация

### Регистрация

Создает нового пользователя и возвращает JWT токен.

**Запрос:**
```
POST /api/auth/register
Content-Type: application/json
```

**Тело запроса:**
```json
{
  "username": "developer123",
  "email": "developer@example.com",
  "password": "securePassword123"
}
```

**Ответ (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "developer123",
  "email": "developer@example.com"
}
```

**Ошибки:**
- `400 Bad Request` - если username или email уже существуют
- `400 Bad Request` - если данные не прошли валидацию

---

### Вход

Аутентифицирует существующего пользователя и возвращает JWT токен.

**Запрос:**
```
POST /api/auth/login
Content-Type: application/json
```

**Тело запроса:**
```json
{
  "username": "developer123",
  "password": "securePassword123"
}
```

**Ответ (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "developer123",
  "email": "developer@example.com"
}
```

**Ошибки:**
- `401 Unauthorized` - если неверный username или password

---

## API Задач

Все эндпоинты задач требуют аутентификации. Добавьте заголовок:
```
Authorization: Bearer <your-jwt-token>
```

---

### Получить текущие задачи пользователя

Возвращает список всех задач пользователя, которые находятся в статусе "В работе" (IN_PROGRESS).

**Запрос:**
```
GET /api/tasks/current
Authorization: Bearer <token>
```

**Ответ (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Изучить Spring Boot",
    "description": "Пройдите базовый курс по Spring Boot",
    "points": 50,
    "status": "IN_PROGRESS",
    "type": "FIXED",
    "createdBy": "developer123",
    "createdAt": "2024-01-15T10:30:00",
    "completedAt": null
  },
  {
    "id": 2,
    "title": "Создать REST API",
    "description": "Реализовать CRUD операции для пользователей",
    "points": 100,
    "status": "IN_PROGRESS",
    "type": "CUSTOM",
    "createdBy": "developer123",
    "createdAt": "2024-01-16T14:20:00",
    "completedAt": null
  }
]
```

**Ошибки:**
- `401 Unauthorized` - если токен отсутствует или невалиден

---

### Завершить задачу

Отмечает задачу как выполненную и начисляет пользователю баллы за задачу.

**Запрос:**
```
POST /api/tasks/{taskId}/complete
Authorization: Bearer <token>
```

**Параметры пути:**
- `taskId` (Long) - ID задачи

**Ответ (200 OK):**
```json
{
  "id": 1,
  "title": "Изучить Spring Boot",
  "description": "Пройдите базовый курс по Spring Boot",
  "points": 50,
  "status": "DONE",
  "type": "FIXED",
  "createdBy": "developer123",
  "createdAt": "2024-01-15T10:30:00",
  "completedAt": "2024-01-17T16:45:00"
}
```

**Ошибки:**
- `400 Bad Request` - если задача уже завершена или отменена
- `404 Not Found` - если задача не найдена или не принадлежит пользователю
- `401 Unauthorized` - если токен отсутствует или невалиден

**Примечание:** После завершения задачи пользователю автоматически начисляются баллы (поле `points` задачи добавляется к текущим баллам пользователя).

---

### Отменить задачу

Отменяет задачу, которая находится в работе. Отмененные задачи не начисляют баллы.

**Запрос:**
```
POST /api/tasks/{taskId}/cancel
Authorization: Bearer <token>
```

**Параметры пути:**
- `taskId` (Long) - ID задачи

**Ответ (200 OK):**
```json
{
  "id": 1,
  "title": "Изучить Spring Boot",
  "description": "Пройдите базовый курс по Spring Boot",
  "points": 50,
  "status": "CANCELLED",
  "type": "FIXED",
  "createdBy": "developer123",
  "createdAt": "2024-01-15T10:30:00",
  "completedAt": null
}
```

**Ошибки:**
- `400 Bad Request` - если задача уже завершена или отменена
- `404 Not Found` - если задача не найдена или не принадлежит пользователю
- `401 Unauthorized` - если токен отсутствует или невалиден

---

### Получить список шаблонов задач

Возвращает список всех активных фиксированных шаблонов задач, которые пользователь может взять в работу.

**Запрос:**
```
GET /api/tasks/templates
Authorization: Bearer <token>
```

**Ответ (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Изучить Spring Boot",
    "description": "Пройдите базовый курс по Spring Boot и создайте простое приложение",
    "points": 50
  },
  {
    "id": 2,
    "title": "Изучить Docker",
    "description": "Освойте основы контейнеризации с Docker",
    "points": 75
  },
  {
    "id": 3,
    "title": "Создать REST API",
    "description": "Реализуйте полноценный REST API с CRUD операциями",
    "points": 100
  }
]
```

**Ошибки:**
- `401 Unauthorized` - если токен отсутствует или невалиден

**Примечание:** Возвращаются только активные шаблоны (`active = true`).

---

### Взять шаблон задачи в работу

Создает новую задачу на основе шаблона и сразу добавляет её в список текущих задач пользователя.

**Запрос:**
```
POST /api/tasks/templates/{templateId}/take
Authorization: Bearer <token>
```

**Параметры пути:**
- `templateId` (Long) - ID шаблона задачи

**Ответ (200 OK):**
```json
{
  "id": 5,
  "title": "Изучить Spring Boot",
  "description": "Пройдите базовый курс по Spring Boot и создайте простое приложение",
  "points": 50,
  "status": "IN_PROGRESS",
  "type": "FIXED",
  "createdBy": "developer123",
  "createdAt": "2024-01-18T09:15:00",
  "completedAt": null
}
```

**Ошибки:**
- `400 Bad Request` - если шаблон не найден или отключен
- `401 Unauthorized` - если токен отсутствует или невалиден

**Примечание:** 
- Задача создается со статусом `IN_PROGRESS`
- Тип задачи устанавливается как `FIXED`
- Данные (title, description, points) копируются из шаблона

---

### Создать пользовательскую задачу

Создает новую задачу, созданную самим пользователем, и сразу добавляет её в работу.

**Запрос:**
```
POST /api/tasks/custom
Content-Type: application/json
Authorization: Bearer <token>
```

**Тело запроса:**
```json
{
  "title": "Изучить GraphQL",
  "description": "Освоить основы GraphQL и создать простой API",
  "points": 80
}
```

**Валидация:**
- `title` - обязательное поле, максимум 255 символов
- `description` - необязательное поле, максимум 1000 символов
- `points` - обязательное поле, минимум 1

**Ответ (200 OK):**
```json
{
  "id": 6,
  "title": "Изучить GraphQL",
  "description": "Освоить основы GraphQL и создать простой API",
  "points": 80,
  "status": "IN_PROGRESS",
  "type": "CUSTOM",
  "createdBy": "developer123",
  "createdAt": "2024-01-18T10:30:00",
  "completedAt": null
}
```

**Ошибки:**
- `400 Bad Request` - если данные не прошли валидацию
- `401 Unauthorized` - если токен отсутствует или невалиден

**Примечание:**
- Задача создается со статусом `IN_PROGRESS`
- Тип задачи устанавливается как `CUSTOM`
- Создатель задачи (`createdBy`) - текущий пользователь

---

### Лидерборд (список пользователей по баллам)

Возвращает пользователей, отсортированных по убыванию их баллов.

**Запрос:**
```
GET /api/users/leaderboard
Authorization: Bearer <token>
```

**Ответ (200 OK):**
```json
[
  { "id": 3, "username": "alice", "points": 150 },
  { "id": 1, "username": "bob",   "points": 120 },
  { "id": 2, "username": "carol", "points": 90  }
]
```

**Ошибки:**
- `401 Unauthorized` - если токен отсутствует или невалиден

---

## API Личного кабинета

Все эндпоинты личного кабинета требуют аутентификации. Добавьте заголовок:
```
Authorization: Bearer <your-jwt-token>
```

---

### Получить данные личного кабинета

Возвращает данные текущего пользователя: фото, логин, email и количество баллов.

**Запрос:**
```
GET /api/users/profile
Authorization: Bearer <token>
```

**Ответ (200 OK):**
```json
{
  "id": 1,
  "username": "developer123",
  "email": "developer@example.com",
  "points": 150,
  "photo": null
}
```

**Примечание:** 
- Поле `photo` содержит массив байтов изображения в формате Base64 (если фото загружено)
- Если фото не загружено, поле `photo` будет `null`

**Ошибки:**
- `401 Unauthorized` - если токен отсутствует или невалиден

---

### Загрузить/обновить фото профиля

Загружает или обновляет фото профиля пользователя. Фото хранится в базе данных.

**Запрос:**
```
POST /api/users/profile/photo
Content-Type: multipart/form-data
Authorization: Bearer <token>
```

**Параметры формы:**
- `photo` (File) - файл изображения (обязательное поле)

**Ответ (200 OK):**
```json
{
  "id": 1,
  "username": "developer123",
  "email": "developer@example.com",
  "points": 150,
  "photo": [/* массив байтов изображения */]
}
```

**Ошибки:**
- `400 Bad Request` - если файл не передан или произошла ошибка при чтении файла
- `401 Unauthorized` - если токен отсутствует или невалиден

**Примечание:**
- Рекомендуется использовать форматы изображений: JPEG, PNG
- Максимальный размер файла ограничен настройками Spring Boot (по умолчанию 10MB)

---

## Модели данных

### TaskStatus (Статус задачи)

```java
IN_PROGRESS  // Задача в работе
DONE         // Задача выполнена
CANCELLED    // Задача отменена
```

### TaskType (Тип задачи)

```java
FIXED   // Фиксированная задача (из шаблона)
CUSTOM  // Пользовательская задача
```

### TaskDto (DTO задачи)

```json
{
  "id": Long,
  "title": String,
  "description": String,
  "points": Integer,
  "status": "IN_PROGRESS" | "DONE" | "CANCELLED",
  "type": "FIXED" | "CUSTOM",
  "createdBy": String (username),
  "createdAt": LocalDateTime,
  "completedAt": LocalDateTime | null
}
```

### TaskTemplateDto (DTO шаблона задачи)

```json
{
  "id": Long,
  "title": String,
  "description": String,
  "points": Integer
}
```

### CreateTaskRequest (Запрос на создание задачи)

```json
{
  "title": String (обязательное, max 255),
  "description": String (необязательное, max 1000),
  "points": Integer (обязательное, min 1)
}
```

### UserProfileDto (DTO профиля пользователя)

```json
{
  "id": Long,
  "username": String,
  "email": String,
  "points": Integer,
  "photo": byte[] | null
}
```

### UserLeaderboardDto (DTO пользователя в лидерборде)

```json
{
  "id": Long,
  "username": String,
  "points": Integer
}
```

---

## Конфигурация

Настройки находятся в `src/main/resources/application.yml`:

### База данных
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mobile_auth
    username: postgres
    password: postgres
```

### JWT
```yaml
jwt:
  secret: ${JWT_SECRET:mySecretKeyForJWTTokenGenerationAndValidationShouldBeAtLeast256Bits}
  expiration: 86400000  # 24 часа в миллисекундах
```

### CORS
По умолчанию разрешены запросы с `http://localhost:8081` (для React Native приложения).

---

## Использование в мобильном приложении

### 1. Аутентификация

После успешной регистрации или входа сохраните токен:

```javascript
// Пример для React Native
const response = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username, password })
});
const { token } = await response.json();
await AsyncStorage.setItem('token', token);
```

### 2. Использование токена

Все запросы к API задач требуют токен в заголовке:

```javascript
const token = await AsyncStorage.getItem('token');
const response = await fetch('http://localhost:8080/api/tasks/current', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});
```

### 3. Загрузка фото профиля

Пример загрузки фото с использованием FormData:

```javascript
const token = await AsyncStorage.getItem('token');
const formData = new FormData();
formData.append('photo', {
  uri: photoUri,
  type: 'image/jpeg',
  name: 'photo.jpg'
});

const response = await fetch('http://localhost:8080/api/users/profile/photo', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`
  },
  body: formData
});
const profile = await response.json();
```

### 4. Типичный сценарий работы

1. **Пользователь входит в систему** → получает JWT токен
2. **Просматривает свой профиль** → `GET /api/users/profile`
3. **Загружает фото профиля** → `POST /api/users/profile/photo` (опционально)
4. **Загружает текущие задачи** → `GET /api/tasks/current`
5. **Просматривает доступные шаблоны** → `GET /api/tasks/templates`
6. **Берет шаблон в работу** → `POST /api/tasks/templates/{id}/take`
7. **Или создает свою задачу** → `POST /api/tasks/custom`
8. **Выполняет задачу** → `POST /api/tasks/{id}/complete` (начисляются баллы)
9. **Или отменяет задачу** → `POST /api/tasks/{id}/cancel`
10. **Просматривает лидерборд** → `GET /api/users/leaderboard`

---

## Структура проекта

```
src/main/java/com/example/mobile/
├── config/
│   ├── JwtUtil.java                    # Утилиты для работы с JWT
│   ├── JwtAuthenticationFilter.java    # Фильтр аутентификации
│   └── SecurityConfig.java             # Конфигурация безопасности
├── controller/
│   ├── AuthController.java             # Контроллер аутентификации
│   ├── TaskController.java             # Контроллер задач
│   └── UserController.java             # Контроллер пользователей (лидерборд, профиль)
├── dto/
│   ├── AuthResponse.java               # Ответ аутентификации
│   ├── LoginRequest.java               # Запрос входа
│   ├── RegisterRequest.java            # Запрос регистрации
│   ├── TaskDto.java                    # DTO задачи
│   ├── TaskTemplateDto.java            # DTO шаблона задачи
│   ├── UserLeaderboardDto.java         # DTO записи лидерборда
│   ├── UserProfileDto.java             # DTO профиля пользователя
│   └── CreateTaskRequest.java          # Запрос создания задачи
├── entity/
│   ├── User.java                       # Сущность пользователя
│   ├── UserTask.java                   # Сущность задачи пользователя
│   ├── TaskTemplate.java               # Сущность шаблона задачи
│   ├── TaskStatus.java                 # Enum статусов задачи
│   └── TaskType.java                   # Enum типов задачи
├── repository/
│   ├── UserRepository.java             # Репозиторий пользователей
│   ├── UserTaskRepository.java         # Репозиторий задач
│   └── TaskTemplateRepository.java     # Репозиторий шаблонов
└── service/
    ├── UserService.java                # Сервис пользователей
    ├── TaskService.java                # Сервис задач
    └── CustomUserDetailsService.java   # Сервис для Spring Security
```

---

## Особенности реализации

### Система баллов

- Пользователь начинает с 0 баллов
- Баллы начисляются только при завершении задачи (`status = DONE`)
- Отмененные задачи не начисляют баллы
- Баллы хранятся в поле `points` сущности `User`

### Типы задач

- **FIXED** - задачи созданные из шаблонов, имеют фиксированные параметры
- **CUSTOM** - задачи созданные пользователем, могут иметь любые параметры

### Статусы задач

- **IN_PROGRESS** - задача взята в работу, но еще не выполнена
- **DONE** - задача выполнена, баллы начислены
- **CANCELLED** - задача отменена пользователем

### Хранение фото пользователя

- Фото хранится в базе данных PostgreSQL в формате BYTEA (массив байтов)
- Поле `photo` в сущности `User` может быть `null`, если фото не загружено
- Рекомендуется использовать форматы JPEG или PNG
- Максимальный размер файла ограничен настройками Spring Boot (по умолчанию 10MB)

---

## Разработка

### Сборка проекта

```bash
./gradlew build
```

### Запуск тестов

```bash
./gradlew test
```

### Запуск в режиме разработки

```bash
./gradlew bootRun
```

---

## Лицензия

Этот проект создан для образовательных целей.
