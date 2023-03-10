public class ValidCharacters {
    static final String[] ARITHMETIC_OPERATORS = {"+", "-", "*", "/"};
    static final String[] RELATIONAL_OPERATORS = {"=", "<>", "<", "<=", ">", ">="};
    static final String[] LOGICAL_OPERATORS = {"and", "or", "not"};

    public static boolean isArithmeticOperator(String s) {
        for (String op : ARITHMETIC_OPERATORS) {
            if (s.contains(op)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRelationalOperator(String s) {
        for (String op : RELATIONAL_OPERATORS) {
            if (s.contains(op)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isLogicalOperator(String s) {
        for (String op : LOGICAL_OPERATORS) {
            if (s.contains(op)) {
                return true;
            }
        }
        return false;
    }

}
