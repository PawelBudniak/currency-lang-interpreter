package currencies.types;

import java.util.Objects;

public abstract class CType <T extends CType<T>> implements Comparable<T>{

    public abstract boolean truthValue();
    public abstract Object getValue();

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }


}
