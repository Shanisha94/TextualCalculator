package com.calculator.models;

public enum AssignmentOperator {
    ASSIGN("=") {
        @Override
        public float apply(float oldValue, float newValue) {
            return newValue;
        }
    },
    ADD_ASSIGN("+=") {
        @Override
        public float apply(float oldValue, float newValue) {
            return oldValue + newValue;
        }
    },
    SUBTRACT_ASSIGN("-=") {
        @Override
        public float apply(float oldValue, float newValue) {
            return oldValue - newValue;
        }
    },
    MULTIPLY_ASSIGN("*=") {
        @Override
        public float apply(float oldValue, float newValue) {
            return oldValue * newValue;
        }
    },
    DIVIDE_ASSIGN("/=") {
        @Override
        public float apply(float oldValue, float newValue) {
            if (newValue == 0) throw new ArithmeticException("Division by zero");
            return oldValue / newValue;
        }
    };

    private final String symbol;

    AssignmentOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static AssignmentOperator fromSymbol(String symbol) {
        for (AssignmentOperator op : values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Invalid assignment operator: " + symbol);
    }

    // Abstract method to apply operation
    public abstract float apply(float oldValue, float newValue);
}
