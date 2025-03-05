package com.calculator.services;
import com.calculator.exceptions.InvalidInputException;
import com.calculator.models.Operator;
import com.calculator.utils.VariableFinder;
import java.util.Stack;
 // TODO: can I assume the variables are characters or also String?

public class ExpressionCalculatorService {
    private static final VariablesManagerService variablesManagerService = new VariablesManagerService();
    private Stack<Integer> values = new Stack<>();
    private Stack<Operator> operators = new Stack<>();

    private void evaluateExpression(String expr) throws InvalidInputException {
        // Insert to Stack
        if (operator.equals("=")) {
            variables.put(varName, value);
        } else if (operator.equals("+=")) {
            variables.put(varName, variables.getOrDefault(varName, 0) + value);
        } else if (operator.equals("-=")) {
            variables.put(varName, variables.getOrDefault(varName, 0) - value);
        }

        String[] tokens = expr.split("\\s+");
        for (String token : tokens) {
            if (token.length() > 1) {
                // parse to Char
            } else {

            }
        }
        // Add the variable to the map
        String assignmentVariable = VariableFinder.findAssignmentVariable(expr);

    }

    private void insertToStack(Character token) {
        switch (token) {
            case Character.isDigit():
                values.push(Integer.parse(token))
            case Character.isLetter():
                if (!variables.containsKey(token)) {
                    throw new IllegalArgumentException("Variable not defined: " + token);
                }
                values.push(variables.get(token));
            case Operator.isOperator(token) {
                top_operator = operators.peek()
                if (top_operator.hasHigherPrecedence(token)) {
                    // pop and calc
                }
            }
        }
    }
}
