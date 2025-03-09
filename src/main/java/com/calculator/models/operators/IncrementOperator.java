package com.calculator.models.operators;

public record IncrementOperator(String variable, boolean postIncrement) implements IUnaryOperator {

    @Override
    public float apply(float value) {
        return value + 1;
    }

    public static String getSymbol() {
        return "++";
    }

    @Override
    public boolean isPostOperation() {
        return postIncrement;
    }

    @Override
    public String getVariable() {
        return variable;
    }
}
