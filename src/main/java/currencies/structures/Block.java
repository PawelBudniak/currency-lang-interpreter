package currencies.structures;

import currencies.executor.ReturnStatementException;
import currencies.executor.Scope;
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
     //TODO: argumenty funcalla nie traktowac jako outer


        scope.enterNewLocalScope();

        for (Statement statement : statements) {
            try {
                statement.execute(scope);
            }

            // keep rethrowing to find the block that is a function's body
            catch (ReturnStatementException ex){
                if (isFunctionBody) {
                    returnedValue = ex.getReturnedValue();
                    break;
                }
                else {
                    scope.leaveLocalScope();
                    throw ex;
                }
            }

        }
        scope.leaveLocalScope();


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
