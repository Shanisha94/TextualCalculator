package com.calculator.models.operators;

public interface IOperator {
    int apply(int firstValue, int secondValue);

    String getSymbol();

    int getPrecedence();
}
