package com.calculator.services;

import com.calculator.models.Expression;
import com.calculator.utils.ExpressionParser;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressionParserProcessor implements IProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ExpressionParserProcessor.class);
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
        logger.info("Starting ExpressionParserProcessor...");
        workerThread.start();
    }

    @Override
    public void stop() {
        logger.info("Stopping ExpressionParserProcessor...");
        isRunning = false;
        workerThread.interrupt();
        try {
            workerThread.join();
            logger.info("Worker thread stopped successfully.");
        } catch (InterruptedException e) {
            logger.error("Interrupted while stopping worker thread.", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void processQueue() {
        logger.info("Worker thread started. Waiting for expressions...");
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
                        logger.error("Failed to process: {}", expression);
                    }
                }
            } catch (InterruptedException e) {
                logger.warn("Worker thread interrupted. Stopping...");
                Thread.currentThread().interrupt();
            }
        }
    }

}
