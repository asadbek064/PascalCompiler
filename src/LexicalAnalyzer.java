import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class LexicalAnalyzer {
    /* Global declarations */
    /* Variables */
    private int charClass;
    private char[] lexeme = new char[100];
    private char nextChar;
    private int lexLen = 0;
    private int token;
    private int nextToken;
    private FileReader in_fp;

    private FileWriter out_fp;
    private FileWriter out_fp2;
    /* Character classes */
    private final int LETTER = 0;
    private final int DIGIT = 1;
    private final int UNKNOWN = 99;
    private final int EOF = 100;
    /* Token codes */
    private final int RES_WORD = 9;
    private final int INT_LIT = 10;
    private final int IDENT = 11;
    private final int ASSIGN_OP = 20;
    private final int ADD_OP = 21;
    private final int SUB_OP = 22;
    private final int MULT_OP = 23;
    private final int DIV_OP = 24;
    private final int LEFT_PAREN = 25;
    private final int RIGHT_PAREN = 26;
    private final int SEMI_COLON = 27;
    private final int PER = 28;
    private final int DOUBLE_QUOTE = 29;
    private final int MOD = 30;
    private final int COLON = 31;
    private final int SINGLE_QUOTE = 32;

    /* Constructor */
    public LexicalAnalyzer(String inputFileName, String outputFileName, String outputFileName2) throws IOException {
        try {
            in_fp = new FileReader(inputFileName);
            out_fp = new FileWriter(outputFileName);
            out_fp2 = new FileWriter(outputFileName2);
            getChar();

            while (nextToken != EOF) {
                nextToken = lex();
            }

            in_fp.close();
            out_fp.close();
            out_fp2.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR - cannot open " + inputFileName);
        }
    }

    /* Function declarations */
    public void addChar() {
        if (lexLen <= 98) {
            lexeme[lexLen++] = nextChar;
            lexeme[lexLen] = 0;
        } else
            System.out.println("Error - lexeme is too long");
    }

    // reads a character from an input stream and classifies it as a letter, digit,
    // unknown, or end of file.
    public void getChar() {
        try {
            int temp = in_fp.read();

            if (temp != -1) {
                nextChar = (char) temp;

                if (ValidCharacters.isValidCharacters(nextChar)) {
                    if (Character.isLetter(nextChar))
                        charClass = LETTER;
                    else if (Character.isDigit(nextChar))
                        charClass = DIGIT;
                } else {
                    charClass = UNKNOWN;
                }
            } else {
                charClass = EOF;
            }
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }

    public void getNonBlank() throws IOException {
        while (Character.isWhitespace(nextChar) && charClass != 100)
            getChar();
    }

    public int lookup(char ch) throws IOException {
        if (ch == '(') {
            addChar();
            return LEFT_PAREN;
        } else if (ch == ')') {
            addChar();
            return RIGHT_PAREN;
        } else if (ch == '+') {
            addChar();
            return ADD_OP;
        } else if (ch == '-') {
            addChar();
            return SUB_OP;
        } else if (ch == '*') {
            addChar();
            return MULT_OP;
        } else if (ch == '/') {
            addChar();
            return DIV_OP;
        } else if (ch == '=') {
            addChar();
            return ASSIGN_OP;
        } else if (ch == ';') {
            addChar();
            return SEMI_COLON;
        } else if (ch == '.') {
            addChar();
            return PER;
        } else if (ch == '"') {
            addChar();
            return DOUBLE_QUOTE;
        } else if (ch == '%') {
            addChar();
            return MOD;
        } else if (ch == ':') {
            addChar();
            return COLON;
        } else if (ch == '\'') {
            addChar();
            return SINGLE_QUOTE;
        } else {
            addChar();
            return UNKNOWN;
        }
    }

    public int lex() throws IOException {
        lexLen = 0;
        getNonBlank();
        switch (charClass) {
            /* Parse identifiers */
            case LETTER:
                addChar();
                getChar();
                while (charClass == LETTER || charClass == DIGIT) {
                    addChar();
                    getChar();
                }
                if (ReservedWords.isReservedWord(String.valueOf(lexeme))) {
                    nextToken = RES_WORD;
                } else {
                    nextToken = IDENT;
                }
                break;
            /* Parse integer literals */
            case DIGIT:
                addChar();
                getChar();
                while (charClass == DIGIT) {
                    addChar();
                    getChar();
                }
                nextToken = INT_LIT;
                break;
            /* Parentheses and operators */
            case UNKNOWN:
                nextToken = lookup(nextChar);
                getChar();
                break;
            /* EOF */
            case EOF:
                nextToken = EOF;
                lexeme[0] = 'E';
                lexeme[1] = 'O';
                lexeme[2] = 'F';
                break;
        }

        // write each result to out file
        out_fp.write("Next token is: " + nextToken + "\tNext lexeme is:" + String.valueOf(lexeme).trim() + "\n");
        out_fp2.write(nextToken + ",," + String.valueOf(lexeme).trim() + ",,");

        // for debugging
        System.out.println("Next token is: " + nextToken + "\tNext lexem is:" + String.valueOf(lexeme).trim() + "");
        lexeme = new char[100];
        return nextToken;
    }
}
