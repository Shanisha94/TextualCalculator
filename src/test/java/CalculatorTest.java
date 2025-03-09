import static org.junit.jupiter.api.Assertions.*;

import com.calculator.models.operators.*;
import org.junit.jupiter.api.Test;

public class CalculatorTest {
    @Test
    public void testAddition() {
        AddOperator operator = new AddOperator();
        float result = operator.apply(2, 3);
        assertEquals(5, result, "2 + 3 should equal 5");
    }

    @Test
    public void testSubtraction() {
        SubtractOperator operator = new SubtractOperator();
        float result = operator.apply(3, 2);
        assertEquals(1, result, "3 - 2 should equal 1");
    }

    @Test
    public void testMultiplication() {
        MultiplyOperator operator = new MultiplyOperator();
        float result = operator.apply(2, 3);
        assertEquals(6, result, "2 * 3 should equal 6");
    }

    @Test
    public void testDivision() {
        DivideOperator operator = new DivideOperator();
        float result = operator.apply(4, 2);
        assertEquals(2, result, "4 / 2 should equal 2");
    }

    @Test
    public void testDivisionByZero() {
        DivideOperator operator = new DivideOperator();
        Exception exception = assertThrows(ArithmeticException.class, () -> {
            operator.apply(2, 0);
        });
        assertEquals("Division by zero", exception.getMessage());
    }

    @Test
    public void testExponent() {
        ExponentOperator operator = new ExponentOperator();
        float result = operator.apply(2, 2);
        assertEquals(4, result, "2 ^ 2 should equal 4");
    }

    @Test
    public void testModulo() {
        ModulusOperator operator = new ModulusOperator();
        float result = operator.apply(3, 2);
        assertEquals(1, result, "3 % 2 should equal 1");
    }
}
