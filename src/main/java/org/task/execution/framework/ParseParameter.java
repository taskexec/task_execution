package org.task.execution.framework;

import com.beust.jcommander.JCommander;
import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author Yong Tan
 */
public class ParseParameter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParseParameter.class);

    public String getValue(String parameter, Class<?> optionClazz, String[] args) {
        Object cliOption = null;
        try {
            cliOption = optionClazz.newInstance();
        } catch (Exception e) {
            LOGGER.error("Failed to new " + optionClazz.getClass().getSimpleName(), e);
        }
        JCommander jc = new JCommander(cliOption);
        jc.parse(args);
        Object varValue = null;
        Field[] fields = optionClazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(parameter)) {
                try {
                    varValue = field.get(cliOption);
                } catch (Exception e) {
                    LOGGER.error("Failed to get parameter: " + field.getName(), e);
                    varValue = null;
                }
            }
        }
        return (varValue == null) ? null : varValue.toString();
    }

    public String getBean(TaskScheduler taskScheduler, Class<?> optionClazz, String[] args) {
        Object cliOption = null;
        try {
            cliOption = optionClazz.newInstance();
        } catch (Exception e) {
            LOGGER.error("Failed to new " + optionClazz.getClass().getSimpleName(), e);
        }
        JCommander jc = new JCommander(cliOption);
        jc.parse(args);
        String bean = null;
        Field[] fields = optionClazz.getDeclaredFields();
        for (Field field : fields) {
            Object varValue = null;
            try {
                varValue = field.get(cliOption);
            } catch (Exception e) {
                LOGGER.error("Failed to get cli option field: " + field.getName(), e);
                return null;
            }
            if (varValue != null) {
                taskScheduler.setData(field.getName(), varValue);
                if (field.getName().equals("bean")) {
                    bean = varValue.toString();
                }
            }
        }
        return bean;
    }
}
