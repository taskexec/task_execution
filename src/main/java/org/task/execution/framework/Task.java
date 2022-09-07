package org.task.execution.framework;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author Yong Tan
 */
public abstract class Task implements Callable<ExecutionResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);
    protected String description = "";
    public ExecutionResult execution_result;
    public boolean mandatory = true;
    public boolean optional = false;
    protected TaskScheduler task_scheduler = null;
    public int timeout = 0;
    public double timecost = -1.0D;

    public ExecutionResult executeTask(TaskScheduler taskScheduler) {
        LOGGER.info(getClass().getSimpleName() + " started.");
        ExecutorService executor = null;
        this.task_scheduler = taskScheduler;
        if (this.optional) {
            LOGGER.info(getClass().getSimpleName() + " is skipped.");
            return ExecutionResult.SKIPPED;
        }
        Future<ExecutionResult> future = null;
        long startTime = (new Date()).getTime();
        executor = Executors.newFixedThreadPool(1);
        try {
            future = executor.submit(this);
            if (this.timeout <= 0) {
                this.execution_result = future.get();
            } else {
                this.execution_result = future.get(this.timeout, TimeUnit.SECONDS);
            }
        } catch (TimeoutException e) {
            this.execution_result = ExecutionResult.TIMEOUT;
            if (future != null) {
                future.cancel(true);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to execute task:", e);
            this.execution_result = ExecutionResult.FAILURE;
        } finally {
            executor.shutdown();
        }
        this.timecost = ((new Date()).getTime() - startTime) / 1000.0D;
        printResult();
        return this.execution_result;
    }

    public void printResult() {
        LOGGER.info(getClass().getSimpleName() + " finished with " + this.execution_result + " Time cost: " + this.timecost);
    }

    protected Object getData(String key) {
        return this.task_scheduler.getData(key);
    }

    protected void setData(String key, Object value) {
        this.task_scheduler.setData(key, value);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOptinal() {
        return this.optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isMandatory() {
        return this.mandatory;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setMandatory(boolean mandotary) {
        this.mandatory = mandotary;
    }

    public ExecutionResult call() throws Exception {
        return ExecutionResult.SUCCESS;
    }
}
