public class ReservedWords {
    // taken from https://www.tutorialspoint.com/pascal/pascal_basic_syntax.htm
    static final String[] RESERVED_WORDS = { "program", "var", "begin", "end", "if", "then", "else", "while", "do",
            "repeat", "until", "for", "to", "downto", "write", "writeln", "and", "array", "case", "const", "div", "file",
            "function", "goto", "in", "label", "mod", "nil", "not", "of", "or", "packed", "procedure", "record", "set", 
            "type", "with", "input", "output", "real", "readline", "writeline", "readln"   };

    public static boolean isReservedWord(String s) {
        for (String word: RESERVED_WORDS) {
            if(s.contains(word)) {
                return  true;
            }
        }
        return  false;
    }
}
