package com.calculator.services;

import com.calculator.models.Expression;
import com.calculator.utils.ExpressionParser;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExpressionParserProcessor implements IProcessor {

    private static final Logger logger = LogManager.getLogger(ExpressionParserProcessor.class);
    private static final BlockingQueue<Expression> outputQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<String> inputQueue;
    private final Thread workerThread;
    private volatile boolean isRunning = true;

    public ExpressionParserProcessor(BlockingQueue<String> inputQueue) {
        this.inputQueue = inputQueue;
        this.workerThread = new Thread(this::processQueue);
    }

    public BlockingQueue<Expression> getOutputQueue() {
        return outputQueue;
    }

    @Override
    public void start() {
        logger.debug("Starting ExpressionParserProcessor...");
        workerThread.start();
    }

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
