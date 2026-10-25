# Social Network

A console application written in Java for managing a small social network: users, friendships, and communities. Built for the Advanced Programming Methods (MAP) course at Babeș-Bolyai University.

Data is persisted in plain text files (or only in memory, depending on `repository.type`), loaded at startup and saved after every change.

## Features

- Add a user with an automatically assigned ID (the smallest available positive ID, or mex).
- Remove a user together with all their friendships.
- Add or remove a friendship between two users.
- Display all users and friendships.
- Compute the number of communities.
- Display all communities.
- Find the most sociable community.
- Persist data in text files.
- Handle validation, repository, and service errors.
- Use a numbered console menu that is reprinted after every action.

## Menu

```text
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

```text
Social network/
├── readme.txt
├── data/
│   ├── users.txt
│   ├── friendships.txt
│   └── test_corrupt/
│       ├── users.txt
│       └── friendships.txt
└── src/
    ├── main/resources/config.properties
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
    │   ├── FriendshipFileRepository.java
    │   ├── UserDbRepository.java
    │   ├── FriendshipDbRepository.java
    │   ├── UsersExistValidator.java
    │   ├── RepositoryType.java
    │   ├── RepositoryConfig.java
    │   └── RepositoryFactory.java
    ├── structures/
    │   ├── Connectivity.java
    │   ├── DynamicConnectivity.java
    │   ├── ConnectivityLevel.java
    │   ├── EulerTourForest.java
    │   ├── Treap.java
    │   ├── TourNode.java
    │   └── Edge.java
    ├── service/
    │   ├── NetworkService.java
    │   ├── CommunityService.java
    │   ├── ConnectivityLoader.java
    │   ├── UserIdGenerator.java
    │   ├── SociableCommunityFinder.java
    │   └── LargestCommunityFinder.java
    ├── ui/
    │   └── ConsoleUI.java
    └── exceptions/
        ├── ValidationException.java
        ├── RepositoryException.java
        └── ServiceException.java
