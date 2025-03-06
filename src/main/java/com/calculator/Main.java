package com.calculator;
import com.calculator.services.ExpressionCalculatorService;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<String> expressions = readUserInput();
        ExpressionCalculatorService expressionCalculatorService = new ExpressionCalculatorService();
        try {
            Map<String, Integer> variablesResult = expressionCalculatorService.calculateExpressions(expressions);
            System.out.println(variablesResult);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static List<String> readUserInput() {
        System.out.println("Please enter expressions to calculate:");
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