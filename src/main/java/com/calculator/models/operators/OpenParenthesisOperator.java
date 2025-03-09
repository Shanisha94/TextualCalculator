package com.calculator.models.operators;

import com.calculator.models.PrecedenceLevel;

public class OpenParenthesisOperator implements IOperator {
    @Override
    public float apply(float firstValue, float secondValue) {
        throw new UnsupportedOperationException("Parentheses are not directly applied to values.");
    }

    @Override
    public String getSymbol() {
        return "(";
    }

    @Override
    public int getPrecedence() {
        return PrecedenceLevel.LOW.getLevel();
    }
}
