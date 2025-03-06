package com.calculator.models.operators;

public interface IUnaryOperator {
    int apply(int value);

    static String getSymbol() {
        return null;
    }

    boolean isPostOperation();

    String getVariable();
}
