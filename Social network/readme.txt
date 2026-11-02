SOCIAL NETWORK - LABORATORY 4 (Java 8 refactoring: lambdas, method references, streams, Optional)
===========================================================================================

See readme.md for the features, the menu and the project structure. Part L4 answers the
questions of the laboratory 4 statement; part L3 is the laboratory 3 document, updated where
the refactoring changed it. The application does the same as in laboratory 3: it was run
on the same data files (normal, data/test_corrupt, missing file, MEMORY, unknown type,
DATABASE, missing property) and the console output, the warnings on System.err and the
resulting data files are identical to the laboratory 3 version.


L4-1. BEFORE / AFTER (loops replaced)
-------------------------------------
Class.method                       Before (laboratory 3)              After (laboratory 4)
FileRepository.loadFromFile        while + readLine + lineNumber++    Files.lines (try-with-resources) + collect;
 (was AbstractFileRepository)                                         IntStream.range + filter + forEach
FileRepository.saveToFile          for + writer.write + newLine       stream + map + collect, then Files.write
NetworkService.removeUser          for + if + toRemove.add, then      findAll(FriendshipCriteria.involving(id))
                                   a second for                       (stream + filter + collect), then forEach
ConnectivityLoader.load            two for + nested if                stream + map + forEach(method reference);
                                                                      findAll(predicate) with Stream.of + map + allMatch
LargestCommunityFinder.find        for + manual size comparison       stream + max(Comparator.comparingInt) + orElseGet
DynamicConnectivity.components     HashMap + for (group by tree),     stream + Collectors.groupingBy; map + sorted + collect;
 (the communities)                 for + Collections.sort, anonymous  Comparator.comparing (lambda instead of the anonymous class)
                                   Comparator
UserIdGenerator.nextId             for (fill a set) + while (id++)    stream + map + collect(toSet);
                                                                      LongStream.iterate + filter + findFirst
RepositoryConfig.getType           two for (search, StringBuilder)    Arrays.stream + filter + findFirst + orElseThrow;
                                                                      map + Collectors.joining
ConsoleUI.showUsers / showFriendships   for + boolean flag            isEmpty() + forEach(System.out::println)
ConsoleUI.showMostSociableCommunity     HashMap + for, for + println  stream + Collectors.toMap; stream + map + forEach
ConsoleUI.run                      switch on the option               Map<String, Runnable> of method references
RepositoryFactory                  switch on the type (x2)            EnumMap<RepositoryType, Function> (x2)

Pipelines: filter, map, sorted and flatMap are intermediate operations (lazy; nothing runs
until a terminal operation is called). collect, forEach, findFirst, max, count and allMatch
are terminal operations (they run the pipeline and consume the stream). A stream is used once:
Repository.stream() returns a new one at every call and is the only place where
StreamSupport.stream(findAll().spliterator(), false) is written.

NetworkService.friendsOf (friends of a user, sorted with Comparator.comparing(...).thenComparing(...))
and NetworkService.findUsers(Predicate) were added so the service exposes the operations
named in the statement; they are not connected to a menu option because the statement asks
for no new functionality.


L4-2. FUNCTIONAL INTERFACES AND METHOD REFERENCES
--------------------------------------------------
Own interface:
  Validator<T>        @FunctionalInterface with the default method andThen(Validator<T>).
                      Main composes the existing UserValidator with a new rule written as a
                      lambda (names at most 50 characters): new UserValidator().andThen(nameLength).
                      UserValidator was not modified.
Standard interfaces (java.util.function):
  Predicate<E>        Repository.findAll(Predicate<E>), UserCriteria, FriendshipCriteria,
                      combined with and, negate, Predicate.not; NetworkService.findUsers.
  Function<A, B>      FileRepository (Function<String, E> and Function<E, String>),
                      RepositoryFactory (Function<Validator<E>, Repository<ID, E>> in the Map),
                      Function.identity() in ConsoleUI.
  Runnable            ConsoleUI menu actions.
  Comparator          comparingInt, comparing, thenComparing (no hand-written compare, no a - b).
  Consumer            behind forEach (System.out::println, connectivity::addVertex).
New criterion without a new class: service.findUsers(u -> u.getId() > 2).

