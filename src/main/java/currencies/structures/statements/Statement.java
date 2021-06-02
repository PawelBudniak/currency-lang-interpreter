package currencies.structures.statements;

import currencies.executor.Scope;

public interface Statement {

    default void execute(Scope scope) { }
}
