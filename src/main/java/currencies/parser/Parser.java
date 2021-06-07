package currencies.parser;

import com.sun.source.tree.IdentifierTree;
import currencies.reader.CharPosition;
import currencies.structures.simple_values.Identifier;
import currencies.types.CBoolean;
import currencies.types.CCurrency;
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
import currencies.types.CNumber;
import currencies.types.CString;

import java.util.*;
import java.util.stream.Stream;


public class Parser {

    private Lexer lexer;
    private Token currentToken;

    private static final TokenType[] typeSpecifiers = {T_KW_NUMBER, T_KW_BOOl, T_KW_STRING, T_KW_CURRENCY};
    private static final Set<TokenType> comparisonOperators = Set.of(T_GT, T_GTE, T_LTE, T_LT, T_EQUALS, T_NOTEQUALS);

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        nextToken();
    }

    public Program parseProgram(){
        List<Function> functions = new ArrayList<>();
        Function function;

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



    public Function tryParseFunction(){

        if (currentToken.getType() == T_EOT)
            return null;

        require(typeSpecifiers, T_KW_VOID);
        Token returnType = currentToken;
        nextToken();

        Identifier id = requireIdentifier();

        requireThenNextToken(T_PAREN_OPEN);

        List<TypeAndId> args = parseArgDefList();

        require(T_PAREN_CLOSE);
        nextToken();

        Block block = parseBlock();
        block.setFunctionBody(true);

        return new Function(returnType, id, args, block);
    }

    public Block parseBlock(){
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

    public  List<TypeAndId> parseArgDefList() {
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

    public  TypeAndId tryParseTypeAndId(){
        if (!arrContains(typeSpecifiers, currentToken.getType()))
            return null;
        else{
            Token typeSpec = currentToken;
            nextToken();
            Identifier id = requireIdentifier();
            return new TypeAndId(typeSpec, id);
        }
    }

    public ExchangeDeclaration tryParseExchangeDeclaration(){
        if (currentToken.getType() != T_KW_EXCHANGE)
            return null;



        nextTokenThenRequire(T_KW_FROM);
        nextTokenThenRequire(T_CURRENCY_CODE);
        String fromCurrency = (String) currentToken.getValue();

        nextTokenThenRequire(T_KW_TO);
        nextTokenThenRequire(T_CURRENCY_CODE);
        String toCurrency = (String) currentToken.getValue();
        nextToken();

        CharPosition position = currentToken.getPosition();
        RValue value = tryParseRValue();
        if (value == null)
            throw new SyntaxException("Value not found for exchange declaration statement", currentToken.getPosition());

        requireThenNextToken(T_SEMICOLON);


        return new ExchangeDeclaration(fromCurrency, toCurrency, value, position);
    }

    public RValue tryParseRValue() {
        return tryParseBoolExpression();
    }

    public RValue tryParseArithmeticExpression() {

        List<RValue> operands = new ArrayList<>();
        List<Token> operators = new ArrayList<>();
        RValue firstTerm;

        if ((firstTerm = tryParseArithmeticTerm()) == null)
            return null;

        operands.add(firstTerm);


        while ( currentToken.getType() == T_PLUS ||
                currentToken.getType() == T_MINUS){
            RValue term;
            Token operator = currentToken;
            nextToken();
            if ((term = tryParseArithmeticTerm()) == null)
                throw new SyntaxException("No second operand found after " + operator.valueStr() + "operator", currentToken.getPosition());

            operands.add(term);
            operators.add(operator);
        }

        if (operands.size() == 1)
            return firstTerm;

        return new ArithmeticExpression(operands, operators);

    }

    public  RValue tryParseArithmeticTerm() {

        List<RValue> operands = new ArrayList<>();
        List<Token> operators = new ArrayList<>();
        RValue firstFactor;

        if ((firstFactor = tryParseArithmeticFactor()) == null)
            return null;

        operands.add(firstFactor);

        while ( currentToken.getType() == T_MULT ||
                currentToken.getType() == T_DIV){

            RValue factor;
            Token operator = currentToken;
            nextToken();
            if ((factor = tryParseArithmeticFactor()) == null)
                throw new SyntaxException("No second operand found after " + operator.valueStr() + "operator", currentToken.getPosition());

            operands.add(factor);
            operators.add(operator);
        }

        if (operands.size() == 1)
            return firstFactor;

        return new ArithmeticTerm(operands, operators);
    }

    public  RValue tryParseArithmeticFactor() {
        Token unaryOp = tryParseUnaryOp();

        if (currentToken.getType() == T_PAREN_OPEN){
            nextToken();
            //ArithmeticExpression expression = tryParseArithmeticExpression();
            RValue expression = tryParseBoolExpression();
            if (expression == null)
                throw new SyntaxException("No expression found in parentheses", currentToken.getPosition());

            requireThenNextToken(T_PAREN_CLOSE);
            return Factor.factorOrRValue(unaryOp, expression);
        }

        RValue simpleValue = tryParseSimpleValue();
        if (simpleValue == null)
            return null;
        return Factor.factorOrRValue(unaryOp, simpleValue);

    }




    public ReturnStatement tryParseReturnStatement() {
        if (currentToken.getType() != T_KW_RETURN)
            return null;

        nextToken();
        CharPosition position = currentToken.getPosition();
        RValue val = tryParseRValue();
        if (val == null)
            throw new SyntaxException("Expected an rvalue after return keyword", currentToken.getPosition());

        requireThenNextToken(T_SEMICOLON);
        return new ReturnStatement(val, position);

    }

    public IfStatement tryParseIfStatement(){
        if(currentToken.getType() != T_KW_IF)
            return null;

        nextTokenThenRequire(T_PAREN_OPEN);

        nextToken();
        RValue condition = tryParseBoolExpression();
        if (condition == null)
            throw new SyntaxException("No condition found for if statement", currentToken.getPosition());

        requireThenNextToken(T_PAREN_CLOSE);

        Block block = parseBlock();

        return new IfStatement(condition, block);
    }

    public RValue tryParseBoolExpression() {

        List<RValue> operands = new ArrayList<>();
        RValue firstTerm;

        if ((firstTerm = tryParseBoolTerm()) == null)
            return null;

        operands.add(firstTerm);


        while ( currentToken.getType() == T_OR){
            RValue term;
            nextToken();
            if ((term = tryParseBoolTerm()) == null)
                throw new SyntaxException("No second operand found after OR token", currentToken.getPosition());
            operands.add(term);
        }

        if (operands.size() == 1)
            return firstTerm;

        return new BoolExpression(operands);
    }


    public  RValue tryParseBoolTerm() {
        List<RValue> operands = new ArrayList<>();
        RValue firstFactor;

        if ((firstFactor = tryParseBoolFactor()) == null)
            return null;

        operands.add(firstFactor);


        while ( currentToken.getType() == T_AND){
            RValue factor;
            nextToken();
            if ((factor = tryParseBoolFactor()) == null)
                throw new SyntaxException("No second operand found after AND token", currentToken.getPosition());
            operands.add(factor);
        }

        if (operands.size() == 1)
            return firstFactor;

        return new BoolTerm(operands);

    }

    public RValue tryParseBoolFactor() {

        Token unaryOp = tryParseUnaryOp();

        //RValue simpleValue = tryParseRValue();
        RValue simpleValue = tryParseArithmeticExpression();
        if (simpleValue == null)
            return null;

        if (currentToken.getType() == T_PAREN_OPEN){
            assert false;
            nextToken();
            RValue expression = tryParseBoolExpression();
            if (expression == null)
                throw new SyntaxException("No expression found in parentheses", currentToken.getPosition());
            requireThenNextToken(T_PAREN_CLOSE);
            return  Factor.factorOrRValue(unaryOp, expression);
        }


        if (comparisonOperators.contains(currentToken.getType())){

            Token operator = currentToken;
            nextToken();
            //RValue rightOperand = tryParseRValue();
            RValue rightOperand = tryParseArithmeticExpression();

            if (rightOperand == null)
                throw new SyntaxException("No second operand found after comparison operator", currentToken.getPosition());

            return  Factor.factorOrRValue(unaryOp, new Comparison(simpleValue, operator, rightOperand));
        }

        return  Factor.factorOrRValue(unaryOp, simpleValue);


    }

    public  RValue tryParseSimpleValue() {

        Identifier id = tryParseIdentifier();
        if (id != null){

            FunctionCall funcall;
            if ((funcall = tryParseRestOfFunctionCall(id)) != null)
                return funcall;

            return new Variable(id);
        }

        Literal literal;
        if ((literal = tryParseLiteral()) != null)
            return literal;
        return null;
    }

    public Literal tryParseLiteral() {

        if (currentToken.getType() == T_STR_LITERAL) {
            String strLiteral = (String) currentToken.getValue();
            nextToken();
            return new Literal(new CString(strLiteral));
        } else if (currentToken.getType() == T_KW_TRUE || currentToken.getType() == T_KW_FALSE) {
            Boolean value = Boolean.valueOf((String) currentToken.getValue());
            nextToken();
            return new Literal(new CBoolean(value));
        } else if (currentToken.getType() == T_NUMBER_LITERAL) {
            CNumber value = (CNumber) currentToken.getValue();
            nextToken();
            if (currentToken.getType() == T_CURRENCY_CODE) {
                CCurrency currencyLiteral = new CCurrency(value, (String) currentToken.getValue());
                nextToken();
                return new Literal(currencyLiteral);
            }
            return new Literal(value);
        }
        return null;
    }

    // assumes the function id has already been parsed
    public  FunctionCall tryParseRestOfFunctionCall(Identifier id) {
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

        return new FunctionCall(id, args);

    }


    public  Token tryParseUnaryOp(){
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

    public Loop tryParseLoop(){
        if(currentToken.getType() != T_KW_WHILE)
            return null;

        nextTokenThenRequire(T_PAREN_OPEN);

        nextToken();
        RValue condition = tryParseBoolExpression();
        if (condition == null)
            throw new SyntaxException("No condition found for while statement", currentToken.getPosition());

        requireThenNextToken(T_PAREN_CLOSE);

        Block block = parseBlock();

        return new Loop(condition, block);
    }

    public Statement tryParseAssignOrFunCall() {

        // check for var declaration and initialization (e.g. int x = 3;)
        TypeAndId typeAndId = tryParseTypeAndId();
        if (typeAndId != null){
            Assignment assignment = tryParseRestOfAssign(typeAndId.getTypeToken(), typeAndId.getId());

            if (assignment == null)
                throw new SyntaxException("Assignment expected after type specifier", currentToken.getPosition());
            requireThenNextToken(T_SEMICOLON);
            return assignment;
        }


        // check for assignment without declaration or a funcall
        Identifier id = tryParseIdentifier();
        if (id != null){
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

    private Identifier tryParseIdentifier(){
        if (currentToken.getType() != T_IDENTIFIER)
            return null;
        return getIdFromToken();
    }

    private Identifier requireIdentifier(){
        require(T_IDENTIFIER);
        return getIdFromToken();
    }

    private Identifier getIdFromToken(){
        String id = (String) currentToken.getValue();
        CharPosition position = currentToken.getPosition();
        nextToken();
        return new Identifier(id, position);
    }

    public Assignment tryParseRestOfAssign(Token varType, Identifier varId){
        if (currentToken.getType() != T_ASSIGNMENT)
            return null;
        nextToken();
        RValue value = tryParseRValue();

        if (value == null)
            throw new SyntaxException("No valid rvalue found after assignment operator", currentToken.getPosition());

        return new Assignment(varType, varId, value);
    }

    public Assignment tryParseRestOfAssign(Identifier varName){
        return tryParseRestOfAssign(null, varName);
    }



}
