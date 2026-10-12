# MAP - Lab 1: Complex number expression evaluator

## 1. Design overview

The expression is given on the command line, one number or operator per argument, for example:

```
java Main 2+3*i "*" "(15+20*i" - "30+2*i)"
```

`Main` calls `ExpressionCalculator.calculate(String[])`, which runs three steps:

1. **`InfixExpressionParser`** (implements `ExpressionParser`) turns the arguments into a `Token[]`. Numbers are read by `ComplexNumberParser`, operators are created by `OperationFactory`, and parentheses become tokens of their own. At the end it calls `ExpressionValidator`, which checks the order of the tokens and the parentheses.
2. **`InfixToPostfixConverter`** (shunting-yard algorithm) converts the expression to postfix form, using `ArrayStack` and `ArrayQueue`.
3. **`PostfixEvaluator`** evaluates the postfix expression with an operand stack.
   A `Token` is a number, an operator (`ComplexOperation`) or a parenthesis (`TokenType`). `ComplexNumber` is immutable.

The operations (`+`, `-`, `*`, `/`) are classes implementing the `ComplexOperation` interface (`apply`, `getSymbol`, `getPrecedence`). `OperationFactory` creates them from their symbol.

Only plain arrays are used: `ArrayStack` and `ArrayQueue` are our own data structures.

## 2. Error handling

- Invalid expressions (missing or extra operator, malformed number, unbalanced parentheses) throw `InvalidExpressionException`.
- Division by zero throws `ArithmeticException`.
- `Main` catches both and prints the message.
## 3. SOLID principles

| Principle | How it is applied |
|---|---|
| **S** - Single Responsibility | Each class has a single responsibility (parsing, validation, conversion, evaluation, arithmetic). |
| **O** - Open/Closed | A new operator is added with a new class implementing `ComplexOperation`. The only class that has to change is `OperationFactory`; the parser, converter and evaluator stay unchanged. |
| **L** - Liskov Substitution | Any implementation of `ComplexOperation` or `ExpressionParser` can be used in place of another. |
| **I** - Interface Segregation | The interfaces are small (`ComplexOperation`, `ExpressionParser`). |
| **D** - Dependency Inversion | `ExpressionCalculator` uses the parser through the `ExpressionParser` interface, and the converter and evaluator work with `ComplexOperation`, not with the concrete operations. |

## 4. Design patterns

| Pattern | Where |
|---|---|
| **Strategy** | `ComplexOperation` and its implementations; `ExpressionParser` (allows alternative parsers). |
| **Simple Factory** | `OperationFactory.create(symbol)`. |
| **Singleton** | `OperationFactory` (private constructor, `getInstance()`). |
| **Facade** | `ExpressionCalculator` hides the parser, converter and evaluator behind the `calculate(String[])` method. |

## 5. How the UML diagram reflects this

- Interfaces are connected to their implementations with realization relationships (Strategy, Open/Closed, Liskov).
- `OperationFactory`: static attribute `instance` (Singleton) and `«create»` dependencies to the concrete operations (Simple Factory).
- `ExpressionCalculator` has associations to `ExpressionParser`, `InfixToPostfixConverter` and `PostfixEvaluator` (Facade).
- `InfixExpressionParser` has associations to `ComplexNumberParser`, `ExpressionValidator` and `OperationFactory`.
- Each step is a separate class (Single Responsibility).
- `ArrayStack` and `ArrayQueue` are used by the converter and the evaluator.
- `ExpressionValidator` has a `«throws»` dependency to `InvalidExpressionException`.
## 6. Running and testing

Compile and run:

```
javac -d out src/*.java
java -cp out Main 1+i + 2
```

JUnit 5 tests are in `ExpressionCalculatorTest` (valid expressions, invalid expressions, and expressions with parentheses).