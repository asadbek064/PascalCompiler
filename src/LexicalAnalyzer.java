import java.io.FileNotFoundException;
import java.io.FileReader;
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
    /* Character classes */
    private final int LETTER = 0;
    private final int DIGIT = 1;
    private final int UNKNOWN = 99;
    private  final int EOF = 100;
    /* Token codes */
    private final int INT_LIT = 10;
    private final int IDENT = 11;
    private final int ASSIGN_OP = 20;
    private final int ADD_OP = 21;
    private final int SUB_OP = 22;
    private final int MULT_OP = 23;
    private final int DIV_OP = 24;
    private final int LEFT_PAREN = 25;
    private final int RIGHT_PAREN = 26;

    /* Constructor */
    public LexicalAnalyzer(String filename) throws IOException {
        try {
            in_fp = new FileReader(filename);
            getChar();

            while (nextToken != EOF) {
                lex();
            }

            in_fp.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR - cannot open " + filename);
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

    // reads a character from an input stream and classifies it as a letter, digit, unknown, or end of file.
    public void getChar() throws IOException {
        try {
            int temp = in_fp.read();

            if (temp != -1) {
                nextChar = (char) temp;

                if (ValidCharacters.isValidCharacters(nextChar)){
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
        while (Character.isWhitespace(nextChar))
            getChar();
    }

    public int lookup(char ch) throws IOException {
        if (ch == '(') {
            addChar();
            return TokenTypes.LEFT_PAREN;
        } else if (ch == ')') {
            addChar();
            return TokenTypes.RIGHT_PAREN;
        } else if (ch == '+') {
            addChar();
            return TokenTypes.ADD_OP;
        } else if (ch == '-') {
            addChar();
            return TokenTypes.SUB_OP;
        } else if (ch == '*') {
            addChar();
            return TokenTypes.MULT_OP;
        } else if (ch == '/') {
            addChar();
            return TokenTypes.DIV_OP;
        } else if (ch == '=') {
            addChar();
            return TokenTypes.ASSIGN_OP;
        }  else {
            addChar();
            return TokenTypes.UNKNOWN;
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
                nextToken = IDENT;
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
                lookup(nextChar);
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
        System.out.println("Next token is: " + nextToken + " Next lexem is:"+lexeme);

        return nextToken;
    }
}
