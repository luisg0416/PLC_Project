package edu.ufl.cise.plcsp23;

public class StringLitToken extends Token implements IStringLitToken {
    public StringLitToken(Kind kind, int pos, int length, char[] source, int line, int column) {
        super(kind, pos, length, source, line, column);

    }

    @Override
    public String getValue() {
        String res = "";
        String temp = "";
        for (int i = pos+1; i < pos+length-1; i++) {
            temp ="";
            if (source[i] != '\\') {
                res += source[i];
            }
            else {
                switch (source[i + 1]) {
                    case 'b':
                        temp += '\b';
                        break;
                    case 't':
                        temp += '\t';
                        break;
                    case 'n':
                        temp += '\n';
                        break;
                    case 'f':
                        temp += '\f';
                        break;
                    case 'r':
                        temp += '\r';
                        break;
                    case '\'':
                        temp += '\'';
                        break;
                    case '\"':
                        temp += '\"';
                        break;
                    case '\\':
                        temp += '\\';
                        break;
                }
                res+=temp;
                i++;
            }
        }
        return res;
    }
}
