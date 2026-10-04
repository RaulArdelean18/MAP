package Service;

import exception.ExpressionException;
import operations.OperationFactory;
import parser.ComplexNumberParser;
import parser.NumberParser;
import postfix.ExpressionEvaluator;
import postfix.InfixToPostfixConverter;
import postfix.PostfixEvaluator;
import postfix.TokenQueue;
import Repository.ExpressionRepository;

import java.io.IOException;

public class ExpressionService {
    private final ExpressionRepository repository;
    private final InfixToPostfixConverter converter;
    private final ExpressionEvaluator evaluator;

    public ExpressionService(ExpressionRepository repository) {
        this.repository = repository;

        NumberParser numberParser = new ComplexNumberParser();
        OperationFactory factory = OperationFactory.getInstance();
        this.converter = new InfixToPostfixConverter(numberParser, factory, false, false);
        this.evaluator = new PostfixEvaluator();
    }

    public void processExpression() throws IOException, ExpressionException {
        String rawContent = repository.readExpression();

        if (rawContent.isEmpty()) {
            repository.saveResult("Fisierul de intrare este gol.");
            return;
        }

        // Pregătim token-urile
        String formatted = rawContent.replace("(", " ( ").replace(")", " ) ").trim();
        String[] tokens = formatted.split("\\s+");

        // Conversie în postfix
        TokenQueue postfix = converter.convert(tokens);
        String postfixText = postfix.asText(); // Salvează textul înainte de evaluare

        // Evaluare
        Object result = evaluator.evaluate(postfix);

        // Generare raport rezultat
        String output = "Expresia " + rawContent + " este valida." + System.lineSeparator()
                + "Forma postfixata: " + postfixText + System.lineSeparator()
                + "Rezultat: " + result + System.lineSeparator();

        // Salvare prin repository
        repository.saveResult(output);
    }
}