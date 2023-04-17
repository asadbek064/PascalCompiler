public class ValidCharacters {

    private static final String VALID_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";

    public static boolean isValidCharacters(char c) {
        return  VALID_CHARACTERS.indexOf(c) != -1;
    }

    /*
      All upper case letters (A-Z)
      All lower case letters (a-z)
      All digits (0-9)
      Special symbols - + * / := , . ;. () [] = {} ` white space
      */
    public static boolean isIdentifier(String token) {
        // Check if the token is a valid identifier
        return token.matches("[a-zA-Z][a-zA-Z0-9]*");
    }
}
