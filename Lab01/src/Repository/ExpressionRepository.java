package Repository;

import java.io.IOException;

public interface ExpressionRepository {
    String readExpression() throws IOException;
    void saveResult(String content) throws IOException;
}