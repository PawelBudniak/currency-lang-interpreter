package currencies.parser;

import currencies.lexer.Lexer;
import currencies.lexer.Token;
import currencies.structures.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private Lexer lexer;
    private Token currentToken;



    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    private void nextToken(){
        currentToken = lexer.getNextToken();
    }

    public Program parseProgram(){
        List<Function> functions = new ArrayList<>();
        Function function;

        while ( (function = parseFunction()) != null)
            functions.add(function);

        return new Program(functions)

    }

    private Function parseFunction(){
        return new Function();
    }

    private ExchangeDeclaration parseExchangeDeclaration(){
        return new ExchangeDeclaration();
    }

    private Block parseBlock(){
        return new Block();
    }

}
