import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

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
  private final int BEGIN = 7;
  private final int END = 8;
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
  public SyntaxAnalyzer(String inputFileName, String outputFileName) throws IOException {
    try {
      // in_fp = new FileReader(inputFileName);
      out_fp = new FileWriter(outputFileName);
      String content = Files.readString(Paths.get(inputFileName)).replace(",,;,,",
          "");
      // String content = Files.readString(Paths.get(inputFileName)).replace(",,;,,",
      // "");
      content = RemoveComments(content);
      // System.out.println(content);
      String[] lines = content.split(",,27");
      int top_of_stack;
      // for (int i = 0; i < contentLength; i++) {
      // System.out.println(lines[i] + '\n');
      // }
      for (String line : lines) {
        String[] current_line = line.split(",,");
        int current_line_length = current_line.length;
        for (int i = 0; i < current_line_length; i += 2) {
          System.out.print(current_line[i]);
          System.out.print(current_line[i + 1]);
          int current_token = Integer.parseInt(current_line[i]);
          String current_word = current_line[i + 1];

          // parantheses stack
          if (current_token == LEFT_PAREN) {
            if (i != current_line_length - 2) {
              // check if current symbol is not the last thing on the line
              // (in which case it is an error)
              stack.push(LEFT_PAREN);
            } else {
              System.out.print("error - line ended with left paran");
            }
          }
          if (current_token == RIGHT_PAREN) {
            top_of_stack = stack.pop();
            if (top_of_stack != LEFT_PAREN) {
              System.out.println("error - parantheses/begin-end/quotes not paired properly");
            }
          }

          // begin/end stack
          if (String.valueOf(current_word) == "BEGIN") {
            if (i != current_line_length - 2) {
              // check if current symbol is not the last thing on the line
              // (in which case it is an error)
              stack.push(BEGIN);
            } else {
              System.out.print("error - line ended with BEGIN");
            }
          }
          if (String.valueOf(current_word) == "END") {
            top_of_stack = stack.pop();
            if (top_of_stack != BEGIN) {
              System.out.println("error - parantheses/begin-end/quotes not paired properly");
            }
          }

          // single quote stack
          if (current_token == SINGLE_QUOTE) {
            int top_of_stack_peek = stack.peek();
            if (top_of_stack_peek == SINGLE_QUOTE) {
              stack.pop();
            } else {
              stack.push(SINGLE_QUOTE);
            }
          }

          // double quote stack
          if (current_token == DOUBLE_QUOTE) {
            int top_of_stack_peek = stack.peek();
            if (top_of_stack_peek == DOUBLE_QUOTE) {
              stack.pop();
            } else {
              stack.push(DOUBLE_QUOTE);
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
