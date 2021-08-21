package currencies.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import currencies.execution.ExecutionException;
import currencies.execution.Utils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

public class CCurrency extends CType<CCurrency> {

    private static Map<String, Map<String, CNumber>> exchangeRates;


    private CNumber value;
    private String code;


    public CCurrency(String strvalue, String code) {
        this.value = CNumber.fromStr(strvalue);
        this.code = code;
    }

    public CCurrency(CNumber value, String code) {
        this.value = value;
        this.code = code;
    }


    /** False if 0 (any precision) */
    @Override
    public boolean truthValue(){
        return value.truthValue();
    }


    public CCurrency subtract(CCurrency other){
        Utils.requireSameCurrencyTypes(other, this, "subtract", null);
        return new CCurrency(value.subtract(other.getValue()), code);
    }

    public CCurrency add(CCurrency other){
        Utils.requireSameCurrencyTypes(this,other,"add", null);
        return new CCurrency(value.add(other.getValue()), code);
    }

    public CCurrency multiply(CNumber other){
        return new CCurrency(value.multiply(other), code);
    }

    public CCurrency divide(CNumber other){
        return new CCurrency(value.divide(other), code);
    }

    public CNumber divide(CCurrency other)
    {
        Utils.requireSameCurrencyTypes(this, other, "divide", null);
        return value.divide(other.getValue());
    }

    @Override
    public CCurrency negate(){
        return new CCurrency(value.negate(), code);
    }

    public boolean codesEqual(CCurrency other){
        return code.equals(other.getCode());
    }

    @Override
    public CCurrency currencyCast(String targetCode){

        if (targetCode.equals(code))
            return this;

        CNumber exchangeRate = exchangeRates.get(code).get(targetCode);
        if (exchangeRate == null)
            throw new ExecutionException("Exchange rate hasn't been defined from currency" + code + " to " + targetCode, null);

        return new CCurrency(value.multiply(exchangeRate), targetCode);
    }


    public static void setExchangeRate(String codeFrom, String codeTo, CNumber rate){
        exchangeRates.get(codeFrom).put(codeTo,rate);
    }

    public static Set<String> allCodes (){
        return exchangeRates.keySet();
    }

    @Override
    public int compareTo(CCurrency other) {
        Utils.requireSameCurrencyTypes(this, other,"compare", null);

        return getValue().compareTo(other.getValue());
    }

    public static void loadExchangeRates(String file){


        exchangeRates = new HashMap<>();
        Gson gson = new Gson();

        try (Reader reader = new FileReader(file)) {

            Map <String, Map<String, String>> input = new HashMap<>();

            // Convert JSON File to Java Object
            input = gson.fromJson(reader, input.getClass());

            // transform exchange rate values into CNumbers
            for (String currencyFrom: input.keySet()){

                Map<String, CNumber> nestedRates = input.get(currencyFrom).entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                e -> e.getKey(),
                                e -> CNumber.fromStr(e.getValue())
                        ));

                exchangeRates.put(currencyFrom, nestedRates);

            }
            // Stream only version
//            exchangeRates = input.entrySet()
//                    .stream()
//                    .collect(Collectors.toMap(
//                            e -> e.getKey(),
//                            e -> e.getValue().entrySet().stream()
//                                .collect(Collectors.toMap(
//                                        nestedEntry -> nestedEntry.getKey(),
//                                        nestedEntry -> NumberFactory.get(nestedEntry.getValue())
//                                ))
//                    ));

        } catch (IOException e) {
            throw new RuntimeException("Problem with exchange rates file\n"+ e.getMessage(), e);
        }

    }

    public String getTypeStr(){
        return code.toString().toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CCurrency currency = (CCurrency) o;
        return Objects.equals(value, currency.value) &&
                Objects.equals(code, currency.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, code);
    }

    @Override
    public String toString() {
        return value.toString() + code;
    }


    public String getCode() {
        return code;
    }

    @Override
    public CNumber getValue() {
        return value;
    }

    @Override
    public CType acceptMultiply(CType other){
        return other.visitMultiply(this);
    }

    @Override
    protected CType visitMultiply(CNumber other){
        return other.multiply(this);
    }

    @Override
    public CType acceptAdd(CType other){
        return other.visitAdd(this);
    }

    @Override
    protected CType visitAdd (CCurrency other){ return other.add(this); }

    @Override
    public CType acceptDivide(CType other){
        return other.visitDivide(this);
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
    protected CType visitSubtract(CCurrency other){
        return other.subtract(this);
    }
}
