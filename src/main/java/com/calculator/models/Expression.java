package com.calculator.models;

import com.calculator.exceptions.InvalidInputException;
import com.calculator.utils.ExpressionParser;

import java.util.List;

public record Expression(String assignedVariable, AssignmentOperator assignmentOperator,
                         List<String> expressionParts) {
}
