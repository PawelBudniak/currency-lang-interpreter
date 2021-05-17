package currencies.parser;

import currencies.Currency;
import currencies.lexer.Lexer;
import currencies.lexer.Token;
import currencies.lexer.TokenType;
import static currencies.lexer.TokenType.*;
import currencies.structures.*;
import currencies.structures.expressions.*;
import currencies.structures.simple_values.FunctionCall;
import currencies.structures.simple_values.Literal;
import currencies.structures.simple_values.Variable;
import currencies.structures.statements.*;

import java.util.*;
import java.util.stream.Stream;


public class Parser {

    private Lexer lexer;
    private Token currentToken;

    private static final TokenType[] typeSpecifiers = {T_KW_NUMBER, T_KW_BOOl, T_KW_STRING, T_KW_CURRENCY};
    private static final Set<TokenType> comparisonOperators = Set.of(T_GT, T_GTE, T_LTE, T_LT, T_EQUALS, T_NOTEQUALS);

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Program parseProgram(){
        List<Function> functions = new ArrayList<>();
        Function function;

        nextToken();
        while ( (function = tryParseFunction()) != null)
            functions.add(function);



        return new Program(functions);

    }


     void nextToken(){
        currentToken = lexer.getNextToken();
    }

    private void require(TokenType... types){

        if (!arrContains(types, currentToken.getType())){
            StringBuilder msg = new StringBuilder("Expected one of: ");
            Arrays.stream(types).
                    map(Objects::toString).
                    forEach((keyword) -> msg.append(" " + keyword));
            msg.append(".\nInstead got: " + currentToken);
            throw new SyntaxException(msg.toString(), currentToken.getPosition());
        }

    }

    private void require(TokenType[] arr, TokenType... args){
        TokenType[] allArgs = Stream.concat(Arrays.stream(arr), Arrays.stream(args))
                .toArray(TokenType[]::new);
        require(allArgs);
    }

    private void nextTokenThenRequire(TokenType... types){
        nextToken();
        require(types);
    }

    private void requireThenNextToken(TokenType... types){
        require(types);
        nextToken();
    }



    Function tryParseFunction(){

        if (currentToken.getType() == T_EOT)
            return null;

        require(typeSpecifiers, T_KW_VOID);
        Token returnType = currentToken;

        nextTokenThenRequire(T_IDENTIFIER);
        String name = (String)currentToken.getValue();

        nextTokenThenRequire(T_PAREN_OPEN);
        nextToken();

        List<TypeAndId> args = parseArgDefList();

        require(T_PAREN_CLOSE);
        nextToken();

        Block block = parseBlock();

        return new Function(returnType, name, args, block);
    }

    Block parseBlock(){
        List<Statement> statements = new ArrayList<>();

        require(T_CURLBRACKET_OPEN);
        nextToken();

        Statement statement;
        while ( (statement = tryParseAssignOrFunCall()) != null ||
                (statement = tryParseExchangeDeclaration()) != null ||
                (statement = tryParseIfStatement()) != null ||
                (statement = tryParseLoop()) != null  ||
                (statement = tryParseReturnStatement()) != null )
        {
            statements.add(statement);
        }


        require(T_CURLBRACKET_CLOSE);
        nextToken();
        return new Block(statements);
    }

    private boolean arrContains(Object[] arr, Object item){
        return Arrays.asList(arr).contains(item);
    }

    private List<TypeAndId> parseArgDefList() {
        List<TypeAndId> args = new ArrayList<>();
        TypeAndId arg;

        if (( arg = tryParseTypeAndId()) != null){
            args.add(arg);
            while(currentToken.getType() == T_COMMA){
                nextToken();
                arg = tryParseTypeAndId();
                if (arg == null)
                    throw new SyntaxException("Expected a function argument after comma", currentToken.getPosition());
                args.add(arg);
            }
        }
        return args;
    }

    private TypeAndId tryParseTypeAndId(){
        if (!arrContains(typeSpecifiers, currentToken.getType()))
            return null;
        else{
            Token typeSpec = currentToken;
            nextTokenThenRequire(T_IDENTIFIER);
            String id = (String)currentToken.getValue();
            nextToken();
            return new TypeAndId(typeSpec, id);
        }
    }

    ExchangeDeclaration tryParseExchangeDeclaration(){
        if (currentToken.getType() != T_KW_EXCHANGE)
            return null;

        nextTokenThenRequire(T_KW_FROM);
        nextTokenThenRequire(T_CURRENCY_CODE);
        Currency.Type fromCurrency = (Currency.Type)currentToken.getValue();

        nextTokenThenRequire(T_KW_TO);
        nextTokenThenRequire(T_CURRENCY_CODE);
        Currency.Type toCurrency = (Currency.Type)currentToken.getValue();
        nextToken();

        RValue value = tryParseRValue();
        if (value == null)
            throw new SyntaxException("Value not found for exchange declaration statement", currentToken.getPosition());

        requireThenNextToken(T_SEMICOLON);


        return new ExchangeDeclaration(fromCurrency, toCurrency, value);
    }

