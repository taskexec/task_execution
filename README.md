# task_execution

Add dependency in pom.xml
```
<dependency>
	<groupId>org.task.execution.framework</groupId>
	<artifactId>org.task.execution.framework</artifactId>
	<version>1.0.0</version>
</dependency>

public static void main(String[] args) {
        TaskScheduler ts = new TaskScheduler(ExampleOpt.class, args);
        ts.scheduleTask();
}

```

![image](https://user-images.githubusercontent.com/73499442/188876455-c8ff2dee-f346-4bf6-9dd9-9ac3bdcd1f41.png)

![image](https://user-images.githubusercontent.com/73499442/188876543-b4f4db0a-11fe-4eb4-ad5e-473c6c42dc06.png)

