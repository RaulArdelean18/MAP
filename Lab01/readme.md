# MAP - Lab 1: Complex number expression evaluator

## 1. Design overview

The expression is given on the command line, one number or operator per argument, for example:

```
java Main 2+3*i "*" "(15+20*i" - "30+2*i)"
```

`Main` calls `Calculator.ExpressionCalculator.calculate(String[])`, which runs three steps:

1. **`Parser.InfixExpressionParser`** (implements `Parser.ExpressionParser`) turns the arguments into a `Token.Token[]`. Numbers are read by `Parser.ComplexNumberParser`, operators are created by `service.OperationFactory`, and parentheses become tokens of their own. At the end it calls `Parser.ExpressionValidator`, which checks the order of the tokens and the parentheses.
2. **`Postfix.InfixToPostfixConverter`** (shunting-yard algorithm) converts the expression to postfix form, using `Structures.ArrayStack` and `Structures.ArrayQueue`.
3. **`Postfix.PostfixEvaluator`** evaluates the postfix expression with an operand stack.
   A `Token.Token` is a number, an operator (`service.ComplexOperation`) or a parenthesis (`Token.TokenType`). `Domain.ComplexNumber` is immutable.

The operations (`+`, `-`, `*`, `/`) are classes implementing the `service.ComplexOperation` interface (`apply`, `getSymbol`, `getPrecedence`). `service.OperationFactory` creates them from their symbol.

Only plain arrays are used: `Structures.ArrayStack` and `Structures.ArrayQueue` are our own data structures.

## 2. Error handling

- Invalid expressions (missing or extra operator, malformed number, unbalanced parentheses) throw `Exception.InvalidExpressionException`.
- Division by zero throws `ArithmeticException`.
- `Main` catches both and prints the message.
## 3. SOLID principles

| Principle | How it is applied |
|---|---|
| **S** - Single Responsibility | Each class has a single responsibility (parsing, validation, conversion, evaluation, arithmetic). |
| **O** - Open/Closed | A new operator is added with a new class implementing `service.ComplexOperation`. The only class that has to change is `service.OperationFactory`; the parser, converter and evaluator stay unchanged. |
| **L** - Liskov Substitution | Any implementation of `service.ComplexOperation` or `Parser.ExpressionParser` can be used in place of another. |
| **I** - Interface Segregation | The interfaces are small (`service.ComplexOperation`, `Parser.ExpressionParser`). |
| **D** - Dependency Inversion | `Calculator.ExpressionCalculator` uses the parser through the `Parser.ExpressionParser` interface, and the converter and evaluator work with `service.ComplexOperation`, not with the concrete operations. |

## 4. Design patterns

| Pattern | Where |
|---|---|
| **Strategy** | `service.ComplexOperation` and its implementations; `Parser.ExpressionParser` (allows alternative parsers). |
| **Simple Factory** | `service.OperationFactory.create(symbol)`. |
| **Singleton** | `service.OperationFactory` (private constructor, `getInstance()`). |
| **Facade** | `Calculator.ExpressionCalculator` hides the parser, converter and evaluator behind the `calculate(String[])` method. |

## 5. How the UML diagram reflects this

- Interfaces are connected to their implementations with realization relationships (Strategy, Open/Closed, Liskov).
- `service.OperationFactory`: static attribute `instance` (Singleton) and `«create»` dependencies to the concrete operations (Simple Factory).
- `Calculator.ExpressionCalculator` has associations to `Parser.ExpressionParser`, `Postfix.InfixToPostfixConverter` and `Postfix.PostfixEvaluator` (Facade).
- `Parser.InfixExpressionParser` has associations to `Parser.ComplexNumberParser`, `Parser.ExpressionValidator` and `service.OperationFactory`.
- Each step is a separate class (Single Responsibility).
- `Structures.ArrayStack` and `Structures.ArrayQueue` are used by the converter and the evaluator.
- `Parser.ExpressionValidator` has a `«throws»` dependency to `Exception.InvalidExpressionException`.
## 6. Running and testing

Compile and run:

```
javac -d out src/*.java
java -cp out Main 1+i + 2
```

JUnit 5 tests are in `ExpressionCalculatorTest` (valid expressions, invalid expressions, and expressions with parentheses).