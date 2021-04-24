package currencies.lexer;

public class Token {
    private TokenType type;
    private Object value;

    public TokenType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public long getPosition() {
        return position;
    }

    private long position;

    public Token(TokenType type, Object value, long position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    public Token(TokenType type, long position) {
        this.type = type;
        this.position = position;
    }
}

