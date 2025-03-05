package com.calculator.models;

import jdk.jshell.spi.ExecutionControl;

import java.util.List;

public class Expression { // TODO: struct? record class?
    private final String assignedVariable;
    private final AssignmentOperator assignmentOperator;
    private final List<String> expressionParts; // TODO: change to IOperator/IVariable

    public Expression(String assignedVariable, AssignmentOperator assignmentOperator, List<String> expressionParts) {
        this.assignedVariable = assignedVariable;
        this.assignmentOperator = assignmentOperator;
        this.expressionParts = expressionParts;
    }

    public String getAssignedVariable() {
        return assignedVariable;
    }

    public AssignmentOperator getAssignmentOperator() {
        return assignmentOperator;
    }

    public List<String> getExpressionParts() {
        return expressionParts;
    }

    public static Expression fromString(String data) {
       return; // TODO
    }
}
