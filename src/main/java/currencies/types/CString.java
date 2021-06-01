package currencies.types;

import java.util.Objects;

public class CString implements CType, Comparable<CString>{

    String string;

    public CString(String string) {
        this.string = string;
    }

    @Override
    public boolean truthValue() {
        return !string.isEmpty();
    }

    @Override
    public String getValue() {
        return string;
    }

    @Override
    public int compareTo(CString cString) {
        return string.compareTo(cString.getValue());
    }

    @Override
    public String toString() {
        return "'" + string + "'";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CString cString = (CString) o;
        return string.equals(cString.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(string);
    }
}
