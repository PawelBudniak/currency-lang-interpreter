package currencies.lexer;

import currencies.types.CCurrency;
import currencies.reader.CharPosition;
import currencies.reader.CodeInput;
import currencies.types.CNumber;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

//TODO: throw exception zamiast return unkown
public class Lexer {
    private CodeInput source;
    private char current;
    private CharPosition position;
    private Token currentToken;

    private static final int MAX_ITER = 10_000;

    public Lexer(CodeInput source) {
        this.source = source;
        current = nextChar();
    }

    public Token getNextToken(){
        currentToken = nextToken();
        return currentToken;
    }

    /**
     * Returns a token that was returned by the previous call to nextToken().
     * If this is called before any calls to nextToken(), a null value is returned.
     */
    public Token currentToken() {
        return currentToken;
    }

    /**
     * Returns a next token from the source and automatically advances to the next one.
     */

    private Token nextToken() {

        while (current == '#' || Character.isWhitespace((current))) {
            skipWhitespace();
            skipComments();
        }

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

            if (CCurrency.allCodes().contains(token.getValue()))
                return new Token(TokenType.T_CURRENCY_CODE, token.getValue(), position);

            return token;
        }

        token = buildPotentialTwoCharToken();
        if (token != null) return token;

        token = buildOneCharToken();
        if (token != null) return token;

        current = nextChar();
        throw new LexerException("Cannot recognize token", position);

    }

    private Token buildPotentialTwoCharToken(){
        switch (current){
            case '=':
                current = nextChar();
                if (current == '=') {
                    current = nextChar();
                    return new Token(TokenType.T_EQUALS, "==", position);
                }
                else
                    return new Token(TokenType.T_ASSIGNMENT, "=", position);
            case '!':
                current = nextChar();
                if (current == '=') {
                    current = nextChar();
                    return new Token(TokenType.T_NOTEQUALS, "!=", position);
                }
                else
                    return new Token(TokenType.T_EXCLAMATION, "!", position);
            case '<':
                current = nextChar();
                if (current == '='){
                    current = nextChar();
                    return new Token(TokenType.T_LTE, "<=", position);
                }
                else
                    return new Token(TokenType.T_LT, "<", position);
            case '>':
                current = nextChar();
                if (current == '='){
                    current = nextChar();
                    return new Token(TokenType.T_GTE, ">=", position);
                }
                else
                    return new Token(TokenType.T_GT, ">", position);
            case '&':
                current = nextChar();
                if (current == '&'){
                    current = nextChar();
                    return new Token(TokenType.T_AND, "&&", position);
                }
                else
                    return null;
            case '|':
                current = nextChar();
                if (current == '|'){
                    current = nextChar();
                    return new Token(TokenType.T_OR, "||", position);
                }
                else
                    return null;
        }
        return null;
    }

    private Token buildIdentifier() {
        if (Character.isLetter(current) || current == '_'){
            StringBuilder id = new StringBuilder();
            int i = 0;
            do{
                id.append(current);
                current = nextChar();
            } while( (Character.isDigit(current) || Character.isLetter(current) || current == '_')
                    && isLegalIter(++i) );

            return new Token(TokenType.T_IDENTIFIER, id.toString(), position);


        }
        return null;
    }

    private Token buildOneCharToken(){
        TokenType type = tokenTypeMap.get(current);
        if (type != null){
            char ch = current;
            current = nextChar();
            return new Token(type, String.valueOf(ch), position);
        }
        return null;
    }


    private Token buildNumber() {
        if (Character.isDigit(current)){

            StringBuilder literal = new StringBuilder();
            boolean seenDecimalPoint = false;
            int i = 0;

            do {
                if (current == '.') {
                    if (seenDecimalPoint)
                        throw new LexerException("Multiple decimal points in a number", position);
                    seenDecimalPoint = true;
                }

                literal.append(current);
                current = nextChar();
            } while((Character.isDigit(current) || current == '.') && isLegalIter(++i));

            CNumber value = CNumber.fromStr(literal.toString());
            return new Token(TokenType.T_NUMBER_LITERAL, value, position);

        }
        return null;
    }


    private boolean isEOT(){
        return !source.hasNext();
    }

    private CharPosition getPosition() {
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
            } while (current != '\n' && isLegalIter(++i));
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

            // dont use do while here, we want to ignore the quotation marks in the str value
            current = nextChar();
            while (current != '\'' && isLegalIter(++i)) {
                literal.append(current);
                current = nextChar();
            }

            if (current == '\'') {
                current = nextChar();
                return new Token(TokenType.T_STR_LITERAL, literal.toString(), position);
            }
            if (isEOT()){
                throw new LexerException("EOF reached before str closing quotation mark", position);
            }
        }
        return null;
    }

    private boolean isLegalIter(int nIter){
        if (nIter > MAX_ITER)
            throw new LexerException("Token too long. Max token length is" + MAX_ITER, source.getPosition());

        return !isEOT();
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
            entry(')', TokenType.T_PAREN_CLOSE),
            entry(',', TokenType.T_COMMA),
            entry('[', TokenType.T_SQUAREBRACKET_OPEN),
            entry(']', TokenType.T_SQUAREBRACKET_CLOSE)
    );

//    private static Map<String, Currency.Type> currencyCodes = Arrays.stream(Currency.Type.values())
//            .collect(Collectors.toMap(value -> value.toString().toLowerCase(),
//                                      value -> value));


    // get keyword strings from token enum values that start with "T_KW_"
    private static Map<String, TokenType> keywordMap = Arrays.stream(TokenType.values())
            .filter(value -> value.toString().startsWith("T_KW_"))
            .collect(Collectors.toMap(value -> value.toString().substring(TokenType.kwPrefixLen()).toLowerCase(),
                                      value -> value));

}