Method references, all four kinds:
  static method                  UserLines::parse, FriendshipLines::format (RepositoryFactory)
  method of a given object       System.out::println, connectivity::addVertex, users::findOne,
                                 userRepository::findOne, byId::get, this::addUser
  method of an arbitrary object  User::getId, User::getLastName, List::size, Optional::isPresent,
                                 Optional::stream, RepositoryType::name
  constructor                    InMemoryRepository::new (RepositoryFactory), ArrayList::new
                                 (LargestCommunityFinder)
(Utilizator::new has no equivalent: users are created from user input with an id, not by a
function.)


L4-3. TEMPLATE METHOD VS FUNCTIONS IN THE FILE REPOSITORY
---------------------------------------------------------
Chosen: one class FileRepository<ID, E> that receives Function<String, E> and Function<E, String>
(composition); the subclasses UserFileRepository, FriendshipFileRepository and AbstractFileRepository
were removed. Reasons:
- In laboratory 3 the constructor of the abstract class called the abstract method extractEntity
  before the fields of the subclass were initialized (the Javadoc had to forbid using them).
  With functions, FileRepository assigns its fields first and loads the file last, and the
  class is final, so this problem cannot appear.
- The line formats are stateless, so they became static methods (UserLines, FriendshipLines) that
  are easy to test alone and that are passed as method references.
- Less code: two classes replaced by two small utility classes and no inheritance.
Price: the line format cannot override other behaviour (save, delete), which is not needed here.
Template Method would be better if a subclass had to change more than the two conversions.


L4-4. REPOSITORY FACTORY WITHOUT SWITCH
---------------------------------------
For each of the two repositories a local EnumMap<RepositoryType, Function<Validator<E>, Repository<ID, E>>>
maps MEMORY, FILE and DATABASE to a function that creates the repository. The chosen
function is the only one that runs, so the properties of the other types are not required
(same behaviour as the switch: a FILE configuration does not need db.url). A type without a
function throws RepositoryException "Unsupported repository type". The DATABASE repositories
are still skeletons that throw UnsupportedOperationException (laboratory 5).


L4-5. OPTIONAL
--------------
Repository.findOne returns Optional<E> since laboratory 3, so the interface and its
implementations (InMemoryRepository, FileRepository, the two DB skeletons) did not change;
Javadoc already says "empty Optional". Chosen to keep Optional because null forces every
caller to remember a check, while Optional makes the "may be missing" case part of the type.
Optional is used only as a return type, never as a field or a parameter. Where it is used:
NetworkService.findUser (Optional.ofNullable(id).flatMap(...).orElseThrow(ServiceException)),
friendsOf (flatMap(Optional::stream)), ConnectivityLoader (allMatch(Optional::isPresent)),
LargestCommunityFinder (max + orElseGet), RepositoryConfig.getType (findFirst + orElseThrow),
UserIdGenerator (findFirst + orElseThrow). get() is never called.


L4-6. EXCEPTIONS AND FILES
--------------------------
Files.lines is read in a try-with-resources and copied into a List (the line numbers are needed
for the warnings). IOException and UncheckedIOException (thrown by the stream while reading,
for example for invalid UTF-8) are both wrapped in RepositoryException("Cannot read file ..."),
as in laboratory 3. Files.write throws IOException, wrapped in RepositoryException as before.
No checked exception is thrown inside a lambda.


L4-7. LOOPS THAT WERE KEPT
--------------------------
ConsoleUI.run (while (true), the menu loop) and ConsoleUI.readLong (while (true), asks until a
number is typed). They are interaction loops that end on user input (return or a valid number),
not traversals of a collection, so a stream does not fit (a stream over Scanner input would
hide the exit conditions). No other for / while / do-while exists in domain, service,
repository or ui. In the package structures only components() (the communities) was
rewritten with streams and its anonymous Comparator was replaced by a lambda; the other
loops there (DynamicConnectivity, Treap, Edge) belong to the connectivity algorithms (Euler tour
forest, treap), are outside the packages listed in the restriction, and were kept as they are.


L4-8. FOR THE DEMONSTRATION
---------------------------
- Same results as laboratory 3: run the application on data/users.txt and data/friendships.txt,
  and on data/test_corrupt (the warnings with the line numbers are the same).
- Before / after: see L4-1 (the laboratory 3 code is in the Git history).
- New validation rule only by lambda: Main.createUi (nameLength).
- New search criterion only by lambda: service.findUsers(u -> ...) or
  UserCriteria.firstNameStartsWith("A").and(UserCriteria.withoutFriends(connectivity)).


