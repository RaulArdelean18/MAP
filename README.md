# MAP – Advanced Programming Methods

Laboratory and seminar work for the Advanced Programming Methods course,
Faculty of Mathematics and Computer Science, Babeș-Bolyai University
(2026–2027, group 221/1).

Author: Raul Ardelean

## Contents

| Folder                                | Description |
|---------------------------------------|---|
| `Complex number expression evaluator/` | Laboratory 1: evaluator for expressions with complex numbers (infix to postfix, custom stack and queue). See `Lab01/readme.md`. |
| `Social network/`                     | Laboratories 2–3: console social network with users, friendships, communities and file persistence behind a repository factory. See `Social network/readme.md` and `Social network/readme.txt`. |
| `Seminar/Seminar3/`                   | Seminar 3: generics. |
| `Seminar/Seminar4/`                   | Seminar 4: repository factory and database repository skeleton. |

## How to run

Each folder is a separate IntelliJ IDEA project with its own `Main` class.
Open the folder you want, not the repository root.

Social network, from its folder:

    javac -d out/production src/Main.java src/*/*.java src/domain/validators/*.java
    java -cp out/production Main