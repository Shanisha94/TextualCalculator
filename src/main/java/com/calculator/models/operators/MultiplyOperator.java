package com.calculator.models.operators;

import com.calculator.models.PrecedenceLevel;

public class MultiplyOperator implements IOperator {
    @Override
    public int apply(int firstValue, int secondValue) {
        return firstValue * secondValue;
    }

    @Override
    public String getSymbol() {
        return "*";
    }

    @Override
    public int getPrecedence() {
        return PrecedenceLevel.MEDIUM.getLevel();
    }
}
