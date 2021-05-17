package currencies.lexer;

/**
 * All keyword tokens are named according to the convention:
 * T_KW_<keyword_in_uppercase>
 */
public enum TokenType {

    T_PAREN_OPEN,
    T_PAREN_CLOSE,
    T_LT,
    T_GT,
    T_LTE,
    T_GTE,
    T_EQUALS,
    T_ASSIGNMENT,
    T_NOTEQUALS,
    T_EXCLAMATION,
    T_AND,
    T_OR,
    T_MINUS,
    T_PLUS,
    T_MULT,
    T_DIV,
    T_CURLBRACKET_OPEN,
    T_CURLBRACKET_CLOSE,
    T_SEMICOLON,
    T_COMMA,
    T_SQUAREBRACKET_OPEN,
    T_SQUAREBRACKET_CLOSE,
    T_IDENTIFIER,
    T_NUMBER_LITERAL,
    T_STR_LITERAL,
    T_CURRENCY_CODE,
    T_KW_NUMBER,
    T_KW_BOOl,
    T_KW_CURRENCY,
    T_KW_STRING,
    T_KW_EXCHANGE,
    T_KW_FROM,
    T_KW_TO,
    T_KW_IF,
    T_KW_WHILE,
    T_KW_TRUE,
    T_KW_FALSE,
    T_KW_VOID,
    T_KW_RETURN,
    T_EOT,
    T_UNKNOWN;

    public static int kwPrefixLen(){
        return "T_KW_".length();
    }
}
