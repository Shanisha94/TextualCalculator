package com.calculator;
import com.calculator.exceptions.InvalidInputException;
import com.calculator.services.ExpressionCalculatorService;
import com.calculator.services.ExpressionParserProcessor;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The {@code Main} class serves as the entry point for the calculator application.
 * It initializes the necessary processors for parsing and evaluating mathematical expressions
 * and continuously reads user input from the console until the user types "exit".
 * </p>
 *
 * <p><strong>Application Workflow:</strong></p>
 * <ol>
 *     <li>Starts the expression parser processor.</li>
 *     <li>Starts the expression calculator processor.</li>
 *     <li>Reads user input in a separate worker thread.</li>
 *     <li>Processes expressions asynchronously.</li>
 *     <li>Displays the final result after "exit" is typed.</li>
 * </ol>
 *
 * <p><strong>Usage:</strong></p>
 * <pre>{@code
 * java -jar CalculatorApp.jar
 * }</pre>
 *
 * <p><strong>Example Input:</strong></p>
 * <pre>{@code
 * x = 5 + 3
 * y = x * 2
 * exit
 * }</pre>
 *
 * <p><strong>Example Output:</strong></p>
 * <pre>{@code
 * (x=8, y=16)
 * }</pre>
 */
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
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

    /**
     * Reads user input from the console in a loop.
     * <p>
     * The method continuously reads mathematical expressions entered by the user,
     * adding them to the processing queue. It terminates when the user types "exit".
     */
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
        logger.debug("Processing finished. Exiting...");
        parserProcessor.stop();
        calculatorProcessor.stop();
    }
}