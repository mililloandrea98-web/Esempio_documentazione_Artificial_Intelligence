# Esempio_documentazione_Artificial_Intelligence

## Obiettivo del progetto

Repository di esempio adibito alla creazione di un sistema CRUD (Create, Read, Update, Delete) per la gestione di utenti, esposto tramite API REST. Il progetto funge da caso di studio per la generazione di documentazione tecnica su un'applicazione backend Java basata su Spring Boot.

## Descrizione funzionale

Il sottoprogetto `user-crud` implementa un'API REST per la gestione dell'anagrafica utenti, con le seguenti funzionalità:

- **Creazione** di un nuovo utente (nome ed email, con validazione dei campi)
- **Lettura** dell'elenco completo degli utenti o di un singolo utente tramite ID
- **Aggiornamento** dei dati di un utente esistente
- **Eliminazione** di un utente

Vincoli funzionali:
- Il campo `name` è obbligatorio (non può essere vuoto)
- Il campo `email` è obbligatorio, deve avere un formato email valido ed è univoco per ciascun utente
- Le richieste verso un utente inesistente restituiscono un errore `404 Not Found` con messaggio descrittivo
- Le richieste con dati non validi restituiscono un errore `400 Bad Request` con il dettaglio dei campi non conformi

### Endpoint API

| Metodo | Endpoint             | Descrizione                          |
|--------|-----------------------|---------------------------------------|
| GET    | `/api/users`          | Restituisce l'elenco di tutti gli utenti |
| GET    | `/api/users/{id}`     | Restituisce un utente specifico       |
| POST   | `/api/users`          | Crea un nuovo utente                  |
| PUT    | `/api/users/{id}`     | Aggiorna un utente esistente          |
| DELETE | `/api/users/{id}`     | Elimina un utente                     |

## Architettura del progetto

Il progetto adotta un'architettura a livelli (layered architecture), tipica delle applicazioni Spring Boot:

```
user-crud/
├── pom.xml
└── src/main/
    ├── java/com/example/usercrud/
    │   ├── UserCrudApplication.java      # Entry point dell'applicazione Spring Boot
    │   ├── controller/
    │   │   └── UserController.java       # Espone gli endpoint REST (livello presentazione)
    │   ├── service/
    │   │   └── UserService.java          # Logica applicativa e regole di business
    │   ├── repository/
    │   │   └── UserRepository.java       # Accesso ai dati tramite Spring Data JPA
    │   ├── model/
    │   │   └── User.java                 # Entità JPA che rappresenta l'utente
    │   └── exception/
    │       ├── UserNotFoundException.java    # Eccezione custom per utente non trovato
    │       └── GlobalExceptionHandler.java   # Gestione centralizzata degli errori (@RestControllerAdvice)
    └── resources/
        └── application.properties        # Configurazione dell'applicazione e del database
```

Flusso delle richieste:

```
Client HTTP → UserController → UserService → UserRepository → Database (H2)
```

- **Controller**: riceve le richieste HTTP, valida il payload (`@Valid`) e delega la logica al service.
- **Service**: contiene la logica applicativa (creazione, aggiornamento, cancellazione, gestione dell'eccezione "utente non trovato").
- **Repository**: interfaccia `JpaRepository` che fornisce le operazioni CRUD di base sul database tramite Spring Data JPA.
- **Model**: entità `User` mappata sulla tabella `users`, con vincoli di validazione (`@NotBlank`, `@Email`).
- **Exception handling**: gli errori vengono intercettati centralmente e restituiti al client in formato JSON con timestamp, stato HTTP e dettaglio.

### Stack tecnologico

- **Linguaggio**: Java 8
- **Framework**: Spring Boot 2.7.18 (Spring Web, Spring Data JPA, Spring Validation)
- **Database**: H2 (in-memory), accessibile anche tramite console web (`/h2-console`)
- **Build tool**: Apache Maven
- **Porta di default**: 8080

## Come avviare il progetto

```bash
cd user-crud
mvn clean package
java -jar target/user-crud.jar
```

L'applicazione sarà disponibile su `http://localhost:8080`, con gli endpoint REST sotto `/api/users` e la console H2 su `/h2-console`.
