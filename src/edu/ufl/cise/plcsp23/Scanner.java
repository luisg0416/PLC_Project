package edu.ufl.cise.plcsp23;

import java.util.Arrays;
import java.util.HashMap;
public class Scanner implements IScanner {
    String input;
    //array containing input chars, terminated with extra char 0
    char[] inputChars;
    //invariant ch == inputChars[pos]
    int pos; //position of ch
    char ch; //next char

    int line;
    int column;
    //constructor
    private enum State {
        START,
        IS_EQ,
        IN_IDENT,
        IN_NUM_LIT,
        IS_AMP,
        IS_EXCHANGE,
        IS_GREATER,
        IS_OR,
        IS_MULT,
        IN_STRING,
        IS_COMMENT

    }
    public static HashMap<String, IToken.Kind> reservedWords;
    public void createMap() {
        reservedWords = new HashMap<String,IToken.Kind>();

        reservedWords.put("image",IToken.Kind.RES_image);
        reservedWords.put("pixel",IToken.Kind.RES_pixel);
        reservedWords.put("int",IToken.Kind.RES_int);
        reservedWords.put("string",IToken.Kind.RES_string);
        reservedWords.put("void",IToken.Kind.RES_void);
        reservedWords.put("nil",IToken.Kind.RES_nil);
        reservedWords.put("load",IToken.Kind.RES_load);
        reservedWords.put("display",IToken.Kind.RES_display);
        reservedWords.put("write",IToken.Kind.RES_write);
        reservedWords.put("x",IToken.Kind.RES_x);
        reservedWords.put("y",IToken.Kind.RES_y);
        reservedWords.put("a",IToken.Kind.RES_a);
        reservedWords.put("r",IToken.Kind.RES_r);
        reservedWords.put("X",IToken.Kind.RES_X);
        reservedWords.put("Y",IToken.Kind.RES_Y);
        reservedWords.put("Z",IToken.Kind.RES_Z);
        reservedWords.put("x_cart",IToken.Kind.RES_x_cart);
        reservedWords.put("y_cart",IToken.Kind.RES_y_cart);
        reservedWords.put("a_polar",IToken.Kind.RES_a_polar);
        reservedWords.put("r_polar",IToken.Kind.RES_r_polar);
        reservedWords.put("rand",IToken.Kind.RES_rand);
        reservedWords.put("sin",IToken.Kind.RES_sin);
        reservedWords.put("cos",IToken.Kind.RES_cos);
        reservedWords.put("atan",IToken.Kind.RES_atan);
        reservedWords.put("if",IToken.Kind.RES_if);
        reservedWords.put("while",IToken.Kind.RES_while);
    }

    public static boolean commentCondition(char ch) {
        int value = (int) ch;
        return value >= 0 && value <= 127 && value !=10 && value != 13;
    }
    private boolean isNum(int ch) {
        return '0' <= ch && ch <= '9';
    }
    private boolean isLetter(int ch) {
        return ('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z');
    }
    private boolean isIdentStart(int ch) {

        return isLetter(ch) || (ch == '$') || (ch == '_');
    }
    private void error(String message) throws LexicalException{
        throw new LexicalException("Error at pos " + pos + ": " + message);
    }
    private boolean escapeSequence(char letter) {
        boolean flag = false;
        switch(letter) {
            case 'n','t','r','f','b','"' -> flag = true;
            default -> flag = false;
        }
        return flag;
    }
    public void nextChar() {
        if(ch == '\n') {
            line++;
            column = 1;
        }
        else {
            column++;
        }
        pos++;
        ch = inputChars[pos];
    }
    public Scanner(String input) {
        this.input = input;
        inputChars = Arrays.copyOf(input.toCharArray(),input.length()+1);
        pos = 0;
        ch = inputChars[pos];
        this.createMap();
        line = 1;
        column = 1;
    }

    @Override
    public IToken next() throws LexicalException {
        return scanToken();
    }

