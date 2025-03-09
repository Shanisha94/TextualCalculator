import com.calculator.exceptions.InvalidInputException;
import com.calculator.models.AssignmentOperator;
import com.calculator.models.Expression;
import com.calculator.services.ExpressionCalculatorService;
import com.calculator.services.VariablesManagerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;


class ExpressionCalculatorServiceTest {
    private ExpressionCalculatorService calculatorService;
    private BlockingQueue<Expression> inputQueue;
    private VariablesManagerService variablesManagerMock;

    @BeforeEach
    void setUp() {
        inputQueue = new LinkedBlockingQueue<>();
        calculatorService = new ExpressionCalculatorService(inputQueue);
        calculatorService.start();
    }

    @AfterEach
    public void tearDown() {
        calculatorService.stop();
    }

    private static Stream<Object[]> provideExpressions() {
        return Stream.of(
                new Object[]{new Expression("x", AssignmentOperator.ASSIGN, List.of("5", "+", "3")), 8},
                new Object[]{new Expression("y", AssignmentOperator.ASSIGN, List.of("10", "-", "2")), 8},
                new Object[]{new Expression("z", AssignmentOperator.ASSIGN, List.of("4", "*", "2")), 8},
                new Object[]{new Expression("w", AssignmentOperator.ASSIGN, List.of("16", "/", "4")), 4},
                new Object[]{new Expression("param", AssignmentOperator.ASSIGN, List.of("5", "+", "3", "*", "10")), 35},
                new Object[]{new Expression("param", AssignmentOperator.ASSIGN, List.of("(", "5", "+", "3", ")", "*", "10")), 80},
                new Object[]{new Expression("param", AssignmentOperator.ASSIGN, List.of("5", "%", "3", "^", "10")), 5}
                );
    }
    @ParameterizedTest
    @MethodSource("provideExpressions")
    public void testEvaluateExpression(Expression expression, int expectedResult) throws Exception {
        inputQueue.add(expression);
        Thread.sleep(100);
        assertEquals(expectedResult, calculatorService.getVariablesManagerService().getVariable(expression.assignedVariable()));
    }

    @Test
    public void testEvaluateExpressionWithInvalidInput() throws InterruptedException {
        Expression expression = new Expression("x", AssignmentOperator.ASSIGN, List.of("5", "/", "i"));
        inputQueue.add(expression);
        Thread.sleep(100);
        assertFalse(calculatorService.getVariablesManagerService().getVariables().containsKey(expression.assignedVariable()));
        assertFalse(calculatorService.getVariablesManagerService().getVariables().containsKey("i"));
    }

    static Stream<Expression> invalidExpressionList() {
        return Stream.of(
                new Expression("x", AssignmentOperator.ASSIGN, List.of("5", "/", "i")),
                new Expression("i", AssignmentOperator.ADD_ASSIGN, List.of("1")),
                new Expression("i", AssignmentOperator.SUBTRACT_ASSIGN, List.of("2")),
                new Expression("i", AssignmentOperator.DIVIDE_ASSIGN, List.of("2")),
                new Expression("i", AssignmentOperator.MULTIPLY_ASSIGN, List.of("1"))
        );
    }
    @ParameterizedTest
    @MethodSource("invalidExpressionList")
    public void testExpressionWithInvalidVariable(Expression expression) throws InterruptedException {
        inputQueue.add(expression);
        Thread.sleep(100);
        assertFalse(calculatorService.getVariablesManagerService().getVariables().containsKey(expression.assignedVariable()));
    }

    static Stream<Expression> expressionList() {
            return Stream.of(
                    new Expression("x", AssignmentOperator.ASSIGN, List.of("(", "1")),
                    new Expression("x", AssignmentOperator.ASSIGN, List.of(")", "1", "(")),
                    new Expression("x", AssignmentOperator.ASSIGN, List.of(")", "(", "(")),
                    new Expression("x", AssignmentOperator.ASSIGN, List.of("(", "1", "(")),
                    new Expression("x", AssignmentOperator.ASSIGN, List.of("(", "(", "(", "3", ")", ")"))
                    );
        }
    @ParameterizedTest
    @MethodSource("expressionList")
    public void testExpressionWithMismatchedParentheses(Expression expression) throws InterruptedException {
        inputQueue.add(expression);
        Thread.sleep(100);
        assertFalse(calculatorService.getVariablesManagerService().getVariables().containsKey(expression.assignedVariable()));
    }

    @Test
    public void testUnaryOperator() throws InvalidInputException, InterruptedException {
        inputQueue.add(new Expression("i", AssignmentOperator.ASSIGN, List.of("1")));
        inputQueue.add(new Expression("x", AssignmentOperator.ASSIGN, List.of("i++")));
        inputQueue.add(new Expression("y", AssignmentOperator.ASSIGN, List.of("++i")));
        inputQueue.add(new Expression("z", AssignmentOperator.ASSIGN, List.of("2", "+", "++i")));
        inputQueue.add(new Expression("w", AssignmentOperator.ASSIGN, List.of("2", "+", "i++")));
        Thread.sleep(100);
        assertEquals(5, calculatorService.getVariablesManagerService().getVariable("i"));
        assertEquals(1, calculatorService.getVariablesManagerService().getVariable("x"));
        assertEquals(3, calculatorService.getVariablesManagerService().getVariable("y"));
        assertEquals(6, calculatorService.getVariablesManagerService().getVariable("z"));
        assertEquals(6, calculatorService.getVariablesManagerService().getVariable("w"));
    }

    @Test
    public void testAssignments() throws InvalidInputException, InterruptedException {
        inputQueue.add(new Expression("y", AssignmentOperator.ASSIGN, List.of("1")));
        inputQueue.add(new Expression("z", AssignmentOperator.ASSIGN, List.of("1")));
        inputQueue.add(new Expression("i", AssignmentOperator.ASSIGN, List.of("1")));
        inputQueue.add(new Expression("w", AssignmentOperator.ASSIGN, List.of("1")));
        inputQueue.add(new Expression("x", AssignmentOperator.ASSIGN, List.of("1")));
        inputQueue.add(new Expression("x", AssignmentOperator.ADD_ASSIGN, List.of("i")));
        inputQueue.add(new Expression("y", AssignmentOperator.DIVIDE_ASSIGN, List.of("1", "+", "i")));
        inputQueue.add(new Expression("z", AssignmentOperator.MULTIPLY_ASSIGN, List.of("2", "+", "++i")));
        inputQueue.add(new Expression("w", AssignmentOperator.SUBTRACT_ASSIGN, List.of("2", "+", "i++")));
        Thread.sleep(100);
        assertEquals(3, calculatorService.getVariablesManagerService().getVariable("i"));
        assertEquals(2, calculatorService.getVariablesManagerService().getVariable("x"));
        assertEquals(0.5, calculatorService.getVariablesManagerService().getVariable("y"));
        assertEquals(4, calculatorService.getVariablesManagerService().getVariable("z"));
        assertEquals(-3, calculatorService.getVariablesManagerService().getVariable("w"));
    }

    @Test
    public void testPrettyPrintResult() throws InvalidInputException, InterruptedException {
        inputQueue.add(new Expression("x", AssignmentOperator.ASSIGN, List.of("10")));
        inputQueue.add(new Expression("y", AssignmentOperator.ASSIGN, List.of("20")));
        Thread.sleep(100);
        String result = calculatorService.prettyPrintResult();
        assertEquals("(x=10,y=20)", result);
    }
}
