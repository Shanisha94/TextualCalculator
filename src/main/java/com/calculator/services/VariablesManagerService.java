package com.calculator.services;

import com.calculator.exceptions.InvalidInputException;
import java.util.HashMap;
import java.util.Map;

public class VariablesManagerService {
    private static final Map<String, Integer> variables = new HashMap<>();

    public void putVariable(String variable, int value) {
        variables.put(variable, value);
    }

    public void putVariable(String variable, int value, String assignmentOperator) {
        variables.put(variable, value);
    }

    public int getVariable(String variable) throws InvalidInputException {
        if (variables.containsKey(variable)) {
            return variables.get(variable);
        }
        throw new InvalidInputException(String.format("Variable %s is not found", variable));
    }

    public Map<String, Integer> getVariables() {
        return variables;
    }
}