L3-1. HOW THE FACTORY WORKS
------------------------
RepositoryFactory (package repository) is a Simple Factory and the only class of the
application that knows the concrete repository classes. It has one creation method per
repository:

    createUserRepository(Validator<User>)             -> Repository<Long, User>
    createFriendshipRepository(Validator<Friendship>) -> Repository<Tuple<Long,Long>, Friendship>

Both methods read the same persistence type from RepositoryConfig, so the two repositories
are always created for the same kind of storage:

    repository.type=MEMORY    -> InMemoryRepository
    repository.type=FILE      -> FileRepository (with UserLines / FriendshipLines)
    repository.type=DATABASE  -> UserDbRepository / FriendshipDbRepository (skeletons)

RepositoryConfig loads src/main/resources/config.properties with java.util.Properties
(in a try-with-resources). Nothing is hard-coded in the Java code except the path of the
configuration file. A missing property, a blank value or an unknown repository.type throws
a RepositoryException whose message names the property and the file; Main catches it and
prints "Cannot start the application: ...".

Since laboratory 4 the factory has no switch: see L4-4. Main, ConsoleUI and the services depend only on the Repository interface. The friendship
repository receives a UsersExistValidator, which needs the user repository: it is created
in Main from the abstract Repository, so no concrete class leaks out of the factory.


L3-2. WHAT MUST CHANGE FOR THE DATABASE (LABORATORY 5)
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


L3-3. MISSING FILE AND CORRUPT LINES
---------------------------------
Missing file: it is not an error. FileRepository starts empty and creates the file
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
If writing a file fails, FileRepository undoes the change in memory and rethrows.

Where the validation is: in the repository (InMemoryRepository.save calls the validator).
Data coming from the file is untrusted, so it must be validated too, not only the data typed
in the UI. Putting the validation in the service would let a corrupt file bypass it. The
service keeps only the business rules that need several repositories (for example "the
user exists" before adding a friendship).

Exceptions are caught where they can be handled: ConsoleUI shows a message, Main reports
a failed start. Repository and service only throw (unchecked ValidationException,
RepositoryException, ServiceException); there are no empty catch blocks and no generic
catch (Exception).


L3-4. DESIGN PRINCIPLES (SOLID)
----------------------------
SRP  Each class has one reason to change. Repository classes store data, the validators
     validate, NetworkService manages users and friendships, CommunityService answers
     community queries, UserIdGenerator chooses ids, ConnectivityLoader fills the
     connectivity, RepositoryConfig reads the settings, RepositoryFactory creates objects.

OCP  A new entity stored in a file needs only a parse and a format function for FileRepository. A
     new definition of "most sociable" or a new connectivity algorithm is a new
     implementation of SociableCommunityFinder or Connectivity, without touching the
     services. The factory is only partly open: see section 5.

LSP  InMemoryRepository and FileRepository can be used wherever a
     Repository is expected and respect the contract in the Javadoc of Repository (for
     example delete(null) throws IllegalArgumentException, delete of a missing id throws
     RepositoryException). The DB skeletons only honour the signatures; they throw
     UnsupportedOperationException for every call, which violates the contract on purpose
     and temporarily, until laboratory 5.

DIP  Services and UI depend on the abstractions Repository, Connectivity and
     SociableCommunityFinder. All concrete classes are created in Main and in
     RepositoryFactory and passed through constructors (dependency injection).

(ISP: Repository has only the four operations that every user of it needs.)


L3-5. SIMPLE FACTORY VS ABSTRACT FACTORY (OCP)
-------------------------------------------
Simple Factory (used here): one class that chooses the repository by the repository type (a switch in laboratory 3, a Map of functions since laboratory 4).
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


L3-6. PATTERNS IN THE PROJECT
--------------------------
Simple Factory   RepositoryFactory.
Template Method  (laboratory 3 only) replaced by functions in laboratory 4, see L4-3.
Strategy         Validator<T> is injected into the repositories; SociableCommunityFinder
                 is injected into CommunityService (LargestCommunityFinder).
Decorator        UsersExistValidator wraps a FriendshipValidator, implements the same
                 interface and adds the check that both users exist.
Dependency       Main builds the objects and passes them through constructors.
Injection

Other notes: FileRepository extends InMemoryRepository (the file is just a persistent
copy of the in-memory map). The data files are plain UTF-8 text with ';' as separator, read
with Files.lines and written with Files.write (java.nio.file). The format did not change.
