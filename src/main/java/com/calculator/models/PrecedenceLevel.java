package com.calculator.models;

public enum PrecedenceLevel {
    LOW(1),         // For + and -
    MEDIUM(2),      // For * and /
    HIGH(3),        // For ^ (exponentiation)
    PARENTHESIS(4); // Highest precedence

    private final int level;

    PrecedenceLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean hasHigherPrecedenceThan(PrecedenceLevel other) {
        return this.level > other.level;
    }
}