    RValue tryParseRValue() {

        return tryParseArithmeticExpression();
    }

    ArithmeticExpression tryParseArithmeticExpression() {



        List<ArithmeticTerm> operands = new ArrayList<>();
        List<Token> operators = new ArrayList<>();
        ArithmeticTerm term;

        if ((term = tryParseArithmeticTerm()) == null)
            return null;

        operands.add(term);


        while ( currentToken.getType() == T_PLUS ||
                currentToken.getType() == T_MINUS){
            Token operator = currentToken;
            nextToken();
            if ((term = tryParseArithmeticTerm()) == null)
                throw new SyntaxException("No second operand found after additive operator", currentToken.getPosition());

            operands.add(term);
            operators.add(operator);
        }


        return new ArithmeticExpression(operands, operators);

    }

    private ArithmeticTerm tryParseArithmeticTerm() {

        List<ArithmeticFactor> operands = new ArrayList<>();
        List<Token> operators = new ArrayList<>();
        ArithmeticFactor factor;

        if ((factor = tryParseArithmeticFactor()) == null)
            return null;

        operands.add(factor);

        while ( currentToken.getType() == T_MULT ||
                currentToken.getType() == T_DIV){
            Token operator = currentToken;
            nextToken();
            if ((factor = tryParseArithmeticFactor()) == null)
                throw new SyntaxException("No second operand found after AND token", currentToken.getPosition());

            operands.add(factor);
            operators.add(operator);
        }


        return new ArithmeticTerm(operands, operators);
    }

    private ArithmeticFactor tryParseArithmeticFactor() {
        Token unaryOp = tryParseUnaryOp();

        if (currentToken.getType() == T_PAREN_OPEN){
            nextToken();
            ArithmeticExpression expression = tryParseArithmeticExpression();
            if (expression == null)
                throw new SyntaxException("No expression found in parentheses", currentToken.getPosition());
            requireThenNextToken(T_PAREN_CLOSE);
            return new ArithmeticFactor(unaryOp, expression);
        }

        RValue simpleValue = tryParseSimpleValue();
        if (simpleValue == null)
            return null;
        return new ArithmeticFactor(unaryOp, simpleValue);

    }




    ReturnStatement tryParseReturnStatement() {
        if (currentToken.getType() != T_KW_RETURN)
            return null;

        nextToken();
        RValue val = tryParseRValue();
        if (val == null)
            throw new SyntaxException("Expected an rvalue after return keyword", currentToken.getPosition());

        requireThenNextToken(T_SEMICOLON);
        return new ReturnStatement(val);

    }

    IfStatement tryParseIfStatement(){
        if(currentToken.getType() != T_KW_IF)
            return null;

        nextTokenThenRequire(T_PAREN_OPEN);

        nextToken();
        BoolExpression condition = tryParseBoolExpression();
        if (condition == null)
            throw new SyntaxException("No condition found for if statement", currentToken.getPosition());

        requireThenNextToken(T_PAREN_CLOSE);

        Block block = parseBlock();

        return new IfStatement(condition, block);
    }

    // TODO: na razie zakładam, że moze wystapic tylko w ifie i while, tzn nie da się uzywac jako rvalue
    BoolExpression tryParseBoolExpression() {

        List<BoolTerm> operands = new ArrayList<>();
        BoolTerm term;

        if ((term = tryParseBoolTerm()) == null)
            return null;

        operands.add(term);


        while ( currentToken.getType() == T_OR){
            nextToken();
            if ((term = tryParseBoolTerm()) == null)
                throw new SyntaxException("No second operand found after OR token", currentToken.getPosition());
            operands.add(term);
        }


        return new BoolExpression(operands);
    }

    private BoolTerm tryParseBoolTerm() {
        List<BoolFactor> operands = new ArrayList<>();
        BoolFactor factor;

        if ((factor = tryParseBoolFactor()) == null)
            return null;

        operands.add(factor);


        while ( currentToken.getType() == T_AND){
            nextToken();
            if ((factor = tryParseBoolFactor()) == null)
                throw new SyntaxException("No second operand found after AND token", currentToken.getPosition());
            operands.add(factor);
        }


        return new BoolTerm(operands);

    }

    private BoolFactor tryParseBoolFactor() {

        Token unaryOp = tryParseUnaryOp();


        if (currentToken.getType() == T_PAREN_OPEN){
            nextToken();
            BoolExpression expression = tryParseBoolExpression();
            if (expression == null)
                throw new SyntaxException("No expression found in parentheses", currentToken.getPosition());
            requireThenNextToken(T_PAREN_CLOSE);
            return new BoolFactor(unaryOp, expression);
        }

        // TODO: should be arithmetic expression?
        RValue simpleValue = tryParseRValue();
        if (simpleValue == null)
            return null;


        if (comparisonOperators.contains(currentToken.getType())){

            Token operator = currentToken;
            nextToken();
            //TODO: tu tez
            RValue rightOperand = tryParseRValue();
            if (rightOperand == null)
                throw new SyntaxException("No second operand found after comparison operator", currentToken.getPosition());

            return new BoolFactor(unaryOp, new Comparison(simpleValue, operator, rightOperand));
        }

        return new BoolFactor(unaryOp, simpleValue);


    }

