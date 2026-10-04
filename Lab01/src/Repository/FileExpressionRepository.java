package Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileExpressionRepository implements ExpressionRepository {
    private final Path inputPath;
    private final Path outputPath;

    public FileExpressionRepository(String inputFilePath, String outputFilePath) {
        this.inputPath = Path.of(inputFilePath);
        this.outputPath = Path.of(outputFilePath);
    }

    @Override
    public String readExpression() throws IOException {
        if (!Files.exists(inputPath)) {
            throw new IOException("Fisierul " + inputPath + " nu a fost gasit!");
        }
        return Files.readString(inputPath).trim();
    }

    @Override
    public void saveResult(String content) throws IOException {
        Files.writeString(outputPath, content);
    }
}