package org.task.execution.framework;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author Yong Tan
 */
public class SequenceTask extends Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceTask.class);

    private List<Task> subtasks;

    public ExecutionResult call() throws Exception {
        this.execution_result = ExecutionResult.SUCCESS;
        for (Task task : this.subtasks) {
            if (this.execution_result == ExecutionResult.FAILURE && task.isMandatory()) {
                LOGGER.info("Skipped the left mandatory task: " + task.getClass().getSimpleName());
                continue;
            }
            ExecutionResult resultTmp = task.executeTask(this.task_scheduler);
            if (task.isMandatory() && resultTmp != ExecutionResult.SUCCESS) {
                this.execution_result = ExecutionResult.FAILURE;
                LOGGER.error("Failed to execute: " + task.getClass().getSimpleName());
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