package com.calculator.models;

public enum AssignmentOperator {
    ASSIGN("=") {
        @Override
        public int apply(int oldValue, int newValue) {
            return newValue; // Direct assignment
        }
    },
    ADD_ASSIGN("+=") {
        @Override
        public int apply(int oldValue, int newValue) {
            return oldValue + newValue; // Addition assignment
        }
    },
    SUBTRACT_ASSIGN("-=") {
        @Override
        public int apply(int oldValue, int newValue) {
            return oldValue - newValue; // Subtraction assignment
        }
    },
    MULTIPLY_ASSIGN("*=") {
        @Override
        public int apply(int oldValue, int newValue) {
            return oldValue * newValue; // Multiplication assignment
        }
    },
    DIVIDE_ASSIGN("/=") {
        @Override
        public int apply(int oldValue, int newValue) {
            if (newValue == 0) throw new ArithmeticException("Division by zero");
            return oldValue / newValue; // Division assignment
        }
    };

    private final String symbol;

    AssignmentOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    // Abstract method to apply operation
    public abstract int apply(int oldValue, int newValue);
}
