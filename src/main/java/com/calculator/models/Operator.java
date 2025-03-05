package com.calculator.models;

public enum Operator {
    ADD("+", 1),
    SUBTRACT("-", 1),
    MULTIPLY("*", 2),
    DIVIDE("/", 2),
    MODULUS("%", 2),
    EXPONENT("^", 3),
    OPEN_PARENTHESIS("(", 1),
    CLOSING_PARENTHESIS(")", 4);

    private final String symbol;
    private final int precedence;

    Operator(String symbol, int precedence) {
        this.symbol = symbol;
        this.precedence = precedence;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getPrecedence() {
        return precedence;
    }

    public static Operator fromSymbol(String symbol) {
        for (Operator op : values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown operator: " + symbol);
    }

    public static boolean hasHigherPrecedence(Operator op1, Operator op2) {
        return op1.getPrecedence() > op2.getPrecedence();
    }

    public static boolean hasEqualPrecedence(Operator op1, Operator op2) {
        return op1.getPrecedence() == op2.getPrecedence();
    }

    public static boolean isOperator(String symbol) {
        for (Operator op : values()) {
            if (op.symbol.equals(symbol)) {
                return true;
            }
        }
        return false;
    }
}