    private Token scanToken() throws LexicalException {
        State state = State.START;
        int tokenStart = -1;
        while (true) { //read chars, loop terminates when a Token is returned
            switch (state) {
                case START -> {
                    tokenStart = pos;
                    switch (ch) {
                        case 0 -> { //end of input
                            return new Token(IToken.Kind.EOF, tokenStart, 0, inputChars, line, column);
                        }
                        case ' ', '\n', '\r', '\t', '\f' -> nextChar();
                        case '+' -> {
                            nextChar();
                            return new Token(IToken.Kind.PLUS, tokenStart, 1, inputChars, line, column);
                        }
                        case '*' -> {
                            state = State.IS_MULT;
                            nextChar();
                        }
                        case '0' -> {
                            nextChar();
                            return new NumLitToken(IToken.Kind.NUM_LIT, tokenStart, 1, inputChars, line, column);
                        }
                        case '=' -> {
                            state = State.IS_EQ;
                            nextChar();
                        }
                        case '"' -> {
                            nextChar();
                            state = State.IN_STRING;

                        }
                        case '~' -> {
                            state = State.IS_COMMENT;
                            nextChar();
                        }
                        case '&' -> {
                            state = State.IS_AMP;
                            nextChar();
                        }
                        case '<' -> {
                            state = State.IS_EXCHANGE;
                            nextChar();
                        }
                        case '>' -> {
                            state = State.IS_GREATER;
                            nextChar();
                        }
                        case '.' -> {
                            nextChar();
                            return new Token(IToken.Kind.DOT, tokenStart, 1, inputChars, line, column);
                        }
                        case ',' -> {
                            nextChar();
                            return new Token(IToken.Kind.COMMA, tokenStart, 1, inputChars, line, column);
                        }
                        case '?' -> {
                            nextChar();
                            return new Token(IToken.Kind.QUESTION, tokenStart, 1, inputChars, line, column);
                        }
                        case ':' -> {
                            nextChar();
                            return new Token(IToken.Kind.COLON, tokenStart, 1, inputChars, line, column);
                        }
                        case '(' -> {
                            nextChar();
                            return new Token(IToken.Kind.LPAREN, tokenStart, 1, inputChars, line, column);
                        }
                        case ')' -> {
                            nextChar();
                            return new Token(IToken.Kind.RPAREN, tokenStart, 1, inputChars, line, column);
                        }
                        case '[' -> {
                            nextChar();
                            return new Token(IToken.Kind.LSQUARE, tokenStart, 1, inputChars, line, column);
                        }
                        case ']' -> {
                            nextChar();
                            return new Token(IToken.Kind.RSQUARE, tokenStart, 1, inputChars, line, column);
                        }
                        case '{' -> {
                            nextChar();
                            return new Token(IToken.Kind.LCURLY, tokenStart, 1, inputChars, line, column);
                        }
                        case '}' -> {
                            nextChar();
                            return new Token(IToken.Kind.RCURLY, tokenStart, 1, inputChars, line, column);
                        }
                        case '!' -> {
                            nextChar();
                            return new Token(IToken.Kind.BANG, tokenStart, 1, inputChars, line, column);
                        }
                        case '-' -> {
                            nextChar();
                            return new Token(IToken.Kind.MINUS, tokenStart, 1, inputChars, line, column);
                        }
                        case '/' -> {
                            nextChar();
                            return new Token(IToken.Kind.DIV, tokenStart, 1, inputChars, line, column);
                        }
                        case '%' -> {
                            nextChar();
                            return new Token(IToken.Kind.MOD, tokenStart, 1, inputChars, line, column);
                        }
                        case '|' -> {
                            state = State.IS_OR;
                            nextChar();

                        }
                        case '1','2','3','4','5','6','7','8','9' -> {
                            state = State.IN_NUM_LIT;
                            nextChar();
                        }
                        default -> {
                            if(isLetter(ch)){
                                state = State.IN_IDENT;
                                nextChar();
                            }
                            else{
                                throw new LexicalException("Switch case one no hit");
                            }
                        }
                    }
                }
                case IS_COMMENT -> {
                   if(ch == '\n') {
                       state = state.START;
                   }
                   else {
                       nextChar();
                   }
                }
                case IS_MULT -> {
                    state = state.START;
                    if (ch == '*') {
                        nextChar();
                        return new Token(IToken.Kind.EXP, tokenStart, 2, inputChars, line, column);
                    }
                    else {
                        return new NumLitToken(IToken.Kind.TIMES, tokenStart, 1, inputChars, line, column);
                    }
                }
                case IS_OR -> {
                    state = state.START;
                    if (ch == '|') {
                        nextChar();
                        return new Token(IToken.Kind.OR, tokenStart, 2, inputChars, line, column);
                    }
                    else {
                        return new NumLitToken(IToken.Kind.BITOR, tokenStart, 1, inputChars, line, column);
                    }
                }
                case IS_AMP -> {
                    state = state.START;
                    if (ch == '&') {
                        nextChar();
                        return new Token(IToken.Kind.AND, tokenStart, 2, inputChars, line, column);
                    }
                    else {
                        return new NumLitToken(IToken.Kind.BITAND, tokenStart, 1, inputChars, line, column);
                    }
                }
                case IS_EQ -> {
                    state = state.START;
                    if (ch == '=') {
                        nextChar();
                        return new Token(IToken.Kind.EQ, tokenStart, 2, inputChars, line, column);
                    }
                    else {
                        return new NumLitToken(IToken.Kind.ASSIGN, tokenStart, 1, inputChars, line, column);
                    }
                }
                case IN_NUM_LIT -> {
                    if(isNum(ch)){
                        nextChar();
                    }
                    else{
                        int length = pos - tokenStart;
                        try {
                            NumLitToken numLitToken = new NumLitToken(IToken.Kind.NUM_LIT, tokenStart, length, inputChars, line, column);
                            numLitToken.getValue();
                            return numLitToken;
                        }
                        catch (NumberFormatException e) {
                            throw new LexicalException("numLitTooBig");
                        }
                    }
                }
                case IN_IDENT -> {
                    if(isIdentStart(ch) || isNum(ch)){
                        nextChar();
                    }
                    else{
                        int length = pos - tokenStart;
                        String text = input.substring(tokenStart,tokenStart + length);
                        IToken.Kind kind = reservedWords.get(text);
                        if(kind == null) {
                            kind = IToken.Kind.IDENT;
                        }
                        return new Token(kind, tokenStart, length, inputChars, line, column-length);
                    }
                }

                case IS_EXCHANGE -> {
                    if(ch == '-' && inputChars[pos + 1] == '>') {
                        state = state.START;
                        nextChar();
                        nextChar();
                        return new Token(IToken.Kind.EXCHANGE, tokenStart, 3, inputChars, line, column);
                    }
                    else if (ch == '=') {
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.LE, tokenStart, 2, inputChars, line, column);
                    }
                    else if (ch == '-' && inputChars[pos + 1] != '>') {
                        throw new LexicalException("is incomplete");
                    }
                    else{
                        state = state.START;
                        return new Token(IToken.Kind.LT, tokenStart, 1, inputChars, line, column);
                    }
                }
                case IS_GREATER -> {
                    if(ch == '=') {
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.GE, tokenStart, 2, inputChars, line, column);
                    }
                    else {
                        state = state.START;
                        return new Token(IToken.Kind.GT, tokenStart, 1, inputChars, line, column);
                    }
                }
                case IN_STRING -> {
                    while (true) {
                        if (ch == 0) {
                            throw new UnsupportedOperationException("No Instance");
                        } else if (ch == '\\' && !escapeSequence(inputChars[pos + 1])) {
                            throw new LexicalException("Lexical error");
                        } else if(ch == '\n' ||ch == '\r') {
                            throw new LexicalException("Illegal Sequence Occurred");
                        }
                        else if (ch == '"' && inputChars[pos - 1] != '\\') {
                            break;
                        }
                        else {
                            nextChar();
                        }
                    }
                    nextChar();
                    int lengthString = pos - tokenStart;
                    return new StringLitToken(IToken.Kind.STRING_LIT, tokenStart, lengthString, inputChars, line, column - lengthString);
                }
                default -> {
                    throw new UnsupportedOperationException("Scanner is not working!");
                }
            }
        }
    }
}
