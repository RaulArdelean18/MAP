# Social Network

A console application written in Java for managing a small social network: users, friendships, and communities. Built for the Advanced Programming Methods (MAP) course at Babeș-Bolyai University.

Data is persisted in plain text files, loaded at startup and saved after every change.

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
    ├── structures/
    │   └── RollbackDisjointSets.java
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

The application is organized into layers, each with a specific responsibility.

| Layer | Responsibility |
|---|---|
| `ui` | Reads user input, displays results, and handles errors. |
| `service` | Implements business logic and community operations. |
| `repository` | Defines CRUD operations and manages in-memory and file storage. |
| `domain` | Contains entities and validators. |
| `structures` | Contains generic data structures, such as the rollback-capable DSU. |
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
- `UserFileRepository` and `FriendshipFileRepository` define how entities are converted to and from text lines.

### Service

`NetworkService` implements the application's business logic and accesses storage through the `Repository` interface.

It manages users and friendships, maintains the DSU structure, computes communities, and identifies the most sociable community.

The DSU is initialized from the repositories and rebuilt when necessary after operations that remove users or friendships.

When adding a friendship, the service creates a checkpoint before merging the corresponding sets. If saving the friendship fails, the DSU rolls back the changes made after that checkpoint.

### Structures

`RollbackDisjointSets` implements a disjoint-set union data structure with rollback support. It is a generic data structure with no dependency on the domain entities, which is why it lives in its own `structures` package.

#### RollbackDisjointSets

`RollbackDisjointSets` maintains the connected components of the social network using a Disjoint-Set Union (DSU) data structure.

Its main operations are:

- `makeSet(x)`: creates a singleton set for a user.
- `find(x)`: returns the representative of the set containing the user.
- `union(a, b)`: merges two sets if they are different.
- `checkpoint()`: returns a marker representing the current history size.
- `rollbackTo(checkpoint)`: undoes successful unions performed after the checkpoint.
- `componentCount()`: returns the current number of disjoint sets.
- `clear()`: removes all elements and history.

The implementation uses union by size to keep trees relatively shallow. Path compression is deliberately omitted because it would complicate rollback.

Successful union operations are recorded in a history stack, allowing changes to be undone in reverse order.

The rollback mechanism applies to union operations only. It does not undo `makeSet` operations.

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
- **Single Responsibility Principle:** Each layer has a distinct role, separating user interaction, business logic, storage, and validation.
- **Dependency Inversion Principle:** `NetworkService` depends on the `Repository` interface rather than concrete file-based implementations.
- **Open/Closed Principle:** New entity types can be supported by creating appropriate subclasses of `AbstractFileRepository`.
- **Rollback / Undo mechanism:** `RollbackDisjointSets` records successful union operations and supports restoring a previous DSU state using checkpoints. This allows the service to undo DSU changes when saving a friendship fails.

## Algorithms

### Number of communities

The application uses a Disjoint-Set Union (DSU) data structure to maintain connected components.

Initially, each user belongs to a separate set. When a friendship is added, the sets containing the two users are merged. The number of communities is maintained as the number of disjoint sets.

With union by size and without path compression, the height of every tree is at most logarithmic in the number of users, so each `find` or `union` operation takes O(log V) time in the worst case, where V is the number of users.

### Rollback

Each successful union records the root that was attached to another root. A checkpoint stores the current history size.

If a subsequent operation fails, `rollbackTo(checkpoint)` reverses all unions performed after that checkpoint and restores the component count.

This avoids rebuilding the DSU when a friendship cannot be saved.

### DSU rebuilding

Removing users or friendships can split a connected component into several components. A standard DSU does not directly support these deletions.

The service therefore marks the DSU as dirty after a removal and rebuilds it from the current repositories when a community-related operation needs an up-to-date structure.

### Most sociable community

The most sociable community is defined as the connected component containing the longest elementary path.

The algorithm builds an adjacency list and uses backtracking to explore paths without revisiting vertices on the current path. It examines possible starting vertices in each community and selects the community with the longest path.

This algorithm can be computationally expensive for large networks because finding a longest elementary path is computationally hard in general.

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

If a line is malformed or fails validation, it is skipped and a warning containing the file name and line number is printed to the error stream.

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