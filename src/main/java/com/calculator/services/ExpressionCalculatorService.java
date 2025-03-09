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


/**
 * Service for evaluating mathematical expressions.
 * This service runs a background thread that processes mathematical expressions from an input queue.
 * It supports standard operators, assignment, and unary operations.
 */
public class ExpressionCalculatorService implements IProcessor {
    private static final Logger logger = LogManager.getLogger(ExpressionCalculatorService.class);
    private final VariablesManagerService variablesManagerService;
    private final Stack<Float> values;
    private final Stack<IOperator> operators;
    private final BlockingQueue<Expression> inputQueue;
    private final Thread workerThread;
    private volatile boolean isRunning = true;

    /**
     * Constructs a new ExpressionCalculatorService.
     *
     * @param inputQueue The queue containing expressions to process.
     */
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

    /**
     * Starts the background thread to process expressions.
     */
    @Override
    public void start() {
        logger.debug("Starting ExpressionCalculatorService...");
        workerThread.start();
    }

    /**
     * Stops the background thread gracefully.
     */
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

    /**
     * Processes the queue of expressions, evaluating each one.
     */
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

    /**
     * Evaluates a mathematical expression and updates the variable storage.
     *
     * @param expression The mathematical expression to evaluate.
     * @throws InvalidInputException If the expression contains invalid input.
     * @throws InvocationTargetException If there is an error invoking a method.
     * @throws NoSuchMethodException If a method is not found during reflection.
     * @throws InstantiationException If an object instantiation fails.
     * @throws IllegalAccessException If access to a class is denied.
     */
    private void evaluateExpression(Expression expression) throws InvalidInputException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (String part : expression.expressionParts()) {
            handleExpressionPart(part);
        }
        float result = calculateStackResult();
        evaluateAssignmentVariable(expression, result);
    }

    /**
     * Handles an individual part of an expression.
     *
     * @param expressionPart The part of the expression to process.
     * @throws InvalidInputException If the part is not a valid number, variable, or operator.
     * @throws InvocationTargetException If an error occurs during method invocation.
     * @throws NoSuchMethodException If a required method is not found.
     * @throws InstantiationException If an object cannot be instantiated.
     * @throws IllegalAccessException If access to a class is denied.
     */
    private void handleExpressionPart(String expressionPart) throws InvalidInputException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (OperatorFactory.isOperator(expressionPart)) {
            handleOperator(expressionPart);
        } else if (ExpressionParser.isNumeric(expressionPart)) {
            values.push(Float.parseFloat(expressionPart));
        } else if (variablesManagerService.getVariables().containsKey(expressionPart)) {
            values.push(variablesManagerService.getVariable(expressionPart));
        } else if (expressionPart.contains(IncrementOperator.getSymbol()) || expressionPart.contains(DecrementOperator.getSymbol())) {
            Optional<IUnaryOperator> operator = OperatorFactory.getUnaryOperator(expressionPart);
            if (operator.isPresent()) {
                handleUnaryOperator(operator.get());
            } else {
                throw new InvalidInputException(expressionPart);
            }
        }
        else {
            throw new InvalidInputException(expressionPart);
        }
    }

    /**
     * Evaluates and assigns a value to a variable based on the assignment operator.
     *
     * @param expression The assignment expression.
     * @param calculatedValue The result of the expression.
     * @throws InvalidInputException If variable is not initialized and has assignment operator that is not '='
     */
    private void evaluateAssignmentVariable(Expression expression, float calculatedValue) throws InvalidInputException {
        AssignmentOperator assignmentOperator = expression.assignmentOperator();
        if (!variablesManagerService.getVariables().containsKey(expression.assignedVariable()) && expression.assignmentOperator() != AssignmentOperator.ASSIGN) {
            throw new InvalidInputException("Assignment variable is not initialized");
        }
        float oldValue = variablesManagerService.getVariables().get(expression.assignedVariable());
        float newValue = assignmentOperator.apply(oldValue, calculatedValue);
        variablesManagerService.putVariable(expression.assignedVariable(), newValue);
    }

    /**
     * Computes the final result of the expression by processing the operator and value stacks.
     *
     * @return The final computed result.
     */
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

    /**
     * Processes and applies an operator to the values stack.
     *
     * @throws InvalidInputException If there are not enough values in the stack.
     */
    private void processOperator() throws InvalidInputException {
        if (values.size() < 2) {
            throw new InvalidInputException("Invalid expression: Not enough values");
        }
        float rightValue = values.pop();
        float leftValue = values.pop();
        IOperator operator = operators.pop();
        values.push(operator.apply(leftValue, rightValue));
    }

    /**
     * Handles standard mathematical operators and parentheses.
     *
     * @param operator The operator to process.
     * @throws InvalidInputException If there is a mismatch in parentheses or operator precedence issues.
     */
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

    /**
     * Handles unary operators such as post/pre increment and decrement.
     *
     * @param unaryOperator The operator to process.
     * @throws InvalidInputException If there is a mismatch in unary operator declaration.
     */
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

    /**
     * Returns a formatted string of all variables and their values.
     *
     * @return A string representation of stored variables.
     * @throws InvalidInputException If there is an issue retrieving values.
     */
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
