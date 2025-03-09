package com.calculator.models.operators;

import com.calculator.models.PrecedenceLevel;

public class DivideOperator implements IOperator {
    @Override
    public float apply(float firstValue, float secondValue) {
        if (secondValue == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return firstValue / secondValue;
    }

    @Override
    public String getSymbol() {
        return "/";
    }

    @Override
    public int getPrecedence() {
        return PrecedenceLevel.MEDIUM.getLevel();
    }
}
