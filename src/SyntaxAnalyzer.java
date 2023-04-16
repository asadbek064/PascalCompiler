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
  private Stack<Integer> delimiterStack = new Stack<Integer>();
  // private Stack<Integer> ifStack = new Stack<Integer>(); // Stack for nested "if" statements
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
          System.out.print(current_line[i]);
          System.out.print(current_line[i + 1]);
          int current_token = Integer.parseInt(current_line[i]);
          String current_word = current_line[i + 1];

          // parantheses stack
          if (current_token == Token.LEFT_PAREN) {
            if (i != current_line_length - 2) {
              // check if current symbol is not the last thing on the line
              // (in which case it is an error)
              stack.push(Token.LEFT_PAREN);
            } else {
              System.out.print("error - line ended with left paran");
            }
          }
          if (current_token == Token.RIGHT_PAREN) {
            top_of_stack = stack.pop();
            if (top_of_stack != Token.LEFT_PAREN) {
              System.out.println("error - parantheses/begin-end/quotes not paired properly");
            }
          }

          // begin/end stack
          if (String.valueOf(current_word) == "BEGIN") {
            if (i != current_line_length - 2) {
              // check if current symbol is not the last thing on the line
              // (in which case it is an error)
              stack.push(Token.BEGIN);
            } else {
              System.out.print("error - line ended with BEGIN");
            }
          }
          if (String.valueOf(current_word) == "END") {
            top_of_stack = stack.pop();
            if (top_of_stack != Token.BEGIN) {
              System.out.println("error - parantheses/begin-end/quotes not paired properly");
            }
          }

          // single quote stack
          if (current_token == Token.SINGLE_QUOTE) {
            int top_of_stack_peek = stack.peek();
            if (top_of_stack_peek == Token.SINGLE_QUOTE) {
              stack.pop();
            } else {
              stack.push(Token.SINGLE_QUOTE);
            }
          }

          // double quote stack
          if (current_token == Token.DOUBLE_QUOTE) {
            int top_of_stack_peek = stack.peek();
            if (top_of_stack_peek == Token.DOUBLE_QUOTE) {
              stack.pop();
            } else {
              stack.push(Token.DOUBLE_QUOTE);
            }
          }

        }
        System.out.println();
      }

      // if stack is not empty, print error
      if (stack.size() != 0) {
        System.out.println("error - parantheses/begin-end/quotes not paired properly");
      }

    } catch (FileNotFoundException e) {
      System.out.println("ERROR - cannot open " + inputFileName);
    }
    // for matching, have a stack, and when you hit a close if it does not match the
    // open that is on the stack, print an error
    // for assignment statements, make sure that it's IDENT = number or IDENT
    // one main function that keeps checking the next lexem, call smaller functions
    // based on that, build the stack inside of that
    // stack should be global so everyone can access it
    // each function should run until it hits the end of a semi-colon

  }


  // Check for matching delimiters (separators)
  private void checkMatchingDelimiter(int expectedDelimiter) {
    if (delimiterStack.isEmpty() || delimiterStack.peek() != expectedDelimiter) {
      System.out.println("Error: Mismatched delimiters");
    } else {
      delimiterStack.pop();
    }
  }

  // Clear outdated delimiter stack
  private void clearDelimiterStack() {
    while (!delimiterStack.isEmpty() && delimiterStack.peek() != Token.LEFT_BRACE) {
      delimiterStack.pop();
    }
    // Remove left brace from stack
    if (!delimiterStack.isEmpty() && delimiterStack.peek() == Token.LEFT_BRACE) {
      delimiterStack.pop();
    }
  }


  // Check for a valid assignment statement
  private void checkAssignmentsStatement(List<Token> tokens, Token identifierToken) {
    int index = tokens.indexOf(identifierToken);
    if (index >= tokens.size() - 2) {
      return; // Not enough tokens for an assignment statement
    }
    Token assignToken = tokens.get(index + 1);
    Token valueToken = tokens.get(index + 2);
    if (assignToken.getType() != Token.ASSIGN_OP || (valueToken.getType() != Token.IDENT && valueToken.getType() != Token.INT_LIT)) {
      System.out.println("Error: Invalid assignment statement");
    }
  }

  // Check for any remaining unmatched delimiters
  private void checkDelimiterStack() {
    while (!delimiterStack.isEmpty()) {
      switch (delimiterStack.pop()) {
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
