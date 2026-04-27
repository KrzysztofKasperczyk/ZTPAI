# ZTPAI Krzysztof Kasperczyk - Projekt Spring Boot (Ocena 4.0)

Aplikacja REST API zbudowana w Spring Boot z obsługą JWT, połączeniem z bazą PostgreSQL (Docker) oraz pełnym CRUD dla encji `Book`.

---

## Technologie

- **Java 21**
- **Spring Boot 4.0.6**
- **Spring Security + JWT**
- **Spring Data JPA + Hibernate**
- **PostgreSQL 16 (Docker)**
- **Lombok**
- **Maven**

---

## Struktura projektu

```
src/main/java/com/example/demo/
├── config/
│   └── SecurityConfig.java          # Konfiguracja Spring Security + JWT filter
├── controller/
│   ├── AuthController.java          # Endpointy rejestracji i logowania
│   └── BookController.java          # Endpointy CRUD dla książek
├── dto/
│   ├── AuthResponseDto.java         # Odpowiedź z tokenem JWT
│   ├── BookRequestDto.java          # Dane wejściowe książki (z walidacją)
│   ├── BookResponseDto.java         # Dane wyjściowe książki
│   ├── LoginRequestDto.java         # Dane logowania
│   └── RegisterRequestDto.java      # Dane rejestracji
├── entity/
│   ├── Book.java                    # Encja JPA — tabela books
│   └── User.java                    # Encja JPA — tabela users
├── exception/
│   ├── GlobalExceptionHandler.java  # Obsługa błędów
│   └── ResourceNotFoundException.java
├── repository/
│   ├── BookRepository.java
│   └── UserRepository.java
├── security/
│   ├── JwtAuthenticationFilter.java # Filtr sprawdzający token w każdym requeście
│   ├── JwtUtil.java                 # Generowanie i walidacja tokenów JWT
│   └── UserDetailsServiceImpl.java  # Ładowanie użytkownika z bazy
└── service/
    ├── BookService.java             # Logika biznesowa książek
    └── UserService.java             # Logika rejestracji i logowania
```

---

## Wymagania

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Java 21 JDK](https://adoptium.net/)
- [Maven](https://maven.apache.org/)
- [Postman](https://www.postman.com/) (do testowania)

---

## Uruchomienie

### Krok 1 - Uruchom bazę danych (PostgreSQL w Dockerze)

```bash
docker compose up -d
```

Sprawdź czy baza działa:

```bash
docker compose ps
```

Powinieneś zobaczyć kontener `ztpai-postgres` ze statusem `running`.

> Baza działa na porcie `5433` (zamiast domyślnego 5432).

---

### Krok 2 - Uruchom aplikację Spring Boot

```bash
./mvnw spring-boot:run
```

lub przez IDE (IntelliJ IDEA): uruchom klasę `DemoApplication.java`.

Aplikacja startuje na: `http://localhost:8080`

---

## Konfiguracja

Plik `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/ztpai_db
spring.datasource.username=ztpai_user
spring.datasource.password=ztpai_pass
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8080

jwt.secret=5a7134743777217a25432a46294a404e635266556a586e3272357538782f413f
jwt.expiration=86400000
```

> Token JWT jest ważny przez **24 godziny** (`86400000` ms).

---

## API - Endpointy

### Autoryzacja (publiczne)

| Metoda | URL | Opis |
|--------|-----|------|
| `POST` | `/api/auth/register` | Rejestracja nowego użytkownika |
| `POST` | `/api/auth/login` | Logowanie, zwraca token JWT |

### Książki (wymagają tokena JWT)

| Metoda | URL | Opis |
|--------|-----|------|
| `GET` | `/api/books` | Pobierz wszystkie książki |
| `GET` | `/api/books/{id}` | Pobierz książkę po ID |
| `POST` | `/api/books` | Dodaj nową książkę |
| `PUT` | `/api/books/{id}` | Zaktualizuj książkę |
| `DELETE` | `/api/books/{id}` | Usuń książkę |

---

## Testowanie w Postmanie

### Krok 1 - Rejestracja

```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "krzysiek",
  "password": "haslo123"
}
```

Odpowiedź `200 OK`:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "krzysiek"
}
```

---

### Krok 2 - Logowanie

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "krzysiek",
  "password": "haslo123"
}
```

**Skopiuj token z odpowiedzi** - będzie potrzebny we wszystkich kolejnych requestach.

---

### Krok 3 - Dodawanie tokena do requestów

W Postmanie dla każdego requestu do `/api/books`:

**Zakładka Authorization → Type: Bearer Token → wklej token**

lub ręcznie w Headers:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

### Krok 4 - Operacje CRUD na książkach

**Dodaj książkę:**
```
POST http://localhost:8080/api/books
Content-Type: application/json

{
  "title": "Wiedźmin",
  "author": "Andrzej Sapkowski",
  "isbn": "9788375780635",
  "year": 1993,
  "price": 39.99
}
```
Odpowiedź: `201 Created`

**Pobierz wszystkie:**
```
GET http://localhost:8080/api/books
```
Odpowiedź: `200 OK` - lista książek

**Pobierz jedną:**
```
GET http://localhost:8080/api/books/1
```

**Zaktualizuj:**
```
PUT http://localhost:8080/api/books/1
Content-Type: application/json

{
  "title": "Wiedźmin - Ostatnie Życzenie",
  "author": "Andrzej Sapkowski",
  "isbn": "9788375780635",
  "year": 1993,
  "price": 44.99
}
```
Odpowiedź: `200 OK`

**Usuń:**
```
DELETE http://localhost:8080/api/books/1
```
Odpowiedź: `204 No Content`

---

## Obsługa błędów

Aplikacja zwraca czytelne odpowiedzi błędów:

| Scenariusz | HTTP Status |
|------------|-------------|
| Brak / niepoprawny token JWT | `403 Forbidden` |
| Zasób nie istnieje (np. `GET /books/999`) | `404 Not Found` |
| Duplikat ISBN lub nazwa użytkownika | `409 Conflict` |
| Błędy walidacji (np. pusty tytuł, zły ISBN) | `400 Bad Request` |
| Złe hasło przy logowaniu | `403 Forbidden` |

Przykładowa odpowiedź `400 Bad Request` z walidacji:
```json
{
  "timestamp": "2025-01-01T12:00:00",
  "status": 400,
  "errors": {
    "title": "Tytuł jest wymagany",
    "isbn": "ISBN musi mieć 10 lub 13 cyfr",
    "price": "Cena musi być większa niż 0"
  }
}
```

---

## Walidacja danych wejściowych (BookRequestDto)

| Pole | Reguła |
|------|--------|
| `title` | Wymagany, 1–255 znaków |
| `author` | Wymagany |
| `isbn` | Wymagany, 10 lub 13 cyfr |
| `year` | Liczba między 1000 a 2100 |
| `price` | Liczba większa od 0 |

---

## Zatrzymanie aplikacji

```bash
# Zatrzymaj bazę danych
docker compose down

# Zatrzymaj bazę i usuń dane
docker compose down -v
```
