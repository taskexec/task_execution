package org.task.execution.framework;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
/**
 * @author Yong Tan
 */
public class ConditionTask extends Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceTask.class);
    private String condition;
    private List<Task> iftasks;
    private List<Task> elsetasks;

    private boolean evaluateCondition() {
        StandardEvaluationContext context = new StandardEvaluationContext(this);
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        boolean result = ((Boolean) spelExpressionParser.parseExpression(this.condition).getValue((EvaluationContext) context, Boolean.class)).booleanValue();
        return result;
    }

    public Object getData(String key) {
        return super.getData(key);
    }

    public ExecutionResult call() throws Exception {
        this.execution_result = ExecutionResult.SUCCESS;
        List<Task> subTasks = evaluateCondition() ? this.iftasks : this.elsetasks;
        if (subTasks != null) {
            for (Task task : subTasks) {
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
        }
        return this.execution_result;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return this.condition;
    }

    public void setIftasks(List<Task> ifTasks) {
        this.iftasks = ifTasks;
    }

    public List<Task> getIftasks() {
        return this.iftasks;
    }

    public void setElsetasks(List<Task> elseTasks) {
        this.elsetasks = elseTasks;
    }

    public List<Task> getElsetasks() {
        return this.elsetasks;
    }
}
