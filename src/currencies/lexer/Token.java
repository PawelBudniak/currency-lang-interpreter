package currencies.lexer;

import currencies.reader.CharPosition;

public class Token {
    private TokenType type;
    private Object value;
    private CharPosition position;


    public TokenType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public CharPosition getPosition() {
        return position;
    }


    public Token(TokenType type, Object value, CharPosition position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    public Token(TokenType type, CharPosition position) {
        this.type = type;
        this.position = position;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value=" + (value == null ? "null" : value) +
                ", position=" + position +
                '}';
    }

    public String valueStr(){
        return value.toString();
    }
}

