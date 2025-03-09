package com.calculator.services;
import com.calculator.exceptions.InvalidInputException;
import com.calculator.factories.OperatorFactory;
import com.calculator.models.AssignmentOperator;
import com.calculator.models.Expression;
import com.calculator.models.operators.*;
import com.calculator.utils.ExpressionParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.calculator.utils.ExpressionParser.formatNumber;
// TODO: docstrings

public class ExpressionCalculatorService implements IProcessor {
    private static final Logger logger = LogManager.getLogger(ExpressionCalculatorService.class);
    private final VariablesManagerService variablesManagerService;
    private final Stack<Float> values;
    private final Stack<IOperator> operators;
    private final BlockingQueue<Expression> inputQueue;
    private final Thread workerThread;
    private volatile boolean isRunning = true;

    public ExpressionCalculatorService(BlockingQueue<Expression> inputQueue) {
        variablesManagerService = new VariablesManagerService();
        values = new Stack<>();
        operators = new Stack<>();
        this.inputQueue = inputQueue;
        this.workerThread = new Thread(this::processQueue);
    }

    public VariablesManagerService getVariablesManagerService() {
        return variablesManagerService;
    }

    @Override
    public void start() {
        logger.debug("Starting ExpressionCalculatorService...");
        workerThread.start();
    }

    @Override
    public void stop() {
        logger.debug("Stopping ExpressionCalculatorService...");
        isRunning = false;
        workerThread.interrupt();
        try {
            workerThread.join();
            logger.debug("Worker thread stopped successfully.");
        } catch (InterruptedException e) {
            logger.error("Interrupted while stopping worker thread.", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void processQueue() {
        logger.debug("Worker thread started. Waiting for expressions...");
        while (isRunning || !inputQueue.isEmpty()) {
            try {
                Expression expression = inputQueue.poll(1, TimeUnit.SECONDS);
                if (expression != null) {
                    evaluateExpression(expression);
                    logger.debug("Evaluate: {}", expression);
                }
            } catch (InterruptedException e) {
                logger.debug("Worker thread interrupted. Stopping...");
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void evaluateExpression(Expression expression) throws InvalidInputException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (String part : expression.expressionParts()) {
            handleExpressionPart(part);
        }
        float result = calculateStackResult();
        evaluateAssignmentVariable(expression, result);
    }

    private void handleExpressionPart(String expression_part) throws InvalidInputException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (OperatorFactory.isOperator(expression_part)) {
            handleOperator(expression_part);
        } else if (ExpressionParser.isNumeric(expression_part)) {
            values.push(Float.parseFloat(expression_part));
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

    private void evaluateAssignmentVariable(Expression expression, float calculatedValue) {
        AssignmentOperator assignmentOperator = expression.assignmentOperator();
        float oldValue = variablesManagerService.getVariables().getOrDefault(expression.assignedVariable(), 0.0F);
        float newValue = assignmentOperator.apply(oldValue, calculatedValue);
        variablesManagerService.putVariable(expression.assignedVariable(), newValue);
    }

    private float calculateStackResult() {
        while (!operators.isEmpty() && !values.isEmpty()) {
            float b = values.pop();
            float a = values.pop();
            IOperator operator = operators.pop();
            float result = operator.apply(a, b);
            values.push(result);
        }
        return values.pop();
    }

    private void processOperator() throws InvalidInputException {
        if (values.size() < 2) {
            throw new InvalidInputException("Invalid expression: Not enough values");
        }
        float rightValue = values.pop();
        float leftValue = values.pop();
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
            } else {
                throw new InvalidInputException("Mismatched parentheses");
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
        if (!variablesManagerService.getVariables().containsKey(unaryOperator.getVariable())) {
            throw new InvalidInputException(String.format("Variable %s is used before being assigned", unaryOperator.getVariable()));
        }
        float currentValue = variablesManagerService.getVariable(unaryOperator.getVariable());
        float newValue = unaryOperator.apply(currentValue);
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
            sb.append(keys.get(i)).append("=").append(formatNumber(variablesManagerService.getVariable(keys.get(i))));
            if (i < keys.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

}
