import com.calculator.models.operators.CloseParenthesisOperator;
import com.calculator.models.operators.IOperator;
import com.calculator.models.operators.OpenParenthesisOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParenthesisTest {

    static Stream<IOperator> provideOperators() {
        return Stream.of(
                new OpenParenthesisOperator(),
                new CloseParenthesisOperator()
        );
    }
    @ParameterizedTest
    @MethodSource("provideOperators")
    public void testCantApplyParenthesis(IOperator operator) {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> {
            operator.apply(2, 0);
        });
        assertEquals("Parentheses are not directly applied to values", exception.getMessage());
    }


}
