package currencies.structures;

import currencies.executor.Scope;
import currencies.structures.statements.ReturnStatement;
import currencies.structures.statements.Statement;
import currencies.types.CType;

import java.util.List;

public class Block {
    private List<Statement> statements;
    private CType returnedValue;
    private boolean isFunctionBody = false;

    public Block(List<Statement> statements) {
        this.statements = statements;
    }

    public void setFunctionBody(boolean functionBody) {
        isFunctionBody = functionBody;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void execute(Scope scope){

        //TODO: shadowowanie zmiennych przy assignmentach here

        try {
            for (Statement statement : statements) {
                statement.execute(scope);
                if (statement instanceof ReturnStatement) {
                    throw new ReturnStatementException(((ReturnStatement) statement).getResult());
                }
            }
        }

        // keep rethrowing to find the block that is a function's body
        catch (ReturnStatementException ex){
            if (isFunctionBody)
                returnedValue = ex.getReturnedValue();
            else
                throw ex;
        }
    }

    public CType getReturnedValue() {
        return returnedValue;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("{\n");
        for (Statement statement : statements) {
            str.append(statement);
            str.append("\n");
        }
        str.append("}");
        return str.toString();
    }
}
