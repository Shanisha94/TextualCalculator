package com.calculator.services;

import com.calculator.exceptions.InvalidInputException;
import java.util.HashMap;
import java.util.Map;


/**
 * The {@code VariablesManagerService} class is responsible for managing named variables
 * and their corresponding floating-point values.
 * It provides methods to store, retrieve, and list variables used in calculations.
 */

public class VariablesManagerService {
    /**
     * A map storing variable names as keys and their corresponding floating-point values.
     */
    private static final Map<String, Float> variables = new HashMap<>();

    /**
     * Stores a variable with the given name and value.
     * If the variable already exists, its value will be updated.
     *
     * @param variable the name of the variable
     * @param value the floating-point value to be assigned to the variable
     */
    public void putVariable(String variable, float value) {
        variables.put(variable, value);
    }

    /**
     * Retrieves the value of a variable.
     * If the variable does not exist, an {@link InvalidInputException} is thrown.
     *
     * @param variable the name of the variable to retrieve
     * @return the floating-point value of the variable
     * @throws InvalidInputException if the variable is not found
     */
    public float getVariable(String variable) throws InvalidInputException {
        if (variables.containsKey(variable)) {
            return variables.get(variable);
        }
        throw new InvalidInputException(String.format("Variable %s is not found", variable));
    }

    /**
     * Returns a map of all stored variables.
     * The returned map is the actual internal storage, meaning modifications
     * to it will affect the stored variables.
     *
     * @return a map containing all stored variables
     */
    public Map<String, Float> getVariables() {
        return variables;
    }
}
