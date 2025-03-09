package com.calculator.models;

import java.util.List;

public record Expression(String assignedVariable, AssignmentOperator assignmentOperator,
                         List<String> expressionParts) {
}
