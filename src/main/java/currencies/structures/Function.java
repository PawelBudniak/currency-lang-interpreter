package currencies.structures;

import currencies.execution.ExecutionException;
import currencies.error.InterpreterException;
import currencies.execution.Scope;
import currencies.lexer.Token;
import currencies.lexer.TokenType;
import currencies.structures.simple_values.Identifier;
import currencies.types.CType;

import java.util.List;

public class Function  {

    Token returnType;
    Identifier id;
    List<TypeAndId> argDefList;
    Block block;
    CType returnedValue;

    public Function(Token returnType, Identifier id, List<TypeAndId> argDefList, Block block) {
        this.returnType = returnType;
        this.id = id;
        this.argDefList = argDefList;
        this.block = block;
    }

    public TokenType getReturnType() {
        return returnType.getType();
    }

    public String getId() {
        return id.getName();
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

        if (scope.getFunction(getId()) != null)
            throw new ExecutionException("Function with name " + id + " is already defined", id.getPosition());

        scope.newFunction(this);
    }

    public void call(Scope scope){
        block.execute(scope);
        if (returnType.getType() != TokenType.T_KW_VOID)
            try {
                returnedValue = CType.assign(block.getReturnedValue(), CType.typeOf(returnType.getType()));
            }catch (ExecutionException e){
                InterpreterException.setPositionAndRethrow(e, block.getReturnStatementPosition());
            }
        else{
            if (block.getReturnedValue() != null){
                throw new ExecutionException("Attempting to return a value from void function", block.getReturnStatementPosition());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(returnType.valueStr() + " " + id + "(");

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
