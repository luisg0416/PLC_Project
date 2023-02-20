package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.ast.*;
import edu.ufl.cise.plcsp23.Scanner;
import edu.ufl.cise.plcsp23.IToken.Kind;

public class Parser implements IParser {
    Scanner scanner;
    Parser(Scanner scan) {
        scanner = scan;
    }
    public AST parse() throws PLCException {
        Token x =  (Token)scanner.next();
        Token curr = x;
        NumLitExpr num = new NumLitExpr(curr);
        return num;
    }
}
