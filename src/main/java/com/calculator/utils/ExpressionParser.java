package com.calculator.utils;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.*;
import com.calculator.models.AssignmentOperator;
import com.calculator.models.Expression;
import com.calculator.models.operators.IUnaryOperator;


public class ExpressionParser {

    public static Expression parse(String input) {
        final Pattern ASSIGNMENT_PATTERN = Pattern.compile("^\\s*([a-zA-Z_]\\w*)\\s*(=|\\+=|-=|\\*=|/=|%=)\\s*(.*)\\s*$");
        Matcher matcher = ASSIGNMENT_PATTERN.matcher(input);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid expression: " + input);
        }

        String assignedVariable = matcher.group(1);
        AssignmentOperator assignmentOperator = AssignmentOperator.fromSymbol(matcher.group(2));
        String rightSide = matcher.group(3);
        List<String> expressionParts = tokenizeExpression(rightSide);

        return new Expression(assignedVariable, assignmentOperator, expressionParts);
    }

    private static List<String> tokenizeExpression(String expression) {
        List<String> tokens = new ArrayList<>();

        // Regex to match variables, numbers, operators, and pre/post increments
        final Pattern EXPRESSIONS_PATTERN = Pattern.compile("\\+\\+|--|\\w+|[+\\-*/()]");
        Matcher matcher = EXPRESSIONS_PATTERN.matcher(expression);

        while (matcher.find()) {
            tokens.add(matcher.group());
        }

        return tokens;
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Optional<IUnaryOperator> parseUnaryOperator(String expression, Map<String, Class<? extends IUnaryOperator>> unaryOperators) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // Regex for pre-increment/decrement (++i, --i)
        Pattern prePattern = Pattern.compile("(\\+\\+|--)(\\w+)");
        // Regex for post-increment/decrement (i++, i--)
        Pattern postPattern = Pattern.compile("(\\w+)(\\+\\+|--)");

        Matcher preMatcher = prePattern.matcher(expression);
        Matcher postMatcher = postPattern.matcher(expression);
        boolean isPostOperator = false;
        String variable = null;
        String operator = null;

        if (preMatcher.find()) {
            operator = preMatcher.group(1);
            variable = preMatcher.group(2);
        }

        else if (postMatcher.find()) {
            variable = postMatcher.group(1);
            operator = postMatcher.group(2);
            isPostOperator = true;
        }

        if (variable == null || operator == null) {
            return Optional.empty();
        }

        return Optional.of((unaryOperators.get(operator))
                .getDeclaredConstructor(String.class, boolean.class)
                .newInstance(variable, isPostOperator));
    }
}
