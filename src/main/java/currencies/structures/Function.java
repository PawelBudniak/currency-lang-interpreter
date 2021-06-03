package currencies.structures;

import currencies.ExecutionException;
import currencies.executor.Scope;
import currencies.lexer.Token;
import currencies.lexer.TokenType;
import currencies.types.CType;

import java.util.List;

//TODO: odroznic fun def od fun body do wykonania
public class Function  {

    Token returnType;
    String name;
    List<TypeAndId> argDefList;
    Block block;
    CType returnedValue;

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

    public CType getReturnedValue() {
        return returnedValue;
    }

    public void define(Scope scope){
        //TODO: overloading?

        if (scope.getFunction(name) != null)
            throw new ExecutionException("Function with name " + name + " is already defined", null);

        scope.newFunction(this);
    }

    public void call(Scope scope){
        block.execute(scope);
        if (returnType.getType() != TokenType.T_KW_VOID)
            returnedValue = CType.assign(block.getReturnedValue(), CType.typeOf(returnType.valueStr()));
        else{
            if (block.getReturnedValue() != null){
                throw new ExecutionException("Attempting to return a value from void function", null);
            }
        }
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
