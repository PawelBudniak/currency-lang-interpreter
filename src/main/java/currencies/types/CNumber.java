package currencies.types;

import currencies.execution.ExecutionException;
import currencies.reader.CharPosition;

import java.math.BigDecimal;

public class CNumber extends CType<CNumber>{

    private BigDecimal number;

    public CNumber(BigDecimal number) {
        this.number = number;
    }




    public CNumber add(CNumber other){
        return new CNumber(number.add(other.getValue()));
    }

    public CNumber subtract(CNumber other){ return new CNumber(number.subtract(other.getValue())); }

    public CNumber multiply(CNumber other){
        return new CNumber(number.multiply(other.getValue()));
    }

    public CCurrency multiply(CCurrency other) { return other.multiply(this); }

    public CNumber divide(CNumber other){
        if (other.compareTo(CNumber.fromStr("0")) == 0){
            throw new ExecutionException("Division by zero", null);
        }
        return new CNumber(number.divide(other.getValue()));
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

    public CCurrency currencyCast(String targetCode){
        return new CCurrency(this, targetCode);
    }

    @Override
    public CNumber negate(){
        return new CNumber(number.negate());
    }



    @Override
    public CType acceptAdd(CType other){
        return other.visitAdd(this);
    }

    @Override
    protected CType visitAdd(CNumber other){
        return other.add(this);
    }

    @Override
    public CType acceptDivide(CType other){
        return other.visitDivide(this);
    }

    @Override
    protected CType visitDivide(CNumber other){
        return other.divide(this);
    }

    @Override
    protected CType visitDivide(CCurrency other){
        return other.divide(this);
    }

    @Override
    public CType acceptSubtract(CType other){
        return other.visitSubtract(this);
    }

    @Override
    protected CType visitSubtract(CNumber other){
        return other.subtract(this);
    }

    @Override
    public CType acceptMultiply(CType other) { return other.visitMultiply(this); }

    @Override
    protected CType visitMultiply(CNumber other) { return other.multiply(this); }

    @Override
    protected CType visitMultiply(CCurrency other) { return other.multiply(this); }

}
