package currencies.structures;

import currencies.structures.statements.Statement;

import java.util.List;

public class Block {
    private List<Statement> statements;

    public Block(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
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
