import exception.ExpressionException;
import Repository.ExpressionRepository;
import Repository.FileExpressionRepository;
import Service.ExpressionService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ExpressionRepository repo = new FileExpressionRepository("Lab01/input.txt", "Lab01/output.txt");
        ExpressionService service = new ExpressionService(repo);

        try {
            service.processExpression();
            System.out.println("All good, good bye!");
        } catch (ExpressionException e) {
            System.err.println("Expression error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error at read/write file: " + e.getMessage());
        }
    }
}