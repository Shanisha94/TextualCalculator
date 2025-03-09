import static org.junit.jupiter.api.Assertions.*;

import com.calculator.models.operators.IncrementOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import com.calculator.models.AssignmentOperator;
import com.calculator.models.Expression;
import com.calculator.models.operators.IUnaryOperator;
import com.calculator.utils.ExpressionParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Method;
import java.util.*;

public class ExpressionParserTest {

    private Map<String, Class<? extends IUnaryOperator>> unaryOperatorsMock;

    @BeforeEach
    void setUp() {
        unaryOperatorsMock = new HashMap<>();
    }

    @Test
    public void testParseValidExpression() {
        String input = "x = 5 + 3";
        Expression expression = ExpressionParser.parse(input);

        assertNotNull(expression);
        assertEquals("x", expression.assignedVariable());
        assertEquals(AssignmentOperator.ASSIGN, expression.assignmentOperator());
        assertEquals(List.of("5", "+", "3"), expression.expressionParts());
    }

    @Test
    public void testParseWithExtraSpaces() {
        String input = "   y   +=   10   -   2  ";
        Expression expression = ExpressionParser.parse(input);

        assertNotNull(expression);
        assertEquals("y", expression.assignedVariable());
        assertEquals(AssignmentOperator.ADD_ASSIGN, expression.assignmentOperator());
        assertEquals(List.of("10", "-", "2"), expression.expressionParts());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_expression", "xx", "x & x", "123", "x // i + 1"})
    public void testParseInvalidExpression(String input) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ExpressionParser.parse(input);
        });

        assertTrue(exception.getMessage().contains("Invalid expression"));
    }

    @Test
    public void testTokenizeExpression() {
        String expression = "3 + (4 * 2)";
        Method method = null;
        try {
            method = ExpressionParser.class.getDeclaredMethod("tokenizeExpression", String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        method.setAccessible(true); // Allow access to private method

        List<String> tokens = null;
        try {
            tokens = (List<String>) method.invoke(null, expression);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertEquals(List.of("3", "+", "(", "4", "*", "2", ")"), tokens);
    }

    @Test
    public void testTokenizeExpressionWithMultipleParentheses() {
        String expression = "(5 + 2) * (10 - 3)";
        Method method = null;
        try {
            method = ExpressionParser.class.getDeclaredMethod("tokenizeExpression", String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        method.setAccessible(true); // Allow access to private method

        List<String> tokens = null;
        try {
            tokens = (List<String>) method.invoke(null, expression);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertEquals(List.of("(", "5", "+", "2", ")", "*", "(", "10", "-", "3", ")"), tokens);
    }

    @Test
    public void testIsNumericWithNumbers() {
        assertTrue(ExpressionParser.isNumeric("123"));
        assertTrue(ExpressionParser.isNumeric("-5"));
    }

    @Test
    public void testIsNumericWithNonNumbers() {
        assertFalse(ExpressionParser.isNumeric("abc"));
        assertFalse(ExpressionParser.isNumeric("5a"));
        assertFalse(ExpressionParser.isNumeric(" "));
    }

    @Test
    public void testParseUnaryOperatorPreIncrement() throws Exception {
        unaryOperatorsMock.put("++", IncrementOperator.class);

        Optional<IUnaryOperator> result = ExpressionParser.parseUnaryOperator("++x", unaryOperatorsMock);
        assertTrue(result.isPresent());
        assertEquals("x", result.get().getVariable());
        assertFalse(result.get().isPostOperation()); // Pre-increment
    }

    @Test
    public void testParseUnaryOperatorPostIncrement() throws Exception {
        unaryOperatorsMock.put("++", IncrementOperator.class);

        Optional<IUnaryOperator> result = ExpressionParser.parseUnaryOperator("x++", unaryOperatorsMock);
        assertTrue(result.isPresent());
        assertEquals("x", result.get().getVariable());
        assertTrue(result.get().isPostOperation()); // Post-increment
    }

    @ParameterizedTest
    @ValueSource(strings = {"x", "x+", "+x+"})
    public void testParseUnaryOperatorInvalid(String expression) throws Exception {
        unaryOperatorsMock.put("++", IncrementOperator.class);

        Optional<IUnaryOperator> result = ExpressionParser.parseUnaryOperator(expression, unaryOperatorsMock);
        assertFalse(result.isPresent()); // No unary operator found
    }
}
