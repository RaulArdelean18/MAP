SOCIAL NETWORK - LABORATORY 3 (file persistence, Factory, exceptions)
======================================================================

See readme.md for the features, the menu, the project structure and the algorithms.
This file answers the questions of the laboratory statement.


1. HOW THE FACTORY WORKS
------------------------
RepositoryFactory (package repository) is a Simple Factory and the only class of the
application that knows the concrete repository classes. It has one creation method per
repository:

    createUserRepository(Validator<User>)             -> Repository<Long, User>
    createFriendshipRepository(Validator<Friendship>) -> Repository<Tuple<Long,Long>, Friendship>

Both methods read the same persistence type from RepositoryConfig, so the two repositories
are always created for the same kind of storage:

    repository.type=MEMORY    -> InMemoryRepository
    repository.type=FILE      -> UserFileRepository / FriendshipFileRepository
    repository.type=DATABASE  -> UserDbRepository / FriendshipDbRepository (skeletons)

RepositoryConfig loads src/main/resources/config.properties with java.util.Properties
(in a try-with-resources). Nothing is hard-coded in the Java code except the path of the
configuration file. A missing property, a blank value or an unknown repository.type throws
a RepositoryException whose message names the property and the file; Main catches it and
prints "Cannot start the application: ...".

Main, ConsoleUI and the services depend only on the Repository interface. The friendship
repository receives a UsersExistValidator, which needs the user repository: it is created
in Main from the abstract Repository, so no concrete class leaks out of the factory.


2. WHAT MUST CHANGE FOR THE DATABASE (LABORATORY 4)
---------------------------------------------------
- Implement findOne, findAll, save and delete with JDBC in UserDbRepository and
  FriendshipDbRepository (today they throw UnsupportedOperationException). The
  constructors already receive the validator, db.url, db.user and db.password.
- Add the JDBC driver to the build and set db.url / db.user / db.password in
  config.properties.
- Set repository.type=DATABASE.

Nothing else changes: not Main, not the UI, not the services, not the factory.
RepositoryFactory already builds the DB repositories for DATABASE.
Until the DB repositories are implemented, starting with repository.type=DATABASE fails
cleanly: the first call (loading the communities) throws UnsupportedOperationException,
which Main catches and reports.


3. MISSING FILE AND CORRUPT LINES
---------------------------------
Missing file: it is not an error. AbstractFileRepository starts empty and creates the file
(and the data/ folder) at the first save.

Unreadable file (I/O error): RepositoryException wrapping the IOException (exception
chaining); the application does not start and prints the reason.

Corrupt or invalid line: the application does NOT stop. The line is reported on System.err
with the file, the line number and the reason, then ignored, for example:

    Warning: data/test_corrupt/users.txt, line 4 ignored: Expected 3 fields but found 2

The cases covered are: wrong number of fields, a non-numeric id, an invalid date, an empty
name (validator), a duplicate id, a self-friendship and a friendship with a user that does
not exist.

Why ignore instead of stopping: one typo in a hand-edited text file should not make the
whole network unavailable, and the user still sees exactly which line is wrong. The price is
that the ignored line disappears from the file the next time the file is rewritten (after
the next successful add or remove). The warning is printed before that, so the information
is not lost silently.

The files data/test_corrupt/users.txt and data/test_corrupt/friendships.txt contain one line
of each kind. To try them, put these paths in config.properties (users.file and
friendships.file).

Consistency of the two files: a friendship is saved only between existing users (loading
also goes through UsersExistValidator), and NetworkService.removeUser first deletes the
friendships of the user and then the user. After each operation both files are consistent.
If writing a file fails, AbstractFileRepository undoes the change in memory and rethrows.

Where the validation is: in the repository (InMemoryRepository.save calls the validator).
Data coming from the file is untrusted, so it must be validated too, not only the data typed
in the UI. Putting the validation in the service would let a corrupt file bypass it. The
service keeps only the business rules that need several repositories (for example "the
user exists" before adding a friendship).

Exceptions are caught where they can be handled: ConsoleUI shows a message, Main reports
a failed start. Repository and service only throw (unchecked ValidationException,
RepositoryException, ServiceException); there are no empty catch blocks and no generic
catch (Exception).


4. DESIGN PRINCIPLES (SOLID)
----------------------------
SRP  Each class has one reason to change. Repository classes store data, the validators
     validate, NetworkService manages users and friendships, CommunityService answers
     community queries, UserIdGenerator chooses ids, ConnectivityLoader fills the
     connectivity, RepositoryConfig reads the settings, RepositoryFactory creates objects.

OCP  A new entity stored in a file needs only a new subclass of AbstractFileRepository. A
     new definition of "most sociable" or a new connectivity algorithm is a new
     implementation of SociableCommunityFinder or Connectivity, without touching the
     services. The factory is only partly open: see section 5.

LSP  InMemoryRepository, AbstractFileRepository and its subclasses can be used wherever a
     Repository is expected and respect the contract in the Javadoc of Repository (for
     example delete(null) throws IllegalArgumentException, delete of a missing id throws
     RepositoryException). The DB skeletons only honour the signatures; they throw
     UnsupportedOperationException for every call, which violates the contract on purpose
     and temporarily, until laboratory 4.

DIP  Services and UI depend on the abstractions Repository, Connectivity and
     SociableCommunityFinder. All concrete classes are created in Main and in
     RepositoryFactory and passed through constructors (dependency injection).

(ISP: Repository has only the four operations that every user of it needs.)


5. SIMPLE FACTORY VS ABSTRACT FACTORY (OCP)
-------------------------------------------
Simple Factory (used here): one class with a switch on the repository type.
  + Very little code, everything about the choice is in one place, easy to read.
  + Both repositories are created in the same class, so they cannot end up with different
    persistence types by mistake.
  - Violates OCP: a new persistence type (for example JSON) means editing both methods of
    RepositoryFactory and RepositoryType.

Abstract Factory: an interface RepositoryFactory with one implementation per type
(FileRepositoryFactory, DbRepositoryFactory, ...), each creating both repositories.
  + OCP: a new type is a new class; the existing factories are not modified.
  + Each factory can have its own constructor parameters (paths for files, url/user/password
    for the database) instead of one object that knows all the settings.
  - More classes, and something still has to choose the implementation from the
    configuration (a small switch moves to Main or to a lookup).

Choice: with three fixed types the Simple Factory is enough and clearer. If more types were
expected, I would switch to the Abstract Factory. The factory is not a Singleton, because it
holds configuration and is created once in Main.


6. PATTERNS IN THE PROJECT
--------------------------
Simple Factory   RepositoryFactory.
Template Method  AbstractFileRepository: loadFromFile, saveToFile, save and delete are
                 fixed; the subclasses UserFileRepository and FriendshipFileRepository
                 implement only extractEntity (line -> entity) and createEntityAsString
                 (entity -> line).
Strategy         Validator<T> is injected into the repositories; SociableCommunityFinder
                 is injected into CommunityService (LargestCommunityFinder).
Decorator        UsersExistValidator wraps a FriendshipValidator, implements the same
                 interface and adds the check that both users exist.
Dependency       Main builds the objects and passes them through constructors.
Injection

Other notes: AbstractFileRepository extends InMemoryRepository (the file is just a persistent
copy of the in-memory map). The data files are plain UTF-8 text with ';' as separator, read
with BufferedReader and written with BufferedWriter (java.nio.file), always in
try-with-resources. The code uses no lambdas, method references, streams or functional
interfaces.
