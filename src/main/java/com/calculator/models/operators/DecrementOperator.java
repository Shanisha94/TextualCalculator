package com.calculator.models.operators;

public record DecrementOperator(String variable, boolean postDecrement) implements IUnaryOperator {

    @Override
    public float apply(float value) {
        return value - 1;
    }

    public static String getSymbol() {
        return "--";
    }

    @Override
    public boolean isPostOperation() {
        return postDecrement;
    }

    @Override
    public String getVariable() {
        return variable;
    }
}
