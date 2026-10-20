# Social Network

A console application written in Java for managing a small social network:
users, friendships and communities. Built for the Advanced Programming
Methods (MAP) course at Babeș-Bolyai University.

Data is persisted in plain text files, loaded at startup and saved after
every change.

## Features

- Add a user (the id is assigned automatically: the smallest free id, i.e. the *mex*)
- Remove a user (all of their friendships are removed too)
- Add / remove a friendship between two existing users
- Show all users and all friendships
- Compute the number of communities
- Find the most sociable community
- Numbered console menu, reprinted after every action

## Menu

```
========== SOCIAL NETWORK ==========
 1. Add user
 2. Remove user
 3. Add friendship
 4. Remove friendship
 5. Show all users
 6. Show all friendships
 7. Show number of communities
 8. Show most sociable community
 0. Exit
====================================
```

## Project structure

```
Social network/
├── data/
│   ├── users.txt
│   └── friendships.txt
└── src/
    ├── Main.java
    ├── domain/
    │   ├── Entity.java
    │   ├── Tuple.java
    │   ├── User.java
    │   ├── Friendship.java
    │   └── validators/
    │       ├── Validator.java
    │       ├── UserValidator.java
    │       └── FriendshipValidator.java
    ├── repository/
    │   ├── Repository.java
    │   ├── InMemoryRepository.java
    │   ├── AbstractFileRepository.java
    │   ├── UserFileRepository.java
    │   └── FriendshipFileRepository.java
    ├── service/
    │   └── NetworkService.java
    ├── ui/
    │   └── ConsoleUI.java
    └── exceptions/
        ├── ValidationException.java
        ├── RepositoryException.java
        └── ServiceException.java
```

## Architecture

The application is split into layers, each depending only on the one below it:

| Layer | Responsibility |
|-------|----------------|
| `ui` | Reads user input, prints results, catches and displays errors |
| `service` | Business logic: user and friendship management, community computation |
| `repository` | Storage: CRUD contract, in-memory implementation, file persistence |
| `domain` | Entities (`User`, `Friendship`) and their validators |
| `exceptions` | Own exception types, one per layer |

### Domain

- `Entity<ID>` is the base class of all entities. Equality is based on the
  runtime class and the id.
- `User` has an id (`Long`), a first name and a last name.
- `Friendship` has a composite id, `Tuple<Long, Long>`, normalized as
  `(min, max)`, so the friendship between 3 and 7 is the same as between 7
  and 3. It also stores the date it was created (`LocalDateTime`).

### Repository

- `Repository<ID, E>` defines the CRUD contract: `findOne`, `findAll`, `save`, `delete`.
- `InMemoryRepository` stores entities in a `LinkedHashMap` (insertion order
  is preserved) and validates them before saving.
- `AbstractFileRepository` extends `InMemoryRepository` and adds persistence.
  It loads the file in the constructor and rewrites it after every `save` and
  `delete`. If writing fails, the in-memory change is undone so memory and
  file stay consistent.
- `UserFileRepository` and `FriendshipFileRepository` only define how one
  line of the file is converted to an entity and back.

### Service

`NetworkService` implements the application logic and uses the repositories
through the `Repository` interface. Community computation builds an adjacency
list from the users and friendships on demand.

### Exceptions

| Exception | Thrown when |
|-----------|-------------|
| `ValidationException` | An entity has invalid data (empty name, negative id, self-friendship, ...) |
| `RepositoryException` | A file cannot be read or written, a line is malformed, an entity does not exist |
| `ServiceException` | A friendship refers to a user that does not exist |

All of them are caught in `ConsoleUI`, which prints a readable message
(`Action failed: ...`) instead of crashing.

## Design patterns and principles

- **Template Method**: `AbstractFileRepository` implements loading and saving;
  subclasses only provide `extractEntity(String)` and `toLine(E)`.
- **Strategy**: `Validator<T>` is injected into the repositories, so
  validation rules can change without touching storage code.
- **Single Responsibility**: each layer and class has one job (UI, logic,
  storage, validation).
- **Dependency Inversion**: `NetworkService` depends on the `Repository`
  interface, not on the file-based classes.
- **Open/Closed**: a new entity type needs a new subclass of
  `AbstractFileRepository`, with no change to the existing ones.

## Algorithms

- **Number of communities**: a community is a connected component of the
  friendship graph. An iterative DFS (with an explicit stack) is started from
  every unvisited user; each start is one new community. Complexity: O(V + E).
- **Most sociable community**: the connected component with the most
  members, found among the components computed above. On a tie, the first one
  found is kept.
- **New user id**: the smallest positive integer not used by any existing
  user (mex), so ids freed by removed users are reused.

## File format

Fields are separated by `;`, one entity per line. The `data/` folder is
created automatically if it does not exist.

`data/users.txt`
```
<id>;<firstName>;<lastName>
```

`data/friendships.txt`
```
<userId1>;<userId2>;<date>
```
where `<date>` is an ISO-8601 `LocalDateTime`, for example `2026-10-14T18:30:12.345`.

If a line is malformed or fails validation, it is skipped and a warning with
the file name and line number is printed to the error stream. A missing file
is not an error: the repository starts empty and the file is created at the
first save.

## Running

The program expects to be run from the project root (the folder containing
`data/`), because file paths are relative.

```bash
javac -d out/production src/Main.java src/*/*.java src/domain/validators/*.java
java -cp out/production Main
```

Or open the project in IntelliJ IDEA and run `Main`.

## Author

Raul Ardelean, group 221/1, Faculty of Mathematics and Computer Science, UBB.