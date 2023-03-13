import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // default if none provided as argument
        String inputFile = "src/Input.txt";
        String outputFile = "src/Output.txt";

        if (args.length > 1) {
            inputFile = args[0];
            outputFile = args[1];
        }
        /* Main driver */
        new LexicalAnalyzer(inputFile, outputFile);
    }
}
