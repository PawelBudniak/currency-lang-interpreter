package currencies.structures.statements;

import currencies.structures.Block;
import currencies.structures.expressions.BoolExpression;

public class IfStatement implements Statement {
    BoolExpression condition;
    Block block;

    public IfStatement(BoolExpression condition, Block block) {
        this.condition = condition;
        this.block = block;
    }

    public BoolExpression getCondition() {
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
