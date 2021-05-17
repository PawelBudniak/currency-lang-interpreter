package currencies.structures.expressions;

import java.util.List;

public class BoolTerm {
    private List<BoolFactor> operands;

    public BoolTerm(List<BoolFactor> operands) {
        this.operands = operands;
    }

    public List<BoolFactor> getOperands() {
        return operands;
    }

    @Override
    public String toString (){
        StringBuilder str = new StringBuilder(operands.get(0).toString());

        for (int i = 1; i < operands.size(); ++i){
            str.append(" && ");
            str.append(operands.get(i));
        }
        return str.toString();
    }
}
