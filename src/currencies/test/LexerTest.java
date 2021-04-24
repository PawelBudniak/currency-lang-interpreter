package currencies.test;

import currencies.lexer.Lexer;
import currencies.lexer.Token;
import currencies.lexer.TokenType;
import currencies.reader.CodeInputStream;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Assignment{
        Lexer lexer;
        Token t;

        @BeforeAll
        public void setUp(){
            String test = "myvar = 42;";
            InputStream stream = new ByteArrayInputStream(test.getBytes(StandardCharsets.UTF_8));
            lexer = new Lexer(new CodeInputStream(stream));
        }

        @BeforeEach
        void getToken(){
            t = lexer.getNextToken();
        }
        @Test
        @Order(1)
        void id(){
            assertEquals(t.getType(), TokenType.T_IDENTIFIER);
            assertEquals(t.getValue(), "myvar");
        }
        @Test
        @Order(2)
        void assign(){
            assertEquals(t.getType(), TokenType.T_ASSIGNMENT);
        }
        @Test
        @Order(3)
        void number(){
            assertEquals(t.getType(),  TokenType.T_NUMBER_LITERAL);
            assertEquals(t.getValue(), new BigDecimal("42"));
        }
        @Test
        @Order(4)
        void semicolon(){
            assertEquals(t.getType(), TokenType.T_SEMICOLON);
        }
        @Test
        @Order(5)
        void EOT(){
            assertEquals(t.getType(),TokenType.T_EOT);
        }

    }

    @Test
    void testAssignment() {

        String test = "myvar = 42;";
        InputStream stream = new ByteArrayInputStream(test.getBytes(StandardCharsets.UTF_8));

        Lexer lexer = new Lexer(new CodeInputStream(stream));

        Token t = lexer.getNextToken();
        assertEquals(t.getType(), TokenType.T_IDENTIFIER);
        assertEquals(t.getValue(), "myvar");
        assertEquals(lexer.getNextToken().getType(), TokenType.T_ASSIGNMENT);

        t = lexer.getNextToken();
        assertEquals(t.getType(),  TokenType.T_NUMBER_LITERAL);
        assertEquals(t.getValue(), new BigDecimal("42"));

        assertEquals(lexer.getNextToken().getType(), TokenType.T_SEMICOLON);
        assertEquals(lexer.getNextToken().getType(), TokenType.T_EOT);

    }
}