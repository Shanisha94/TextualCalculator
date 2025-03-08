package com.calculator.models.operators;

import com.calculator.models.PrecedenceLevel;

public class ExponentOperator implements IOperator {
    @Override
    public float apply(float firstValue, float secondValue) {
        return (float) Math.pow(firstValue, secondValue); // Use Math.pow for exponentiation
    }

    @Override
    public String getSymbol() {
        return "^";
    }

    @Override
    public int getPrecedence() {
        return PrecedenceLevel.HIGH.getLevel();
    }
}
