package currencies.types;

import java.math.BigDecimal;

public class CNumber extends CType<CNumber> implements Comparable<CNumber>{

    BigDecimal number;

    public CNumber(BigDecimal number) {
        this.number = number;
    }


    public CNumber add(CNumber other){
        return new CNumber(number.add(other.getValue()));
    }

    public CNumber subtract(CNumber other){
        return new CNumber(number.subtract(other.getValue()));
    }

    public CNumber multiply(CNumber other){
        return new CNumber(number.multiply(other.getValue()));
    }
    public CNumber divide(CNumber other){
        return new CNumber(number.divide(other.getValue()));
    }

    public CCurrency divide(CCurrency other){
        return new CCurrency(this.divide(other.getValue()), other.getCode());
    }


    public static CNumber fromStr(String strvalue){
        return new CNumber(new BigDecimal(strvalue));
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CNumber cNumber = (CNumber) o;
        return number.equals(cNumber.number);
    }

}