```

## Architecture

The application is organized into layers, each with a specific responsibility.

| Layer | Responsibility |
|---|---|
| `ui` | Reads user input, displays results, and handles errors. |
| `service` | Implements business logic: users and friendships, community queries, id generation. |
| `repository` | Defines CRUD operations and manages in-memory and file storage. |
| `domain` | Contains entities and validators. |
| `structures` | Contains generic data structures, such as the fully dynamic connectivity structure. |
| `exceptions` | Defines custom exceptions for different types of errors. |

### Domain

- `Entity<ID>` is the base class for entities. Equality is based on the runtime class and ID.
- `User` represents a user with an ID (`Long`), first name, and last name.
- `Friendship` represents a friendship between two users. Its composite ID is a `Tuple<Long, Long>`, normalized as `(min, max)`, so the friendship between users 3 and 7 is equivalent to the friendship between users 7 and 3. It also stores the creation date as a `LocalDateTime`.
- `Tuple<A, B>` represents a pair of values.

### Repository

- `Repository<ID, E>` defines the CRUD contract: `findOne`, `findAll`, `save`, and `delete`.
- `InMemoryRepository` stores entities in a `LinkedHashMap`, preserving insertion order, and validates entities before saving.
- `AbstractFileRepository` extends `InMemoryRepository` and adds file persistence. It loads data when initialized and rewrites the file after every successful save or delete. If writing fails, the in-memory change is undone.
- `UserFileRepository` and `FriendshipFileRepository` define how entities are converted to and from text lines (`extractEntity` and `createEntityAsString`).
- `UserDbRepository` and `FriendshipDbRepository` are skeletons for the database laboratory: they take the validator and the connection parameters, and every method throws `UnsupportedOperationException`.
- `UsersExistValidator` decorates the friendship validator so that a friendship is accepted only between existing users, also for the lines loaded from the file.
- `RepositoryFactory` is a Simple Factory and the only class that knows the concrete repositories. It creates them for the type configured in `src/main/resources/config.properties` (`MEMORY`, `FILE` or `DATABASE`, see `RepositoryType`), which `RepositoryConfig` reads with `java.util.Properties`. A missing or unknown value throws a `RepositoryException` with a clear message. `Main`, the UI and the services depend only on `Repository`.

The details of the factory, the handling of missing files and corrupt lines, and the design principles and patterns are described in `readme.txt`.

### Service

Each class in `service` has one responsibility, and all of them depend on abstractions (`Repository`, `Connectivity`, `SociableCommunityFinder`) injected from `Main`.

| Class | Responsibility |
|---|---|
| `NetworkService` | The only service the UI talks to. Adds and removes users and friendships, keeps the `Connectivity` in sync, and delegates community queries to `CommunityService`. |
| `CommunityService` | Read-only community queries: list, count, most sociable. Used through `NetworkService`. |
| `ConnectivityLoader` | Fills a `Connectivity` from the repositories at startup. |
| `UserIdGenerator` | Chooses the id of a new user (mex). |
| `SociableCommunityFinder` | Strategy for choosing the most sociable community. |
| `LargestCommunityFinder` | Implementation that picks the community with the most users. |

`NetworkService` changes the repository first and the connectivity afterwards, so if saving or deleting fails, the connectivity is left untouched and no rollback is needed. Removals are applied directly, without any rebuild.

### Structures

`Connectivity` is the contract for tracking the connected components of a dynamic graph: `addVertex`, `removeVertex`, `addEdge`, `removeEdge`, `connected`, `componentCount`, `components`, `neighbours`.

`DynamicConnectivity` implements it with the fully dynamic connectivity algorithm of Holm, de Lichtenberg and Thorup (2001). It is generic, with no dependency on domain entities, and it is split into small package-private helpers, each with one responsibility:

| Class | Responsibility |
|---|---|
| `DynamicConnectivity` | Public API and the level logic of the algorithm (insertion, deletion, replacement edge search). |
| `ConnectivityLevel` | One level: its spanning forest and its non-tree edges, kept in sync. |
| `EulerTourForest` | The spanning forest of one level as Euler tour trees: `link`, `cut`, `connected`, marks. |
| `Treap` | Treap operations (`merge`, `split`, `indexOf`, ...) on sequences of `TourNode`. Knows nothing about graphs. |
| `TourNode` | Treap node with the aggregates needed by the searches. |
| `Edge` | An edge with its level, its tree flag and its arcs in the forests. |

| Operation | Complexity |
|---|---|
| `addEdge`, `removeEdge` | O(log² n) amortized |
| `connected` | O(log n) |
| `componentCount` | O(1) |

### Exceptions

| Exception | Purpose |
|---|---|
| `ValidationException` | Indicates invalid entity data, such as empty names, invalid IDs, or self-friendships. |
| `RepositoryException` | Indicates storage errors, malformed file entries, or missing entities. |
| `ServiceException` | Indicates service-level errors, such as operations involving nonexistent users. |

The console UI catches and displays errors as readable messages instead of allowing expected application errors to terminate the program.

## Design patterns and principles

- **Template Method:** `AbstractFileRepository` implements common file loading and saving logic. Subclasses provide `extractEntity(String)` and `toLine(E)`.
- **Strategy:** `Validator<T>` is injected into repositories, allowing validation rules to vary independently of storage.
- **Single Responsibility Principle:** Each layer has a distinct role, and inside `service` each class has one reason to change (data changes, community queries, loading, id generation, the sociable-community algorithm).
- **Dependency Inversion Principle:** services depend on the `Repository`, `Connectivity` and `SociableCommunityFinder` interfaces; concrete classes are created only in `Main`.
- **Open/Closed Principle:** New entity types can be supported by subclassing `AbstractFileRepository`; a new connectivity algorithm or a new definition of "most sociable" can be added by implementing `Connectivity` or `SociableCommunityFinder`, without changing the services.
- **Strategy:** `SociableCommunityFinder` is injected into `CommunityService`.

## Algorithms

### Number of communities

The number of communities is the number of connected components, maintained by `DynamicConnectivity`. Adding a friendship links two components when its endpoints are not yet connected; removing a friendship looks for a replacement edge and splits the component only if none exists. Users can be added and removed in any order, so no rebuild is ever needed.

### Most sociable community

The most sociable community is the connected component with the most users. `LargestCommunityFinder` scans the list of components once, in O(C) where C is the number of communities. On a tie, the community with the smallest user id wins.

### New user ID

The service assigns the smallest positive integer not used by an existing user (mex). IDs freed by removing users can therefore be reused.

## File format

Fields are separated by semicolons (`;`), with one entity per line. The `data/` directory is expected to exist or be created automatically by the application.

### `data/users.txt`

```text
<id>;<firstName>;<lastName>
```

Example:

```text
1;John;Smith
2;Alice;Brown
3;Michael;Jones
```

### `data/friendships.txt`

```text
<userId1>;<userId2>;<date>
```

The date is an ISO-8601 `LocalDateTime`, for example:

```text
1;2;2026-10-14T18:30:12.345
```

If a line is malformed or fails validation (including a friendship with a missing user), it is skipped and a warning containing the file name, line number and reason is printed to the error stream. `data/test_corrupt/` contains example files with corrupt lines.

A missing file is not an error: the repository starts empty, and the file is created on the first save.

## Running the application

Run the following commands from the project root, which contains the `data/` directory:

```bash
javac -d out/production src/Main.java src/*/*.java src/domain/validators/*.java
java -cp out/production Main
```

Alternatively, open the project in IntelliJ IDEA and run `Main`.

## Documentation

Javadoc can be generated into the `javadoc/` directory. Open `javadoc/index.html` to browse the generated documentation.

## Author

Raul Ardelean, group 221/1, Faculty of Mathematics and Computer Science, Babeș-Bolyai University.