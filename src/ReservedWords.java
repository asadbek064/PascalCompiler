public class ReservedWords {
    static final String[] RESERVED_WORDS = {"program", "var", "begin", "end", "if", "then", "else", "while", "do", "repeat","until", "for", "to", "downto", "write", "writeln"};

    public static boolean isReservedWord(String s) {
        for (String word: RESERVED_WORDS) {
            if(s.contains(word)) {
                return  true;
            }
        }
        return  false;
    }
}
