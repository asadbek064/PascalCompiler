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
        String examine_line = line.replaceAll(",,", " ");
        String raw_line = "";
        for (int i = 0; i < current_line_length; i++) {
          if (i % 2 == 1) {
            raw_line += (' ' + current_line[i]);
          }
        }

        // Assignment statements
        if (examine_line.contains("=") && !(examine_line.contains("IF") || examine_line.contains("WHILE"))) {
          if (!isPascalAssignment(current_line)) {
            System.out.println("ERROR: Not Valid Assignment statement: " + raw_line);
          }
        }

        // Variable declarations ( and initializing)
        if (examine_line.contains(":") && !(examine_line.contains("=")) && !(examine_line.contains("'"))
            && !(examine_line.contains(String.valueOf(Token.DOUBLE_QUOTE)))) {
          if (!isVariableDeclaration(current_line)) {
            System.out.println("ERROR: Not Valid declaration/init: " + raw_line);
          }
        }

        // // Boolean Expressions
        // if (Operators.isRelationalOperator(raw_line)) {
        // if (!isBooleanOperation(raw_line)) {
        // System.out.println("ERROR: Not Valid Boolean Op: " + raw_line);
        // }
        // }

        // “If” statements
        if (current_line[1].equals("IF")) {
          if (!isIfStatement(current_line, examine_line)) {
            System.out.println("ERROR: Not Valid If statement: " + raw_line);
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
  public static boolean isPascalAssignment(String[] line) {
    Boolean part_one = false;
    Boolean part_two = false;
    Boolean part_three = false;
    if (Integer.parseInt(line[0]) == Token.IDENT) {
      part_one = true;
    } else {
      return false;
    }
    if (line[3].equals(":") && line[5].equals("=")) {
      part_two = true;
    } else {
      return false;
    }
    String[] new_line = new String[line.length - 6];
    for (int i = 6; i < line.length; i++) {
      new_line[i - 6] = line[i];
    }
    // can either be assigning to a var or it must be an arithmetic operation
    if (isArithmeticOperation(new_line) || Integer.parseInt(line[6]) == Token.IDENT) {
      part_three = true;
    } else {
      return false;
    }
    return true;
  }

  // Check for a valid variable declaration
  public static boolean isVariableDeclaration(String[] line) {
    int start_index = 0;
    if (line[1].equals("VAR")) {
      start_index += 2;
    }
    if (line.length != 6 + start_index) {
      return false;
    }
    String[] types = { "INTEGER", "STRING", "REAL", "CHARACTER", "BOOLEAN", "SET", "ARRAY" };
    Boolean part_one = false;
    Boolean part_two = false;
    Boolean part_three = false;

    // test to make sure parseInt does not break down
    try {
      int j = Integer.parseInt(line[start_index]);
    } catch (Exception e) {
      return false;
    } finally {
      int k;
    }

    // regular code
    if (Integer.parseInt(line[start_index]) == Token.IDENT) {
      part_one = true;
    } else {
      return false;
    }
    if (line[start_index + 3].equals(":")) {
      part_two = true;
    } else {
      return false;
    }
    for (String op : types) {
      if (line[start_index + 5].equals(op)) {
        part_three = true;
      }
    }
    if (!(part_three)) {
      return false;
    }
    return true;
  }

  // Check for Arithmetic Operations
  public static boolean isArithmeticOperation(String[] line) {
    Boolean part_one = false;
    Boolean part_two = false;
    Boolean part_three = false;
    if (Integer.parseInt(line[0]) == Token.INT_LIT || Integer.parseInt(line[0]) == Token.IDENT) {
      part_one = true;
    } else {
      return false;
    }
    for (String op : Operators.ARITHMETIC_OPERATORS) {
      if (line[3].equals(op)) {
        part_two = true;
      }
    }
    if (!part_two) {
      return false;
    }
    String[] new_line = new String[line.length - 4];
    for (int i = 4; i < line.length; i++) {
      new_line[i - 4] = line[i];
    }
    if ((line.length == 6 && (Integer.parseInt(line[4]) == Token.INT_LIT || Integer.parseInt(line[4]) == Token.IDENT))
        || isArithmeticOperation(new_line)) {
      part_three = true;
    } else {
      return false;
    }
    return true;
  }

  // Check Boolean Expressions
  public static boolean isBooleanOperation(String[] line) {
    Boolean part_one = false;
    Boolean part_two = false;
    Boolean part_three = false;
    Boolean part_four = false;
    if (Integer.parseInt(line[0]) == Token.LEFT_PAREN && Integer.parseInt(line[8]) == Token.RIGHT_PAREN) {
      part_one = true;
    } else {
      return false;
    }

    if (Integer.parseInt(line[2]) == Token.IDENT || Integer.parseInt(line[2]) == Token.INT_LIT) {
      part_two = true;
    } else {
      return false;
    }

    if (Integer.parseInt(line[6]) == Token.IDENT || Integer.parseInt(line[6]) == Token.INT_LIT) {
      part_three = true;
    } else {
      return false;
    }

    if (Integer.parseInt(line[4]) == Token.ASSIGN_OP) {
      part_four = true;
    } else {
      return false;
    }

    return true;
  }

  // Check for if statements
  public static boolean isIfStatement(String[] line, String string_line) {
    String[] new_line = new String[10];
    for (int i = 2; i < 12; i++) {
      new_line[i - 2] = line[i];
    }
    Boolean part_one = false;
    if (isBooleanOperation(new_line)) {
      part_one = true;
    } else {
      return false;
    }

    if (!(line[13].equals("THEN"))) {
      return false;
    }

    if (!(string_line.contains("ELSE"))) {
      return false;
    }
    return true;
    // return line.startsWith("if ") || line.startsWith("if(");
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
  /*
   * private void checkStack() {
   * while (!stack.isEmpty()) {
   * switch (stack.pop()) {
   * case Token.LEFT_PAREN:
   * System.out.println("Error: Mismatched parentheses");
   * break;
   * case Token.LEFT_BRACKET:
   * System.out.println("Error: Mismatched square brackets");
   * break;
   * case Token.LEFT_BRACE:
   * System.out.println("Error: Mismatched curly braces");
   * break;
   * case Token.SINGLE_QUOTE:
   * System.out.
   * println("Error: Unterminated string literal: missing open/close single quote"
   * );
   * break;
   * case Token.DOUBLE_QUOTE:
   * System.out.
   * println("Error: Unterminated string literal: missing open/close double quote"
   * );
   * break;
   * case Token.BEGIN:
   * System.out.println("Error: Missing END");
   * break;
   * }
   * }
   * }
   */

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
