## Lexical Analyzer
- Input txt (Pascal statements)
- Perform lexical analysis on each statement
- Output expected
```txt
	Next token is: 25 Next lexeme is (
	Next token is: 11 Next lexeme is sum
	Next token is: 21 Next lexeme is +
```

[] Need list structure for **reserved words**
[] Need list structure for **operators by category**
[] Need list structure for **characters for variables/constants**

## Tests
Test validity of **identifiers**
ex:
if string doesn't not match any categories mark as **UNKNOWN**

## Classes
```
Main.class

ReservedWord.class
    RESERVED_WORDS = []
    
    isReservedWord()

Operators.class
    ARITHMETIC_OPERATORS = []
    RELATIONAL_OPERATORS = []
    LOGICAL_OPERATORS = []
    
    isArithmeticOperator()
    isRelationalOperator()
    isLogicalOperator()

ValidCharacters.class
    VALID_CHARACTERS = []

    isValidCharacter()	
```