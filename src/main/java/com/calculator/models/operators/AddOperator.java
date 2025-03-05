package com.calculator.models.operators;

public class AddOperator implements IOperator {
    @Override
    public int apply(int firstValue, int secondValue) {
        return firstValue + secondValue;
    }

    @Override
    public String getSymbol() {
        return "+";
    }

    @Override
    public int getPrecedence() {
        return 1;
    }
}
