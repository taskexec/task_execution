package org.task.execution.framework;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * @author Yong Tan
 */
public class TaskScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskScheduler.class);
    private Task task;
    private String bean;
    private Map<String, Object> task_data = new ConcurrentHashMap<String, Object>();
    private ParseParameter parseParameter = new ParseParameter();

    public TaskScheduler(Class<?> optionClazz, String[] args) {
        setData("optionClazz", optionClazz);
        setData("args", args);
        this.bean = this.parseParameter.getBean(this, optionClazz, args);
        loadBean();
    }

    public void loadBean() {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath:" + this.bean);
        this.task = (Task) classPathXmlApplicationContext.getBean(Task.class);
    }

    public ExecutionResult scheduleTask() {
        return this.task.executeTask(this);
    }

    public Object getData(String key) {
        return this.task_data.get(key);
    }

    public void setData(String key, Object value) {
        if (value != null)
            this.task_data.put(key, value);
    }
}