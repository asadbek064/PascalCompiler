
public class Main {

    public static void main(String[] args) {
        // testing structs
        String line = "PROGRAM  ChangeMaker".toLowerCase();

        System.out.println(Operators.isValidCharacters(line.charAt(0)));
        System.out.println(ReservedWords.isReservedWord(line));

        String line2 = "Remainder := 100 - Cost; x and y".toLowerCase();

        System.out.println(ValidCharacters.isArithmeticOperator(line2));
        System.out.println(ValidCharacters.isRelationalOperator(line2));
        System.out.println(ValidCharacters.isLogicalOperator(line2));

    }
}
