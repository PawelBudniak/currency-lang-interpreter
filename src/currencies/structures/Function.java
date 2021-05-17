package currencies.structures;

import currencies.lexer.Token;
import currencies.lexer.TokenType;

import java.util.List;

public class Function  {

    Token returnType;
    String name;
    List<TypeAndId> argDefList;
    Block block;

    public Function(Token returnType, String name, List<TypeAndId> argDefList, Block block) {
        this.returnType = returnType;
        this.name = name;
        this.argDefList = argDefList;
        this.block = block;
    }

    public TokenType getReturnType() {
        return returnType.getType();
    }

    public String getName() {
        return name;
    }

    public List<TypeAndId> getArgDefList() {
        return argDefList;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(returnType.valueStr() + " " +  name + "(");

        for (int i = 0; i < argDefList.size(); ++i){
            str.append(argDefList.get(i));
            if (i != argDefList.size() - 1)
                str.append(", ");
        }
        str.append(") ");
        str.append(block);
        return str.toString();

    }
}
