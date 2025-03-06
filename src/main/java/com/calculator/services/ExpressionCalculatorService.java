package com.calculator.services;
import com.calculator.exceptions.InvalidInputException;
import com.calculator.factories.OperatorFactory;
import com.calculator.models.AssignmentOperator;
import com.calculator.models.Expression;
import com.calculator.models.operators.*;
import com.calculator.utils.ExpressionParser;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
// TODO: 1. Yes, you can assume there are spaces between each number or operator in the expression.
//        2. Yes, variable names can be longer than one character.
//        3. Supporting all types of operators, such as % or ^, is not mandatory.
//        4. Yes, you should support parentheses.
// TODO: validator
// TODO: TESTS

public class ExpressionCalculatorService {
    private final VariablesManagerService variablesManagerService;
    private final Stack<Integer> values;
    private final Stack<IOperator> operators;

    public ExpressionCalculatorService() {
        variablesManagerService = new VariablesManagerService();
        values = new Stack<>();
        operators = new Stack<>();
    }

    public String calculateExpressions(List<String> expressions) throws InvalidInputException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for(String rawExpression : expressions) {
            Expression expression = ExpressionParser.parse(rawExpression);
            evaluateExpression(expression);
        }
        return prettyPrintResult();
    }

    private void evaluateExpression(Expression expression) throws InvalidInputException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (String part : expression.expressionParts()) {
            handleExpressionPart(part);
        }
        int result = calculateStackResult();
        evaluateAssignmentVariable(expression, result);
    }

    private void handleExpressionPart(String expression_part) throws InvalidInputException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (OperatorFactory.isOperator(expression_part)) {
            handleOperator(expression_part);
        } else if (ExpressionParser.isNumeric(expression_part)) {
            values.push(Integer.parseInt(expression_part));
        } else if (variablesManagerService.getVariables().containsKey(expression_part)) {
            values.push(variablesManagerService.getVariable(expression_part));
        } else if (expression_part.contains(IncrementOperator.getSymbol()) || expression_part.contains(DecrementOperator.getSymbol())) {
            Optional<IUnaryOperator> operator = OperatorFactory.getUnaryOperator(expression_part);
            if (operator.isPresent()) {
                handleUnaryOperator(operator.get());
            } else {
                throw new InvalidInputException(expression_part);
            }
        }
        else {
            throw new InvalidInputException(expression_part);
        }
    }

    private void evaluateAssignmentVariable(Expression expression, int calculatedValue) {
        AssignmentOperator assignmentOperator = expression.assignmentOperator();
        int oldValue = variablesManagerService.getVariables().getOrDefault(expression.assignedVariable(), 0);
        int newValue = assignmentOperator.apply(oldValue, calculatedValue);
        variablesManagerService.putVariable(expression.assignedVariable(), newValue);
    }

    private int calculateStackResult() {
        while (!operators.isEmpty() && !values.isEmpty()) {
            int b = values.pop();
            int a = values.pop();
            IOperator operator = operators.pop();
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
        IOperator operator = operators.pop();
        values.push(operator.apply(leftValue, rightValue));
    }

    private void handleOperator(String operator) throws InvalidInputException {
        IOperator currentOperator = OperatorFactory.getOperator(operator);
        if (currentOperator instanceof OpenParenthesisOperator) {
            operators.push(currentOperator);
        }
        else if (currentOperator instanceof CloseParenthesisOperator) {
            // Process everything inside the parentheses
            while (!operators.isEmpty() && !(operators.peek() instanceof OpenParenthesisOperator)) {
                processOperator();
            }
            if (!operators.isEmpty() && operators.peek() instanceof OpenParenthesisOperator) {
                operators.pop(); // Remove '(' from the stack
            }
        }
        else {
            // Handle standard operators
            while (!operators.isEmpty() && OperatorFactory.hasHigherPrecedence(operators.peek(), currentOperator)) {
                processOperator();
            }
            operators.push(currentOperator);
        }
    }

    private void handleUnaryOperator(IUnaryOperator unaryOperator) throws InvalidInputException {
        int currentValue = variablesManagerService.getVariable(unaryOperator.getVariable());
        int newValue = unaryOperator.apply(currentValue);
        variablesManagerService.putVariable(unaryOperator.getVariable(), newValue);
        if (unaryOperator.isPostOperation()) {
            values.push(currentValue);
        } else {
            values.push(newValue);
        }
    }

    public String prettyPrintResult() throws InvalidInputException {
        StringBuilder sb =new StringBuilder();
        sb.append("(");
        List<String> keys = new ArrayList<>(variablesManagerService.getVariables().keySet());
        for (int i = 0; i < keys.size(); i++) {
            sb.append(keys.get(i)).append("=").append(variablesManagerService.getVariable(keys.get(i)));
            if (i < keys.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
