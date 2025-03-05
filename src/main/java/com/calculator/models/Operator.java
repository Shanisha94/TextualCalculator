package com.calculator.models;

public enum Operator {
    ADD("+", 1) {
        @Override
        public int apply(int firstValue, int secondValue) {
            return firstValue + secondValue;
        }
    },
    SUBTRACT("-", 1) {
        @Override
        public int apply(int firstValue, int secondValue) {
            return firstValue - secondValue;
        }
    },
    MULTIPLY("*", 2) {
        @Override
        public int apply(int firstValue, int secondValue) {
            return firstValue * secondValue;
        }
    },
    DIVIDE("/", 2) {
        @Override
        public int apply(int firstValue, int secondValue) {
            if (secondValue == 0) throw new ArithmeticException("Division by zero");
            return firstValue / secondValue;
        }
    },
    MODULUS("%", 2) {
        @Override
        public int apply(int firstValue, int secondValue) {
            return firstValue % secondValue;
        }
    },
    EXPONENT("^", 3) {
        @Override
        public int apply(int firstValue, int secondValue) {
            return firstValue ^ secondValue;
        }
    },
    OPEN_PARENTHESIS("(", 1) {
        @Override
        public int apply(int oldValue, int newValue) {
            return 0; // TODO
        }
    },
    CLOSING_PARENTHESIS(")", 4) {
        @Override
        public int apply(int oldValue, int newValue) {
            return 0; // TODO
        }
    };

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

    public static boolean hasHigherPrecedence(Operator op1, Operator op2) {
        return op1.getPrecedence() > op2.getPrecedence();
    }

    public static boolean isOperator(String symbol) {
        for (Operator op : values()) {
            if (op.symbol.equals(symbol)) {
                return true;
            }
        }
        return false;
    }

    // Abstract method to apply operation
    public abstract int apply(int oldValue, int newValue);
}
