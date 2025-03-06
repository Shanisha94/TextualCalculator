package com.calculator.factories;

import com.calculator.models.operators.*;
import com.calculator.utils.ExpressionParser;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class OperatorFactory {
    private static final Map<String, IOperator> operators = new HashMap<>();
    private static final Map<String, Class<? extends IUnaryOperator>> unaryOperators = new HashMap<>();

    static {
        operators.put("+", new AddOperator());
        operators.put("-", new SubtractOperator());
        operators.put("*", new MultiplyOperator());
        operators.put("/", new DivideOperator());
        operators.put("%", new ModulusOperator());
        operators.put("^", new ExponentOperator());
        operators.put("(", new OpenParenthesisOperator());
        operators.put(")", new CloseParenthesisOperator());
        unaryOperators.put("++", IncrementOperator.class);
        unaryOperators.put("--", DecrementOperator.class);
    }

    public static IOperator getOperator(String symbol) {
        if (!operators.containsKey(symbol)) {
            throw new IllegalArgumentException("Invalid operator: " + symbol);
        }
        return operators.get(symbol);
    }

    public static Optional<IUnaryOperator> getUnaryOperator(String expression) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return ExpressionParser.parseUnaryOperator(expression, unaryOperators);
    }

    public static boolean isOperator(String symbol) {
        return operators.containsKey(symbol);
    }

    public static boolean hasHigherPrecedence(IOperator op1, IOperator op2) {
        return op1.getPrecedence() > op2.getPrecedence();
    }
}
