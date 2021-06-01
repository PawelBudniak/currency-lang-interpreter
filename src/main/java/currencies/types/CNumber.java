package currencies.types;

import java.math.BigDecimal;
import java.util.Objects;

public class CNumber implements CType, Comparable<CNumber>{

    BigDecimal number;

    public CNumber(BigDecimal number) {
        this.number = number;
    }

    /** False if 0 (any precision) */
    @Override
    public boolean truthValue() {
        return number.compareTo(BigDecimal.ZERO) != 0;
    }

    @Override
    public BigDecimal getValue() {
        return number;
    }

    @Override
    public int compareTo(CNumber cNumber) {
        return number.compareTo(cNumber.getValue());
    }

    @Override
    public String toString() {
        return number.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CNumber cNumber = (CNumber) o;
        return number.equals(cNumber.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
