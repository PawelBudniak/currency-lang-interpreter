package currencies.structures.statements;

import currencies.execution.Scope;

public interface Statement {
    void execute(Scope scope);
}
