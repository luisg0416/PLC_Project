package edu.ufl.cise.plcsp23;

import java.lang.reflect.Array;

public class NumLitToken extends Token implements INumLitToken {
    public NumLitToken(Kind kind, int pos, int length, char[] source, int line, int column) {
        super(kind, pos, length, source, line, column);
    }

    @Override
    public int getValue() {
        String temp = "";
        for (int i = 0; i < length; i++) {
            temp += source[pos + i];
        }
        return Integer.parseInt(temp);
    }
}
