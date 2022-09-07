package org.task.execution.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author Yong Tan
 */
public class ParallelTask extends Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelTask.class);

    private List<Task> subtasks;

    public ExecutionResult call() throws Exception {
        this.execution_result = ExecutionResult.SUCCESS;
        if (this.subtasks == null || this.subtasks.isEmpty()) {
            return this.execution_result;
        }
        Map<Task, Future<ExecutionResult>> futureList = new HashMap<Task, Future<ExecutionResult>>();
        Map<Task, ExecutionResult> executionList = new HashMap<Task, ExecutionResult>();
        ExecutorService executor = Executors.newFixedThreadPool(this.subtasks.size());
        for (final Task task : this.subtasks) {
            Future<ExecutionResult> future = executor.submit(new Callable<ExecutionResult>() {
                public ExecutionResult call() throws Exception {
                    return task.executeTask(ParallelTask.this.task_scheduler);
                }
            });
            futureList.put(task, future);
        }

        try {
            for (Map.Entry<Task, Future<ExecutionResult>> entry : futureList.entrySet()) {
                ExecutionResult resultTmp = ((Future<ExecutionResult>) entry.getValue()).get();
                executionList.put(entry.getKey(), resultTmp);
            }
        } catch (Exception e) {
            LOGGER.error("It should never happen here", e);
            this.execution_result = ExecutionResult.FAILURE;
        } finally {
            executor.shutdown();
        }

        for (Map.Entry<Task, ExecutionResult> entry : executionList.entrySet()) {
            if (((Task) entry.getKey()).mandatory && entry.getValue() != ExecutionResult.SUCCESS) {
                LOGGER.error("The mandatory task was failed: " + ((Task) entry.getKey()).getClass().getSimpleName());
                this.execution_result = entry.getValue();
                break;
            }
        }
        return this.execution_result;
    }

    public void setSubtasks(List<Task> subtasks) {
        this.subtasks = subtasks;
    }

    public List<Task> getSubtasks() {
        return this.subtasks;
    }
}