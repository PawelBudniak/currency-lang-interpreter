package currencies.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import currencies.ExecutionException;
import currencies.executor.Utils;
import currencies.reader.CharPosition;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class CCurrency extends CType<CCurrency> implements Comparable<CCurrency>{
    public static final int CODE_LEN = 3;

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


    @Override
    public CNumber getValue() {
        return value;
    }

    /** False if 0 (any precision) */
    @Override
    public boolean truthValue(){
        return value.truthValue();
    }

    public String getCode() {
        return code;
    }

    public String getTypeStr(){
        return code.toString().toLowerCase();
    }

    public CNumber divide(CCurrency other){
        return value.divide(other.getValue());
    }

    @Override
    public CType add (CType other, CharPosition position){
        if (other instanceof CCurrency){
            Utils.requireSameCurrencyTypes(this,other,"add", position);
            return this.add((CCurrency)other);
        }
        return super.add(other,position);

    }

    public CCurrency add(CCurrency other){
        return new CCurrency(value.add(other.getValue()), code);
    }

    public CCurrency subtract(CCurrency other){
        return new CCurrency(value.subtract(other.getValue()), code);
    }

    public CCurrency multiply(CNumber other){
        return new CCurrency(value.multiply(other), code);
    }

    public CCurrency divide(CNumber other){
        return new CCurrency(value.divide(other), code);
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


    private static Map<String, Map<String, CNumber>> exchangeRates;

    /** Returns true also for differing precision - same value comparisons, e.g. 0.00 [equals] 0.0,
     * This is not the case for the default equals method
      */
    public boolean precisionlessEquals(CCurrency other){
        return this.compareTo(other) == 0;
    }



    public static void setExchangeRate(String codeFrom, String codeTo, CNumber rate){
        exchangeRates.get(codeFrom).put(codeTo,rate);
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

            gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(exchangeRates);
            System.out.println(json);



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Set<String> allCodes (){
        return exchangeRates.keySet();
    }

    @Override
    public int compareTo(CCurrency other) {
        Utils.requireSameCurrencyTypes(this, other,"compare", null);

        return getValue().compareTo(other.getValue());
    }
}
