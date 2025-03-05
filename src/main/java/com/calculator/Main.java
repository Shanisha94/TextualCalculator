package com.calculator;
import com.calculator.services.ExpressionCalculatorService;

import java.beans.Expression;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<String> expressions = readUserInput();
        ExpressionCalculatorService expressionCalculatorService = new ExpressionCalculatorService();
        expressionCalculatorService.evaluateExpression(expressions);
    }

    private static List<String> readUserInput() {
        List<String> expressions = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("END")) break; // Exit on "END"
            if (!line.isEmpty()) {
                expressions.add(line);
            }
        }
        scanner.close();
        return expressions;
    }
}