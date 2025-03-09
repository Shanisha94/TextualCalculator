package com.calculator.services;

/**
 * The {@code IProcessor} interface defines a contract for processing tasks asynchronously.
 * Implementing classes are responsible for managing a queue of tasks, starting and stopping
 * processing as needed.
 */

public interface IProcessor {
    void start();
    void stop();
    void processQueue();
}