    private RValue tryParseSimpleValue() {

        if (currentToken.getType() == T_IDENTIFIER){
            String name = (String) currentToken.getValue();
            nextToken();

            FunctionCall funcall;
            if ((funcall = tryParseRestOfFunctionCall(name)) != null)
                return funcall;

            return new Variable(name);
        }

        Literal literal;
        if ((literal = tryParseLiteral()) != null)
            return literal;
        return null;
    }

    Literal tryParseLiteral() {

        if (currentToken.getType() == T_STR_LITERAL) {
            String strLiteral = (String) currentToken.getValue();
            nextToken();
            return new Literal(strLiteral, T_KW_STRING);
        } else if (currentToken.getType() == T_KW_TRUE || currentToken.getType() == T_KW_FALSE) {
            Boolean value = Boolean.valueOf((String) currentToken.getValue());
            nextToken();
            return new Literal(value, T_KW_BOOl);
        } else if (currentToken.getType() == T_NUMBER_LITERAL) {
            Number value = (Number) currentToken.getValue();
            nextToken();
            if (currentToken.getType() == T_CURRENCY_CODE) {
                Currency currencyLiteral = new Currency(value, (Currency.Type) currentToken.getValue());
                nextToken();
                return new Literal(currencyLiteral, T_KW_CURRENCY);
            }
            return new Literal(value, T_KW_NUMBER);
        }
        return null;
    }

    // assumes the function id has already been parsed
    private FunctionCall tryParseRestOfFunctionCall(String name) {
        if (currentToken.getType() != T_PAREN_OPEN)
            return null;
        nextToken();

        List<RValue> args = new ArrayList<>();
        RValue arg = tryParseRValue();
        if (arg != null){
            args.add(arg);
            while (currentToken.getType() == T_COMMA){
                nextToken();
                if ((arg = tryParseRValue()) == null)
                    throw new SyntaxException("No argument found after comma in function call", currentToken.getPosition());
                args.add(arg);
            }

        }

        requireThenNextToken(T_PAREN_CLOSE);

        return new FunctionCall(name, args);

    }


    private Token tryParseUnaryOp(){
        if (currentToken.getType() == T_MINUS ||
                currentToken.getType() == T_EXCLAMATION){
            Token op = currentToken;
            nextToken();
            return op;

        }
        if (currentToken.getType() == T_SQUAREBRACKET_OPEN){
            nextTokenThenRequire(T_CURRENCY_CODE);
            Token currency = currentToken;
            nextTokenThenRequire(T_SQUAREBRACKET_CLOSE);
            nextToken();
            return currency;
        }
        return null;
    }

    Statement tryParseLoop(){
        if(currentToken.getType() != T_KW_WHILE)
            return null;

        nextTokenThenRequire(T_PAREN_OPEN);

        nextToken();
        BoolExpression condition = tryParseBoolExpression();
        if (condition == null)
            throw new SyntaxException("No condition found for while statement", currentToken.getPosition());

        requireThenNextToken(T_PAREN_CLOSE);

        Block block = parseBlock();

        return new Loop(condition, block);
    }

    Statement tryParseAssignOrFunCall() {

        // expect var declaration and initialization (e.g. int x = 3;)
        if (arrContains(typeSpecifiers, currentToken.getType())){
            Token varType = currentToken;
            nextTokenThenRequire(T_IDENTIFIER);
            String varName = (String) currentToken.getValue();
            nextToken();

            Assignment assignment = tryParseRestOfAssign(varType, varName);
            if (assignment == null)
                throw new SyntaxException("Assignment expected after type specifier", currentToken.getPosition());
            requireThenNextToken(T_SEMICOLON);
            return assignment;
        }

        if (currentToken.getType() == T_IDENTIFIER){
            String id = (String) currentToken.getValue();
            nextToken();
            Statement statement;
            if ((statement = tryParseRestOfAssign(id)) != null) {
                requireThenNextToken(T_SEMICOLON);
                return statement;
            }
            if ((statement = tryParseRestOfFunctionCall(id)) != null) {
                requireThenNextToken(T_SEMICOLON);
                ((FunctionCall)statement).setAsStatement(true);
                return statement;
            }

            throw new SyntaxException("Expected either assignment or function call after finding an identifier", currentToken.getPosition());

        }
        return null;

    }

    Assignment tryParseRestOfAssign(Token varType, String varName){
        if (currentToken.getType() != T_ASSIGNMENT)
            return null;
        nextToken();
        RValue value = tryParseRValue();

        if (value == null)
            throw new SyntaxException("No valid rvalue found after assignment operator", currentToken.getPosition());

        return new Assignment(varType, new Variable(varName), value);
    }

    Assignment tryParseRestOfAssign(String varName){
        return tryParseRestOfAssign(null, varName);
    }



}
