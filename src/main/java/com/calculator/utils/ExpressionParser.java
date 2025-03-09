package com.calculator.utils;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.*;
import com.calculator.models.AssignmentOperator;
import com.calculator.models.Expression;
import com.calculator.models.operators.IUnaryOperator;


/**
 * The {@code ExpressionParser} class provides utility methods to parse mathematical expressions,
 * tokenize input strings, identify numeric values, and handle unary operators.
 * It supports parsing assignment expressions, tokenizing mathematical expressions,
 * formatting numeric values, and dynamically instantiating unary operators using reflection.
 */
 public class ExpressionParser {

    /**
     * Parses a given assignment expression and extracts variable assignment,
     * operator, and right-hand expression tokens.
     *
     * @param input the input string containing an assignment expression (e.g., "x += 5 + 3")
     * @return an {@link Expression} object containing parsed components
     * @throws IllegalArgumentException if the expression does not match an assignment pattern
     */
    public static Expression parse(String input) {
        final Pattern ASSIGNMENT_PATTERN = Pattern.compile("^\\s*([a-zA-Z_]\\w*)\\s*(=|\\+=|-=|\\*=|/=|%=)\\s*(.*)\\s*$");
        Matcher matcher = ASSIGNMENT_PATTERN.matcher(input);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid expression, assignment pattern was not found");
        }

        String assignedVariable = matcher.group(1);
        AssignmentOperator assignmentOperator = AssignmentOperator.fromSymbol(matcher.group(2));
        String rightSide = matcher.group(3);
        List<String> expressionParts = tokenizeExpression(rightSide);

        return new Expression(assignedVariable, assignmentOperator, expressionParts);
    }

    /**
     * Tokenizes a mathematical expression string into individual tokens such as numbers,
     * operators, and parentheses.
     *
     * @param expression the mathematical expression to tokenize
     * @return a list of tokens extracted from the expression
     */
    private static List<String> tokenizeExpression(String expression) {
        List<String> tokens = new ArrayList<>();
        Scanner scanner = new Scanner(expression);
        while (scanner.hasNext()) {
            String token = scanner.next();
            if (token.startsWith("(")) {
                tokens.add("(");
                tokens.add(token.substring(1));
            }
            else if (token.endsWith(")")) {
                tokens.add(token.substring(0, token.length() - 1));
                tokens.add(")");
            } else {
                tokens.add(token);
            }
        }

        return tokens;
    }

    /**
     * Determines whether a given string represents a numeric value.
     *
     * @param str the string to check
     * @return {@code true} if the string is numeric, otherwise {@code false}
     */
    public static boolean isNumeric(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses a unary operator from an expression string.
     * Supports both pre-increment (`++i`, `--i`) and post-increment (`i++`, `i--`) operators.
     * Uses reflection to dynamically instantiate the corresponding {@link IUnaryOperator} class.
     *
     * @param expression the input expression containing a unary operator
     * @param unaryOperators a map of available unary operators and their respective class types
     * @return an {@link Optional} containing an instance of the unary operator if found, otherwise empty
     * @throws NoSuchMethodException if the operator class constructor is not found
     * @throws InvocationTargetException if the constructor throws an exception
     * @throws InstantiationException if the operator class cannot be instantiated
     * @throws IllegalAccessException if access to the constructor is denied
     */
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

    /**
     * Formats a floating-point number to remove unnecessary trailing decimal zeros.
     *
     * @param number the floating-point number to format
     * @return a string representation of the formatted number
     */
    public static String formatNumber(float number) {
        DecimalFormat df = new DecimalFormat("#.##"); // Removes trailing .0
        return df.format(number);
    }
}
