package com.calculator.models.operators;

import java.util.HashMap;
import java.util.Map;

public class OperatorFactory {
    private static final Map<String, IOperator> operators = new HashMap<>();

    static {
        operators.put("+", new AddOperator());
        operators.put("-", new SubtractOperator());
        operators.put("*", new MultiplyOperator());
        operators.put("/", new DivideOperator());
        operators.put("%", new ModulusOperator());
        operators.put("^", new ExponentOperator());
    }

    public static IOperator getOperator(String symbol) {
        if (!operators.containsKey(symbol)) {
            throw new IllegalArgumentException("Invalid operator: " + symbol);
        }
        return operators.get(symbol);
    }

    public static boolean isOperator(String symbol) {
        return operators.containsKey(symbol);
    }

    public static boolean hasHigherPrecedence(IOperator op1, IOperator op2) {
        return op1.getPrecedence() > op2.getPrecedence();
    }
}
