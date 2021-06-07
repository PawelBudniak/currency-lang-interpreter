package currencies.structures;

import currencies.execution.ReturnStatementException;
import currencies.execution.Scope;
import currencies.reader.CharPosition;
import currencies.structures.statements.Statement;
import currencies.types.CType;

import java.util.List;

public class Block {
    private List<Statement> statements;
    private CType returnedValue;
    private boolean isFunctionBody = false;
    private CharPosition returnStatementPosition;

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

        scope.enterNewLocalScope();

        for (Statement statement : statements) {
            try {
                statement.execute(scope);
            }

            // ReturnStatementException is used to easily find the main block of the function (with isFunctionBody set to true)
            catch (ReturnStatementException ex){
                if (isFunctionBody) {
                    returnedValue = ex.getReturnedValue();
                    returnStatementPosition = ex.getPosition();
                    break;
                }

                else {
                    // keep rethrowing to find the block that is a function's body, while unrolling the scope "stack"
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

    public CharPosition getReturnStatementPosition() {
        return returnStatementPosition;
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
