package com.calculator.models.operators;

public interface IUnaryOperator {
    float apply(float value);

    static String getSymbol() {
        return null;
    }

    boolean isPostOperation();

    String getVariable();
}
