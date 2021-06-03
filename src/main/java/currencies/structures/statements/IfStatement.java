package currencies.structures.statements;

import currencies.executor.Scope;
import currencies.structures.Block;
import currencies.structures.expressions.BoolExpression;
import currencies.structures.expressions.RValue;

public class IfStatement implements Statement {
    RValue condition;
    Block block;

    public IfStatement(RValue condition, Block block) {
        this.condition = condition;
        this.block = block;
    }

    @Override
    public void execute(Scope scope){
        if (condition.truthValue(scope)){
            block.execute(scope);
        }
    }

    public RValue getCondition() {
        return condition;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public String toString() {
      return "if (" +condition + ") " + block;
    }
}
