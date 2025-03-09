package com.calculator.services;

import com.calculator.models.Expression;
import com.calculator.utils.ExpressionParser;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The {@code ExpressionParserProcessor} class is responsible for processing
 * mathematical expressions in a background thread. It reads expressions
 * from an input queue, parses them into {@link Expression} objects,
 * and places them in an output queue.
 * This class implements {@link IProcessor} and provides a mechanism to start
 * and stop processing asynchronously using a worker thread.
 */
public class ExpressionParserProcessor implements IProcessor {

    private static final Logger logger = LogManager.getLogger(ExpressionParserProcessor.class);

    /**
     * The output queue that stores parsed {@link Expression} objects.
     */
    private static final BlockingQueue<Expression> outputQueue = new LinkedBlockingQueue<>();

    /**
     * The input queue containing raw string expressions to be processed.
     */
    private final BlockingQueue<String> inputQueue;
    private final Thread workerThread;
    private volatile boolean isRunning = true;

    /**
     * Constructs a new {@code ExpressionParserProcessor} with the specified input queue.
     *
     * @param inputQueue the queue containing raw string expressions to be parsed
     */
    public ExpressionParserProcessor(BlockingQueue<String> inputQueue) {
        this.inputQueue = inputQueue;
        this.workerThread = new Thread(this::processQueue);
    }

    public BlockingQueue<Expression> getOutputQueue() {
        return outputQueue;
    }

    /**
     * Starts the processor by launching a worker thread that continuously
     * processes expressions from the input queue.
     */
    @Override
    public void start() {
        logger.debug("Starting ExpressionParserProcessor...");
        workerThread.start();
    }

    /**
     * Stops the processor by interrupting the worker thread and waiting for
     * it to terminate gracefully.
     */
    @Override
    public void stop() {
        logger.debug("Stopping ExpressionParserProcessor...");
        isRunning = false;
        workerThread.interrupt();
        try {
            workerThread.join();
            logger.debug("Worker thread stopped successfully.");
        } catch (InterruptedException e) {
            logger.error("Interrupted while stopping worker thread.", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Processes the input queue, parsing each expression and adding
     * the resulting {@link Expression} object to the output queue.
     * This method runs in a loop until the processor is stopped.
     * If no input is available within 1 second, the loop continues.
     */
    @Override
    public void processQueue() {
        logger.debug("Worker thread started. Waiting for expressions...");
        while (isRunning || !inputQueue.isEmpty()) {
            try {
                String expression = inputQueue.poll(1, TimeUnit.SECONDS);
                if (expression != null) {
                    try {
                        logger.debug("Received expression: {}", expression);
                        Expression parsedExpression = ExpressionParser.parse(expression);
                        outputQueue.offer(parsedExpression);
                        logger.debug("Processed expression: {}", parsedExpression);
                    } catch (Exception e) {
                        logger.error("Failed to process: {} {}", expression, e.getMessage());
                    }
                }
            } catch (InterruptedException e) {
                logger.debug("Worker thread interrupted. Stopping...");
                Thread.currentThread().interrupt();
            }
        }
    }

}
