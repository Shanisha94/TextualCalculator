package com.calculator.models.operators;

public interface IOperator {
    float apply(float firstValue, float secondValue);

    String getSymbol();

    int getPrecedence();
}
