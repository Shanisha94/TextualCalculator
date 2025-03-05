package com.calculator;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<String> expressions = readUserInput();
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