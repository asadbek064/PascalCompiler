# PascalCompiler

## Lexical Analyzer
- Input txt (Pascal statements)
- Perform lexical analysis on each statement
- Output expected
```txt
	Next token is: 25 Next lexeme is (
	Next token is: 11 Next lexeme is sum
	Next token is: 21 Next lexeme is +
```

- Need list structure for **reserved words**
- Need list structure for **operators by category**
- Need list structure for **characters for variables/constants**

## Syntax Check
```
9,,PROGRAM,,11,,ChangeMaker,,27,,;,,25,,(,,23,,*,,11,,Make,,11,,change,,9,,
```
Take the output from lexical analyzer and apply checks for
- Removes Comments
- Matching on parens, quotes, begin/end etc.
- Assignment statements
- Variable declarations ( and initializing)
- Arithmetic Operations
- Boolean Expressions
- “If” statements

[//]: # (- While loop &#40;not the body&#41;)

[//]: # (- For loop &#40;not the body&#41;)

## How to run
```bash
javac Main.java
javac Main .\Input.txt .\Output.txt
```

