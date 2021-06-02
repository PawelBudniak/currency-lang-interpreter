package currencies.types;

public class CString extends CType<CString> implements Comparable<CString>{

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
}
