import currencies.types.CCurrency;
import currencies.lexer.Lexer;
import currencies.lexer.Token;
import currencies.lexer.TokenType;
import currencies.reader.CodeInputStream;
import currencies.types.CNumber;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    @BeforeAll
    static void loadCurrencies(){
        Util.loadCurrencies();
    }

    Lexer lexerFromStringStream(String s){
        InputStream stream = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        return new Lexer(new CodeInputStream(stream));
    }

    @Test
    void ignoresComments(){
        Lexer l = lexerFromStringStream("#hello 32\nvar");

        assertAll(
                "Comments should end after a newline and be ignored by lexer",
                ()-> assertEquals(TokenType.T_IDENTIFIER, l.getNextToken().getType()),
                ()->assertEquals("var", l.currentToken().getValue())
        );
    }

    @Test
    void currentTokenBeforeFirstNextToken(){
        Lexer l = lexerFromStringStream("aa");

        assertNull(l.currentToken(),"A call to current token before any nextToken() calls should return null");
    }

    @Test
    void ignoresWhitespace(){
        Lexer l = lexerFromStringStream("   \t\n\r  32");

        assertAll(
                ()-> assertEquals(TokenType.T_NUMBER_LITERAL, l.getNextToken().getType()),
                ()->assertEquals(CNumber.fromStr("32"), l.currentToken().getValue())
        );
    }

    @Test
    void emptyStream(){
        Lexer l = lexerFromStringStream("");

        assertEquals(TokenType.T_EOT, l.getNextToken().getType(),
                "Empty input should include only an EOT token");
    }

    @Test
    void stringLiteral(){
        Lexer l = lexerFromStringStream("'stringer'");

        assertAll(
                ()-> assertEquals(TokenType.T_STR_LITERAL, l.getNextToken().getType()),
                ()->assertEquals("stringer", l.currentToken().getValue())
        );
    }

    @Test
    void emptyStringLiteral(){
        Lexer l = lexerFromStringStream("''");

        assertAll(
                ()-> assertEquals(TokenType.T_STR_LITERAL, l.getNextToken().getType()),
                ()->assertEquals("", l.currentToken().getValue())
        );
    }

    @Test
    void integerNumber(){
        Lexer l = lexerFromStringStream("69");

        assertAll(
                ()-> assertEquals(TokenType.T_NUMBER_LITERAL, l.getNextToken().getType()),
                ()->assertEquals(CNumber.fromStr("69"), l.currentToken().getValue())
        );
    }

    @Test
    void decimalNumber(){
        Lexer l = lexerFromStringStream("69.69");

        assertAll(
                ()-> assertEquals(TokenType.T_NUMBER_LITERAL, l.getNextToken().getType()),
                ()->assertEquals(CNumber.fromStr("69.69"), l.currentToken().getValue())
        );
    }

    @Test
    void doubleDecimalNumberIsIncorrect() {
        Lexer l = lexerFromStringStream("69.69.69");

        assertThrows(currencies.lexer.LexerException.class, () -> l.getNextToken());
    }

    @Test
    void stringQuotesMustBeClosed(){
        Lexer l = lexerFromStringStream("'incomplete string 43 a = 5");
        assertThrows(currencies.lexer.LexerException.class, () -> l.getNextToken());
    }

    @Test
    void simpleId(){
        Lexer l = lexerFromStringStream("foo");

        assertAll(
                ()-> assertEquals(TokenType.T_IDENTIFIER, l.getNextToken().getType()),
                ()->assertEquals("foo", l.currentToken().getValue())
        );
    }

    @Test
    void idWithUnderscore(){
        Lexer l = lexerFromStringStream("foo_bar");

        assertAll(
                "Ids can contain underscores",
                ()-> assertEquals(TokenType.T_IDENTIFIER, l.getNextToken().getType()),
                ()-> assertEquals("foo_bar", l.currentToken().getValue())
        );
    }

    @Test
    void idWithNumber(){
        Lexer l = lexerFromStringStream("foo69");

        assertAll(
                "Ids can contain numbers after the first character",
                ()-> assertEquals(TokenType.T_IDENTIFIER, l.getNextToken().getType()),
                ()-> assertEquals("foo69", l.currentToken().getValue())
        );
    }

    @Test
    void numberThenId(){
        Lexer l = lexerFromStringStream("69foo");

        assertAll(
                "Ids can not start with a number",
                ()-> assertEquals(TokenType.T_NUMBER_LITERAL, l.getNextToken().getType()),
                ()-> assertEquals(CNumber.fromStr("69"), l.currentToken().getValue()),
                ()-> assertEquals(TokenType.T_IDENTIFIER, l.getNextToken().getType()),
                ()-> assertEquals("foo", l.currentToken().getValue())
        );
    }

    @Test
    void whileKeyword(){
        Lexer l = lexerFromStringStream("while");

        assertEquals(TokenType.T_KW_WHILE, l.getNextToken().getType());
    }

    @Test
    void currencyCode(){
        Lexer l = lexerFromStringStream("pln");

        assertAll(
                "Currency codes are recognized",
                ()-> assertEquals(TokenType.T_CURRENCY_CODE, l.getNextToken().getType()),
                ()-> assertEquals("pln", l.currentToken().getValue())
        );
    }

    @Test
    void boolAND(){
        Lexer l = lexerFromStringStream("&&");
        assertEquals(TokenType.T_AND, l.getNextToken().getType());
    }

    @Test
    void boolOR(){
        Lexer l = lexerFromStringStream("||");
        assertEquals(TokenType.T_OR, l.getNextToken().getType());
    }

    @Test
    void assignment(){
        Lexer l = lexerFromStringStream("=a");
        assertEquals(TokenType.T_ASSIGNMENT, l.getNextToken().getType());
    }

    @Test
    void bracket(){
        Lexer l = lexerFromStringStream(")");
        assertEquals(TokenType.T_PAREN_CLOSE, l.getNextToken().getType());
    }


    @Nested
    class ComparisonOperators{
        @Test
        void lessThan(){
            Lexer l = lexerFromStringStream("<");
            assertEquals(TokenType.T_LT, l.getNextToken().getType());
        }
        @Test
        void lessThanEqual(){
            Lexer l = lexerFromStringStream("<=");
            assertEquals(TokenType.T_LTE, l.getNextToken().getType());
        }
        @Test
        void greaterThanEqual(){
            Lexer l = lexerFromStringStream(">=");
            assertEquals(TokenType.T_GTE, l.getNextToken().getType());
        }
        @Test
        void greaterThan(){
            Lexer l = lexerFromStringStream(">");
            assertEquals(TokenType.T_GT, l.getNextToken().getType());
        }
        @Test
        void equals(){
            Lexer l = lexerFromStringStream("==");
            assertEquals(TokenType.T_EQUALS, l.getNextToken().getType());
        }
        @Test
        void notEquals(){
            Lexer l = lexerFromStringStream("!=");
            assertEquals(TokenType.T_NOTEQUALS, l.getNextToken().getType());
        }
    }




    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    private class Assignment{
        Lexer lexer;
        Token t;

        @BeforeAll
        public void setUp(){
            lexer = lexerFromStringStream("myvar = 42;");
        }

        @BeforeEach
        void getToken(){
            t = lexer.getNextToken();
        }

        @Test @Order(1)
        void id(){
            assertAll(
                    ()-> assertEquals(TokenType.T_IDENTIFIER, t.getType()),
                    ()-> assertEquals("myvar", t.getValue())
            );
        }
        @Test @Order(2)
        void assign(){
            assertEquals(TokenType.T_ASSIGNMENT, t.getType());
        }
        @Test @Order(3)
        void number(){
            assertAll(
                    () -> assertEquals(TokenType.T_NUMBER_LITERAL, t.getType()),
                    ()-> assertEquals(CNumber.fromStr("42"), t.getValue())
            );
        }
        @Test @Order(4)
        void semicolon(){
            assertEquals(TokenType.T_SEMICOLON, t.getType());
        }
        @Test @Order(5)
        void EOT(){
            assertEquals(TokenType.T_EOT, t.getType());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    private class ExchangeRate{
        Lexer lexer;
        Token t;

        @BeforeAll
        public void setUp(){
            lexer = lexerFromStringStream("exchange from gbp to pln 4.21;");
        }

        @BeforeEach
        void getToken(){
            t = lexer.getNextToken();
        }


        @Test @Order(1)
        void exchange(){
           assertEquals(TokenType.T_KW_EXCHANGE, t.getType());
        }
        @Test @Order(2)
        void from(){
            assertEquals(TokenType.T_KW_FROM, t.getType());
        }
        @Test @Order(3)
        void currency_gbp(){
            assertAll(
                    () -> assertEquals(TokenType.T_CURRENCY_CODE, t.getType()),
                    () -> assertEquals("gbp", t.getValue())
            );
        }
        @Test @Order(4)
        void to(){
            assertEquals(TokenType.T_KW_TO, t.getType());
        }
        @Test @Order(5)
        void currency_pln(){
            assertAll(
                    () -> assertEquals(TokenType.T_CURRENCY_CODE, t.getType()),
                    () -> assertEquals("pln", t.getValue())
            );
        }
        @Test @Order(6)
        void number(){
            assertAll(
                    () -> assertEquals(TokenType.T_NUMBER_LITERAL, t.getType()),
                    () -> assertEquals(CNumber.fromStr("4.21"), t.getValue())
            );
        }
        @Test @Order(7)
        void semicolon(){
            assertEquals(TokenType.T_SEMICOLON, t.getType());
        }
        @Test @Order(8)
        void EOT(){
            assertEquals(TokenType.T_EOT, t.getType());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    private class AdditionAndMultiplication{
        Lexer lexer;
        Token t;

        @BeforeAll
        public void setUp(){
            lexer = lexerFromStringStream("43.2+ 90 * 21.3");
        }

        @BeforeEach
        void getToken(){
            t = lexer.getNextToken();
        }

        @Test @Order(1)
        void number_1(){
            assertAll(
                    () -> assertEquals(TokenType.T_NUMBER_LITERAL, t.getType()),
                    () -> assertEquals(CNumber.fromStr("43.2"), t.getValue())
            );
        }
        @Test @Order(2)
        void plus(){
            assertEquals(TokenType.T_PLUS, t.getType());
        }
        @Test @Order(3)
        void number_2(){
            assertAll(
                    () -> assertEquals(TokenType.T_NUMBER_LITERAL, t.getType()),
                    () -> assertEquals(CNumber.fromStr("90"), t.getValue())
            );
        }
        @Test @Order(4)
        void multiplication(){
            assertEquals(TokenType.T_MULT, t.getType());
        }
        @Test @Order(5)
        void number_3(){
            assertAll(
                    () -> assertEquals(TokenType.T_NUMBER_LITERAL, t.getType()),
                    () -> assertEquals(CNumber.fromStr("21.3"), t.getValue())
            );
        }
        @Test @Order(6)
        void EOT(){
            assertEquals(TokenType.T_EOT, t.getType());
        }
    }

//    @Test
//    void testAssignment() {
//
//        String test = "myvar = 42;";
//        InputStream stream = new ByteArrayInputStream(test.getBytes(StandardCharsets.UTF_8));
//
//        Lexer l = new Lexer(new CodeInputStream(stream));
//
//
//        assertAll(
//                ()-> assertEquals(TokenType.T_IDENTIFIER, l.getNextToken().getType()),
//                ()-> assertEquals("myvar", l.currentToken().getValue()),
//                ()-> assertEquals(TokenType.T_ASSIGNMENT, l.getNextToken().getType()),
//                ()-> assertEquals(TokenType.T_NUMBER_LITERAL, l.getNextToken().getType()),
//                ()-> assertEquals(NumberFactory.of("42"), l.currentToken().getValue()),
//                ()-> assertEquals( TokenType.T_SEMICOLON, l.getNextToken().getType()),
//                ()-> assertEquals(TokenType.T_EOT, l.getNextToken().getType())
//        );
//
//    }
}