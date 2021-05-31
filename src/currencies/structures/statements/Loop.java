package currencies.structures.statements;

import currencies.structures.Block;
import currencies.structures.expressions.BoolExpression;
import currencies.structures.expressions.RValue;

public class Loop implements Statement {
    private RValue condition;
    private Block block;

    public Loop(RValue condition, Block block) {
        this.condition = condition;
        this.block = block;
    }

    public RValue getCondition() {
        return condition;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public String toString() {
        return "while (" +condition + ") " + block;
    }
}
