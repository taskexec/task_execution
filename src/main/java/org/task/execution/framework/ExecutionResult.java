package org.task.execution.framework;
/**
 * @author Yong Tan
 */
public enum ExecutionResult
{
  SUCCESS(0), FAILURE(1), SKIPPED(2), IGNORED(3), TIMEOUT(4);
  private int value;
  
  ExecutionResult(int value) {
    this.value = value;
  }
  
  public int getValue() {
    return this.value;
  }
}