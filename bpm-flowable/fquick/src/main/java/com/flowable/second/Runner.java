package com.flowable.second;

import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.task.api.Task;
import org.flowable.variable.service.impl.persistence.entity.VariableInstanceEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

/**
 * 〈〉
 *
 * @author bob
 * @create 2020/9/3
 */
@SpringBootApplication(proxyBeanMethods = false)
@EnableSwagger2
public class Runner {

    public static void main(String[] args) {

        SpringApplication.run(Runner.class);
    }

    @Bean
    public CommandLineRunner init(final RepositoryService repositoryService,
                                  final RuntimeService runtimeService,
                                  final TaskService taskService) {

        return (String... strings) -> {

            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
            RepositoryService repositoryServiceT = processEngine.getRepositoryService();
            TaskService taskServiceT = processEngine.getTaskService();
            RuntimeService runtimeServiceT = processEngine.getRuntimeService();
            ManagementService managementService = processEngine.getManagementService();

            RepositoryService rs = repositoryService;
            RuntimeService rt = runtimeService;
            TaskService ts = taskService;

            String processKey = "oneTaskProcess";
            Deployment deployment = repositoryService.createDeploymentQuery()
                    .processDefinitionKey(processKey).singleResult();
            if (deployment != null) {

//                ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey);
//                System.out.println("成功启动一个流程实例 [ " + processInstance.getProcessDefinitionName() + " ] " + processInstance);

                System.out.println("using query ");
                List<Task> tasks = taskService.createTaskQuery().list();
                tasks.forEach(x -> {
                    System.out.println(x.getClass());
                    System.out.println(x);
                });

                System.out.println("using native query ");
                tasks = taskService.createNativeTaskQuery()
                        .sql("SELECT count(*) FROM " + managementService.getTableName(Task.class) +
                                " T WHERE T.NAME_ = #{taskName}")
                        .parameter("taskName", "my task")
                        .list();
                if (!CollectionUtils.isEmpty(tasks)) {
                    tasks.forEach(x -> {
                        // 奇葩，不知道为啥，会返回一个 list 不为空，但里面记录是空的 list，后续看看发往数据库的真实 sql
                        if (x != null) {
                            System.out.println(x.getClass());
                            System.out.println(x);
                        }
                    });
                }

                long count = taskService.createNativeTaskQuery()
                        .sql("SELECT count(*) FROM " + managementService.getTableName(Task.class) + " T1, " +
                                managementService.getTableName(VariableInstanceEntity.class) + " V1 WHERE V1.TASK_ID_ = T1.ID_")
                        .count();
                System.out.println("count --> " + count);
            }

//                System.out.println("Number of process definitions : "
//                        + repositoryService.createProcessDefinitionQuery().count());
//                System.out.println("Number of tasks : " + taskService.createTaskQuery().count());
//                runtimeService.startProcessInstanceByKey("oneTaskProcess");
//                System.out.println("Number of tasks after process start: "
//                        + taskService.createTaskQuery().count());

        };
    }
}
