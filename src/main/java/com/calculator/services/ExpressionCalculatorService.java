package com.calculator.services;
import com.calculator.exceptions.InvalidInputException;
import com.calculator.models.AssignmentOperator;
import com.calculator.models.Expression;
import com.calculator.models.Operator;
import com.calculator.utils.ExpressionParser;

import java.util.List;
import java.util.Map;
import java.util.Stack;
// TODO: 1. Yes, you can assume there are spaces between each number or operator in the expression.
//        2. Yes, variable names can be longer than one character.
//        3. Supporting all types of operators, such as % or ^, is not mandatory.
//        4. Yes, you should support parentheses.
// TODO: validator
// TODO: TESTS
// TODO: convert operators to class that has apply method

public class ExpressionCalculatorService {
    private final VariablesManagerService variablesManagerService;
    private final Stack<Integer> values;
    private final Stack<Operator> operators;

    public ExpressionCalculatorService() {
        variablesManagerService = new VariablesManagerService();
        values = new Stack<>();
        operators = new Stack<>();
    }

    public Map<String, Integer> calculateExpressions(List<String> expressions) throws InvalidInputException {
        for(String rawExpression : expressions) {
            Expression expression = Expression.fromString(rawExpression);
            evaluateExpression(expression);
        }
        return variablesManagerService.getVariables();
    }

    private void evaluateExpression(Expression expression) throws InvalidInputException {
        for (String part : expression.getExpressionParts()) {
            insertToStack(part);
        }
        int result = calculateStackResult();
        evaluateAssignmentVariable(expression, result);
    }

    private void insertToStack(String expression_part) throws InvalidInputException {
        if (Operator.isOperator(expression_part)) {
            Operator currentOperator = Operator.valueOf(expression_part);
            Operator topOperator = operators.peek();
            if (expression_part.equals(Operator.CLOSING_PARENTHESIS.getSymbol())) {
                // Process until '(' is encountered
                while (!operators.isEmpty() && operators.peek() != Operator.OPEN_PARENTHESIS) {
                    processOperator();
                }
                if (!operators.isEmpty() && operators.peek() == Operator.OPEN_PARENTHESIS) {
                    operators.pop(); // Remove '('
                }
            }
            else if (Operator.hasHigherPrecedence(topOperator, currentOperator)) {
                processOperator();
                operators.push(currentOperator);
            }
            operators.push(currentOperator);
        } else if (ExpressionParser.isNumeric(expression_part)) {
            values.push(Integer.parseInt(expression_part));
        } else if (variablesManagerService.getVariables().containsKey(expression_part)) {
            values.push(variablesManagerService.getVariable(expression_part));
        } // TODO: increment
        else {
            throw new InvalidInputException(expression_part);
        }
    }

    private void evaluateAssignmentVariable(Expression expression, int calculatedValue) {
        AssignmentOperator assignmentOperator = expression.getAssignmentOperator();
        int oldValue = variablesManagerService.getVariables().getOrDefault(expression.getAssignedVariable(), 0);
        int newValue = assignmentOperator.apply(oldValue, calculatedValue);
        variablesManagerService.putVariable(expression.getAssignedVariable(), newValue);
    }

    private int calculateStackResult() {
        while (!operators.isEmpty()) {
            int b = values.pop();
            int a = values.pop();
            Operator operator = operators.pop();
            int result = operator.apply(a, b);
            values.push(result);
        }
        return values.pop();
    }

    private void processOperator() throws InvalidInputException {
        if (values.size() < 2) {
            throw new InvalidInputException("Invalid expression: Not enough values");
        }
        int rightValue = values.pop();
        int leftValue = values.pop();
        Operator operator = operators.pop();
        values.push(operator.apply(leftValue, rightValue));
    }
}
