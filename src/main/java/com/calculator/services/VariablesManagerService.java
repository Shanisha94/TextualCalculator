package com.calculator.services;

import com.calculator.exceptions.InvalidInputException;
import java.util.HashMap;
import java.util.Map;

public class VariablesManagerService {
    private static final Map<String, Float> variables = new HashMap<>();

    public void putVariable(String variable, float value) {
        variables.put(variable, value);
    }

    public float getVariable(String variable) throws InvalidInputException {
        if (variables.containsKey(variable)) {
            return variables.get(variable);
        }
        throw new InvalidInputException(String.format("Variable %s is not found", variable));
    }

    public Map<String, Float> getVariables() {
        return variables;
    }
}
