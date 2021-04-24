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

    private static final int MAX_ITER = 10_000;

    public Token getNextToken() throws IOException {
        skipWhitespace();
        skipComments();

        position = getPosition();
        if (isEOT())
            return new Token(TokenType.T_EOT, position);

        Token token;

        token = buildString();
        if (token != null) return token;
        token = buildNumber();
        if (token != null) return token;

        token = buildIdentifier();
        if (token != null) {
            // check if the potential identifier is not a keyword or a valid currency code

            TokenType keyword = keywordMap.get(token.getValue());
            if (keyword != null)
                return new Token(keyword, token.getValue(), position);

            Currency.Type code = currencyCodes.get(token.getValue());
            if (code != null)
                return new Token(TokenType.T_CURRENCY_TYPE, code, position);

            return token;
        }

       token = buildPotentialTwoCharToken();
        if (token != null) return token;

        token = buildOneCharToken();
        if (token != null) return token;

        current = nextChar();
        return new Token(TokenType.T_UNKNOWN, position);

    }

    private Token buildPotentialTwoCharToken(){
        switch (current){
            case '=':
                current = nextChar();
                if (current == '=') {
                    current = nextChar();
                    return new Token(TokenType.T_EQUALS, position);
                }
                else
                    return new Token(TokenType.T_ASSIGNMENT, position);
            case '!':
                current = nextChar();
                if (current == '=') {
                    current = nextChar();
                    return new Token(TokenType.T_NOTEQUALS, position);
                }
                else
                    return new Token(TokenType.T_EXCLAMATION, position);
            case '<':
                current = nextChar();
                if (current == '='){
                    current = nextChar();
                    return new Token(TokenType.T_LTE, position);
                }
                else
                    return new Token(TokenType.T_LT, position);
            case '>':
                current = nextChar();
                if (current == '='){
                    current = nextChar();
                    return new Token(TokenType.T_GTE, position);
                }
                else
                    return new Token(TokenType.T_GT, position);
            case '&':
                current = nextChar();
                if (current == '&'){
                    current = nextChar();
                    return new Token(TokenType.T_AND, position);
                }
                else
                    return null;
            case '|':
                current = nextChar();
                if (current == '|'){
                    current = nextChar();
                    return new Token(TokenType.T_OR, position);
                }
                else
                    return null;
        }
        return null;
    }

    private Token buildIdentifier() {
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

    private Token buildOneCharToken(){
        TokenType type = tokenTypeMap.get(current);
        if (type != null){
            current = nextChar();
            return new Token(type, position);
        }
        return null;
    }


    private Token buildNumber() {
        // TODO: allow 0 na starcie tylko jesli poprzedzone kropka, tylko 1 kropka mozliwa
        if (Character.isDigit(current)){
            StringBuilder literal = new StringBuilder();
            int i = 0;
            do {
                literal.append(current);
                current = nextChar();
            } while( (Character.isDigit(current) || current == '.') && isLegalIter(++i));

            BigDecimal value = new BigDecimal(literal.toString());
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




    private static Map<Character, TokenType> tokenTypeMap = Map.ofEntries(
            entry('/', TokenType.T_DIV),
            entry('*',TokenType.T_MULT),
            entry('+', TokenType.T_PLUS),
            entry('-', TokenType.T_MINUS),
            entry('{', TokenType.T_CURLBRACKET_OPEN),
            entry('}', TokenType.T_CURLBRACKET_CLOSE),
            entry(';', TokenType.T_SEMICOLON),
            entry('(', TokenType.T_PAREN_OPEN),
            entry(')', TokenType.T_PAREN_CLOSE)
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
