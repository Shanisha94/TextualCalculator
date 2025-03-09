package com.calculator;
import com.calculator.exceptions.InvalidInputException;
import com.calculator.services.ExpressionCalculatorService;
import com.calculator.services.ExpressionParserProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private static final ExpressionParserProcessor parserProcessor = new ExpressionParserProcessor(queue);
    private static final ExpressionCalculatorService calculatorProcessor = new ExpressionCalculatorService(parserProcessor.getOutputQueue());

    public static void main(String[] args) {
        parserProcessor.start();
        calculatorProcessor.start();
        Thread workerThread = new Thread(Main::readUserInput);
        workerThread.start();
        try {
            workerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        try {
            logger.info(calculatorProcessor.prettyPrintResult());
        } catch (InvalidInputException e) {
            throw new RuntimeException("Failed to calculate expression", e);
        }
    }

    private static void readUserInput() {
        logger.info("Please enter expressions to calculate and type `exit` to see the result:");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("exit")) {
                break; // Stop reading input
            }
            queue.offer(line);
        }
        scanner.close();
        stop();
    }

    private static void stop() {
        logger.info("Processing finished. Exiting...");
        parserProcessor.stop();
        calculatorProcessor.stop();
    }
}