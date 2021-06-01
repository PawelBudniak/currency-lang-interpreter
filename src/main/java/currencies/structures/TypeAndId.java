package currencies.structures;

import currencies.lexer.Token;
import currencies.lexer.TokenType;

import java.util.Objects;

public class TypeAndId {
    Token type;
    String id;

    public TypeAndId(Token type, String id) {
        this.type = type;
        this.id = id;
    }

    public TokenType getType() {
        return type.getType();
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeAndId typeAndId = (TypeAndId) o;
        return getType() == typeAndId.getType() &&
                Objects.equals(id, typeAndId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }

    @Override
    public String toString() {
        return type.valueStr() + " " + id;
    }
}
