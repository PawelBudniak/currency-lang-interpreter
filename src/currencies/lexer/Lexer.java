package currencies.lexer;

import currencies.Currency;
import currencies.reader.CodeInput;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public class Lexer {

    private CodeInput source;
    private char current;
    private long position;

    private static final int MAX_ITER = 100_000;

    public Token getNextToken() throws IOException {
        skipWhitespace();
        skipComments();

        position = getPosition();
        if (isEOT())
            return new Token(TokenType.T_EOT, position);

        Token token;

        token = buildString();
        if (token != null) return token;
//        token = buildInt();
//        if (token != null) return token;
        token = buildNumber();
        if (token != null) return token;

        //TODO: keyword check
        token = buildIdentifier();
        if (token != null) {
            TokenType keyword = keywordMap.get(token.getValue());
            if (keyword != null)
                return new Token(keyword, token.getValue(), position);
            Currency.Type code = currencyCodes.get(token.getValue());
            if (code != null)
                return new Token(TokenType.T_CURRENCY_TYPE, code, position);
            else
                return token;
        }

        
        
        




        TokenType type;




         type = tokenTypeMap.get(current);


         return new Token(TokenType.T_UNKNOWN, position);

    }

    private Token buildIdentifier() throws IOException {
        if (Character.isLetter(current)){
            StringBuilder id = new StringBuilder();
            int i = 0;
            do{
                id.append(current);
                current = nextChar();
            } while( (Character.isDigit(current) || Character.isLetter(current)) && isLegalIter(++i) );

            return new Token(TokenType.T_IDENTIFIER, id.toString(), position);


        }
        return null;
    }


    private Token buildNumber() {
        // TODO: allow 0 na starcie tylko jesli poprzedzone kropka
        if (Character.isDigit(current)){
            StringBuilder literal = new StringBuilder();
            int i = 0;
            do {
                literal.append(current);
                current = nextChar();
            } while( (Character.isDigit(current) || current == '.') && isLegalIter(++i));

            BigDecimal value = new BigDecimal(literal.toString());


            // try building a currency literal (e.g. 0.23gbp)
            if (isValidCurrencyCodeChar(current)){
                StringBuilder currencyCode = new StringBuilder();
                int j = 0;
                do {
                    currencyCode.append(current);
                    current = nextChar();
                    ++j;
                } while (isLegalIter(j) && isValidCurrencyCodeChar(current));

                Currency.Type type = currencyCodes.get(currencyCode.toString());
                // if the currency code is incorrect  then the token is invalid
                if (type == null)
                    invalidToken();
                else{
                    Currency currency = new Currency(value, type);
                    return new Token(TokenType.T_CURRENCY_LITERAL, currency, position);
                }
            }
            else
                return new Token(TokenType.T_NUMBER_LITERAL, value, position);


        }
        return null;
    }

    private void invalidToken(){
        throw new RuntimeException();
    }


    private boolean isValidCurrencyCodeChar(char c){
        return Character.isLetter(c) && Character.isLowerCase(c);
    }

//    private Token buildInt() throws IOException {
//        if (isValidNumberStart(current)){
//            int val = 0;
//            int i = 0;
//            do{
//                val = val * 10 + current - '0';
//                current = nextChar();
//            } while (Character.isDigit())
//        }
//    }

    private boolean isValidIntStart(char c){
        return Character.isDigit(c) && c != '0';
    }

    private boolean isEOT(){
        return !source.hasNext();
    }

    private long getPosition() {
        return source.getPosition();
    }

    private char nextChar() {
        return source.nextChar();
    }


    private void skipComments() {
        if (current == '#'){
            int i = 0;
            do {
                current = nextChar();
            } while (current != '\n' && !isLegalIter(++i));
        }
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(current))
            current = nextChar();
    }

    private Token buildString() {
        if (current == '\''){
            int i = 0;
            StringBuilder literal = new StringBuilder();
            do{
                current = nextChar();
                literal.append(current);
            } while (current != '\'' && isLegalIter(++i));

            return new Token(TokenType.T_STR_LITERAL, literal.toString(), position);
        }
        return null;
    }

    // TODO: throw at EOT?
    private boolean isLegalIter(int nIter){
        return nIter < MAX_ITER && !isEOT();
    }




    private static Map<String, TokenType> tokenTypeMap = Map.ofEntries(
            entry("/", TokenType.T_DIV),
            entry("*",TokenType.T_MULT),
            entry("+", TokenType.T_PLUS),
            entry("-", TokenType.T_MINUS),
            entry("{", TokenType.T_CURLBRACKET_OPEN),
            entry("}", TokenType.T_CURLBRACKET_CLOSE),
            entry(";", TokenType.T_SEMICOLON),
            entry("(", TokenType.T_PAREN_OPEN),
            entry(")", TokenType.T_PAREN_CLOSE)
    );

    private static Map<String, Currency.Type> currencyCodes = Arrays.stream(Currency.Type.values())
            .collect(Collectors.toMap(value -> value.toString().toLowerCase(),
                                      value -> value));

    private static Map<String, TokenType> keywordMap = Arrays.stream(TokenType.values())
            .filter(value -> value.toString().startsWith("T_KW_"))
            .collect(Collectors.toMap(value -> value.toString().substring(TokenType.kwPrefixLen()).toLowerCase(),
                                      value -> value));
//    = Map.ofEntries(
//            entry("gbp", Currency.Type.GBP),
//            entry("eur", Currency.Type.EUR),
//            entry("pln", Currency.Type.PLN),
//            entry("chp", Currency.Type.CHP),
//            entry("usd", Currency.Type.USD)
//    );


}
