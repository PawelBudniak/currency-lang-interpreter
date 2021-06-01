package currencies.types;

import java.util.Objects;

public class CBoolean implements CType, Comparable<CBoolean> {

    private Boolean bool;

    public CBoolean(Boolean bool) {
        this.bool = bool;
    }

    @Override
    public Boolean getValue() {
        return bool;
    }

    @Override
    public boolean truthValue() {
        return bool;
    }

    @Override
    public int compareTo(CBoolean cBoolean) {
        return bool.compareTo(cBoolean.getValue());
    }

    @Override
    public String toString() {
        return bool.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CBoolean cBoolean = (CBoolean) o;
        return bool.equals(cBoolean.bool);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bool);
    }
}
