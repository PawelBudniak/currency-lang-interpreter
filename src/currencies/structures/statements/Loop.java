package currencies.structures.statements;

import currencies.structures.Block;
import currencies.structures.expressions.BoolExpression;

public class Loop implements Statement {
    private BoolExpression condition;
    private Block block;

    public Loop(BoolExpression condition, Block block) {
        this.condition = condition;
        this.block = block;
    }

    @Override
    public String toString() {
        return "while (" +condition + ") " + block;
    }
}
