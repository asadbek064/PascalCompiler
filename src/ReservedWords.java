public class ReservedWords {
    // taken from https://www.tutorialspoint.com/pascal/pascal_basic_syntax.htm
    static final String[] RESERVED_WORDS = { "program", "var", "begin", "end", "if", "then", "else", "while", "do",
            "repeat", "until", "for", "to", "downto", "write", "writeln", "array", "case", "const", "div", "file",
            "function", "goto", "in", "label", "mod", "nil", "of", "packed", "procedure", "record", "set",
            "type", "with", "input", "output", "real", "readline", "writeline", "readln" };
    // not, or, and are all reserved words but I left them out for now because they
    // are logical operators, not sure how we are handling that yet

    public static boolean isReservedWord(String s) {
        s = s.toLowerCase().trim();
        for (String word : RESERVED_WORDS) {
            if (s.compareTo(word) == 0) {
                return true;
            }
        }
        return false;
    }
}
