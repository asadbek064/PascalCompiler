import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

public class SyntaxCheck {
  /* Variables */
  private int j;
  private Stack<Integer> stack = new Stack<Integer>();
  private FileReader in_fp;
  private FileWriter out_fp;
  private FileWriter out_fp2;

  /* Character classes */
  private final int LETTER = 0;
  private final int DIGIT = 1;
  private final int UNKNOWN = 99;
  private final int EOF = 100;

  /* Constructor */
  public SyntaxCheck(String inputFileName, String outputFileName) throws IOException {
    try {
      out_fp = new FileWriter(outputFileName);
      String content = Files.readString(Paths.get(inputFileName)).replace(",,;,,", "");
      content = RemoveComments(content);
      String[] lines = content.split(",,27");
      int top_of_stack;

      for (String line : lines) {
        String[] current_line = line.split(",,");
        int current_line_length = current_line.length;

        for (int i = 0; i < current_line_length; i += 2) {
          /*
           * System.out.print(current_line[i]);
           * System.out.print(current_line[i + 1]);
           */
          int current_token = Integer.parseInt(current_line[i]);
          String current_word = current_line[i + 1];
          // System.out.println(current_token+ " " + current_word);

          switch (current_token) {
            case Token.BEGIN, Token.LEFT_PAREN, Token.LEFT_BRACE, Token.LEFT_BRACKET:
              stack.push(current_token);
              break;
            case Token.SINGLE_QUOTE, Token.DOUBLE_QUOTE:
              quoteCheck(current_token);
              break;
            case Token.RIGHT_PAREN, Token.RIGHT_BRACKET, Token.RIGHT_BRACE, Token.END:
              // since right is 1 higher than left in all cases, this trick works
              checkMatching(current_token - 1);
              break;
          }
        }

        // reconstruct current_line to original raw source
        String raw_line = "";
        for (int i = 0; i < current_line_length; i++) {
            if(i % 2 == 1) {
              raw_line += (' ' + current_line[i]);
            }
        }

        // Assignment statements
        if (raw_line.contains(":=")){
          if (!isPascalAssignment(raw_line)) {
            System.out.println("ERROR: Not Valid Assignment statement: " + raw_line);
          }
        }

        // Variable declarations ( and initializing)
        if (raw_line.contains("var")) {
          if(!isVariableDeclaration(raw_line)) {
            System.out.println("ERROR: Not Var declaration/init: " + raw_line);
          }
        }

        // Arithmetic Operations
        if(Operators.isArithmeticOperator(raw_line)) {
          if(!isArithmeticOperation(raw_line)) {
            System.out.println("ERROR: Not Valid Arithmetic Op: "+ raw_line);
          }
        }

        // Boolean Expressions
        if(Operators.isRelationalOperator(raw_line)) {
          if(!isBooleanOperation(raw_line)) {
            System.out.println("ERROR: Not Valid Boolean Op: "+ raw_line);
          }
        }

        // “If” statements
        if(raw_line.contains("if") || raw_line.contains("if(")) {
          if(!isIfStatement(raw_line)) {
            System.out.println("ERROR: Not Valid If statement: "+ raw_line);
          }
        }
      }

      // if stack is not empty, print error
      if (stack.size() != 0) {
        System.out.println("error - parantheses/begin-end/quotes not paired properly");
      }

    } catch (FileNotFoundException e) {
      System.out.println("ERROR - cannot open " + inputFileName);
    }
  }

  // Check for a valid assignment statement
  public static boolean isPascalAssignment(String line) {
    return line.contains(":=") && !line.contains("=") && !line.contains(":=:");
  }

  // Check for a valid variable declaration
  public static boolean isVariableDeclaration(String line) {
    line = line.trim(); // remove leading and trailing white spaces
    return line.contains("=") && !line.contains("==") && line.indexOf('=') == line.lastIndexOf('=');
  }

  // Check for Arithmetic Operations
  public static boolean isArithmeticOperation(String line) {
    line = line.trim(); // remove white spaces
    for (String op : Operators.ARITHMETIC_OPERATORS) {
      if (line.contains(op)) {
        return true;
      }
    }
    return false;
  }

  // Check Boolean Expressions
  public static boolean isBooleanOperation(String line) {
    line = line.trim(); // remove leading and trailing white spaces
    for (String op : Operators.RELATIONAL_OPERATORS) {
      if (line.contains(op)) {
        return true;
      }
    }
    return false;
  }

  // Check for if statements
  public static boolean isIfStatement(String line) {
    line = line.trim(); // remove leading and trailing white spaces
    return line.startsWith("if ") || line.startsWith("if(");
  }

  // Check for matching delimiters (separators)
  private void checkMatching(int expectedDelimiter) {
    if (stack.isEmpty() || stack.peek() != expectedDelimiter) {
      System.out.println("Error: Mismatched delimiters");
    } else {
      stack.pop();
    }
  }

  // check for single and double quotes
  private void quoteCheck(int token) {
    if (stack.isEmpty() || stack.peek() != token) {
      stack.push(token);
    } else {
      stack.pop();
    }
  }

  // Clear outdated delimiter stack
  private void clearStack() {
    while (!stack.isEmpty() && stack.peek() != Token.LEFT_BRACE) {
      stack.pop();
    }
    // Remove left brace from stack
    if (!stack.isEmpty() && stack.peek() == Token.LEFT_BRACE) {
      stack.pop();
    }
  }

  // Check for any remaining unmatched delimiters
/*  private void checkStack() {
    while (!stack.isEmpty()) {
      switch (stack.pop()) {
        case Token.LEFT_PAREN:
          System.out.println("Error: Mismatched parentheses");
          break;
        case Token.LEFT_BRACKET:
          System.out.println("Error: Mismatched square brackets");
          break;
        case Token.LEFT_BRACE:
          System.out.println("Error: Mismatched curly braces");
          break;
        case Token.SINGLE_QUOTE:
          System.out.println("Error: Unterminated string literal: missing open/close single quote");
          break;
        case Token.DOUBLE_QUOTE:
          System.out.println("Error: Unterminated string literal: missing open/close double quote");
          break;
        case Token.BEGIN:
          System.out.println("Error: Missing END");
          break;
      }
    }
  }*/

  public String RemoveComments(String text) {
    String startDelim = "25,,(,,23,,*";
    String endDelim = ",,23,,*,,26,,),,";
    int startIndex = text.indexOf(startDelim);
    while (startIndex > -1) {
      int endIndex = text.indexOf(endDelim, startIndex);
      String endPart = "";

      if ((endIndex + endDelim.length()) < text.length())
        endPart = text.substring(endIndex + endDelim.length());

      text = text.substring(0, startIndex) + endPart;
      startIndex = text.indexOf(startDelim);
    }
    // System.out.println(text);
    return text;
  }
}
