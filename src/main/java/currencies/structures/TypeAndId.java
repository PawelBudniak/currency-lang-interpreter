package currencies.structures;

import currencies.lexer.Token;
import currencies.lexer.TokenType;

import java.util.Objects;

public class TypeAndId {
    Token typeToken;
    String id;

    public TypeAndId(Token typeToken, String id) {
        this.typeToken = typeToken;
        this.id = id;
    }

    public Token getTypeToken() {
        return typeToken;
    }

    public TokenType getType(){
        return typeToken.getType();
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
        return Objects.hash(typeToken, id);
    }

    @Override
    public String toString() {
        return typeToken.valueStr() + " " + id;
    }
}
