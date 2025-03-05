package com.calculator.utils;
import java.util.regex.*;
import com.calculator.exceptions.*;

public class ExpressionParser {

    public static String findAssignmentVariable(String expr) throws InvalidInputException {
        // Regex to match assignment statements (variable before '=', '+=', '-=', '*=', '/=')
        Pattern pattern = Pattern.compile("^\\s*([a-zA-Z_]\\w*)\\s*(=|\\+=|-=|\\*=|/=)\\s*");
        Matcher matcher = pattern.matcher(expr);

        if (matcher.find()) {
            return matcher.group(1); // Group 1 captures the variable name
        }
        throw new InvalidInputException("Variable assignment is not found");
    }
}
