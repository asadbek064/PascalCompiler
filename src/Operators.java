public class Operators {
    private static final String VALID_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";

    public static boolean isValidCharacters(char c) {
        return  VALID_CHARACTERS.indexOf(c) != -1;
    }
}
