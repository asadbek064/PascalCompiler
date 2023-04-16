import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Stack;
import java.util.List;



public class SyntaxAnalyzer {
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


  /* Token codes */
  /* converted to class based
     easier to use while checking delimiters.
     just call Token.[keyword]
   */

  /* Constructor */
  public SyntaxAnalyzer(String inputFileName, String outputFileName) throws IOException {
    try {
      out_fp = new FileWriter(outputFileName);
      String content = Files.readString(Paths.get(inputFileName)).replace(",,;,,","");
      content = RemoveComments(content);
      String[] lines = content.split(",,27");
      int top_of_stack;

      for (String line : lines) {
        String[] current_line = line.split(",,");
        int current_line_length = current_line.length;

        for (int i = 0; i < current_line_length; i += 2) {
          /*
           System.out.print(current_line[i]);
           System.out.print(current_line[i + 1]);
          */
          int current_token = Integer.parseInt(current_line[i]);
          String current_word = current_line[i + 1];
          // System.out.println(current_token+ " " + current_word);

          switch (current_token) {
            case Token.BEGIN, Token.LEFT_PAREN, Token.LEFT_BRACE, Token.LEFT_BRACKET, Token.SINGLE_QUOTE, Token.DOUBLE_QUOTE:
              stack.push(current_token);
              break;
            case Token.RIGHT_PAREN, Token.RIGHT_BRACKET, Token.RIGHT_BRACE:
              checkMatching(current_token);
              break;
            case Token.IDENT:
              checkAssignmentStatement(current_line, current_token);
              break;
            default:
              // ignore other tokens
              break;
          }
        }
        // check for any remaining unmatched delimiters
        checkStack();

        // Clear delimiter stack for Token.SEMI_COLON since lines are already split by semicolons.
        clearStack();
        System.out.println();
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
  private void checkAssignmentStatement(String[] current_line, int identifierIndex) {
    if (identifierIndex >= current_line.length - 2) {
      return; // Not enough tokens for an assignment statement
    }
    String assignToken = current_line[identifierIndex + 1];
    String valueToken = current_line[identifierIndex + 2];
    if (!assignToken.equals("=") || (!ValidCharacters.isIdentifier(valueToken) && !isNumeric(valueToken))) {
      System.out.println("Error: Invalid assignment statement");
    }
  }

  private boolean isNumeric(String token) {
    // Check if the token is a valid number
    return token.matches("-?\\d+(\\.\\d+)?");
  }

  // Check for matching delimiters (separators)
  private void checkMatching(int expectedDelimiter) {
    if (stack.isEmpty() || stack.peek() != expectedDelimiter) {
      System.out.println("Error: Mismatched delimiters");
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
  private void checkStack() {
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
  }

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