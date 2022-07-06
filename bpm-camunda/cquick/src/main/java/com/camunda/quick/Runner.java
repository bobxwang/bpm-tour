package com.camunda.quick;

import com.camunda.quick.camunda.ext.CustomTaskServiceImpl;
import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import springfox.documentation.oas.annotations.EnableOpenApi;

import java.sql.ResultSet;
import java.util.List;

/**
 * 〈〉
 *
 * @author bob
 * @create 2020/9/3
 */
@SpringBootApplication(proxyBeanMethods = false)
@EnableOpenApi
public class Runner {

    private String formatter = "yyyy-MM-dd'T'HH:mm:ss";

    private Logger logger = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args) {

        SpringApplication.run(Runner.class);
    }

    @Bean
    public CommandLineRunner init(final RepositoryService repositoryService,
                                  final RuntimeService runtimeService,
                                  final IdentityService identityService,
                                  final HistoryService historyService,
                                  final ManagementService managementService,
                                  final TaskService taskService,
                                  final JdbcTemplate jdbcTemplate) {

        CustomTaskServiceImpl customTaskService = (CustomTaskServiceImpl) taskService;

        return (String... strings) -> {

            if (1 > 5) {
                customTaskService.move("d4c4a7dd-e47b-11eb-9cc1-167c690843de", "_4", null);
                return;
            }

            if (1 > 5) {
                String taskId = "c284d99e-f8fe-11eb-9d55-167c690843de";
                java.util.Map<String, Object> map = Maps.newHashMap();
                List<String> assigneeList = Lists.newArrayList();
                assigneeList.add("a");
                assigneeList.add("b");
                assigneeList.add("c");
                map.put("assigneeList", assigneeList);
                customTaskService.complete(taskId, map);
                return;
            }

            if (1 > 8) {
                String taskId = "98f29299-f5bd-11eb-804d-167c690843de";
                TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
                ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
                        .getProcessDefinition(taskEntity.getProcessDefinitionId());
                ActivityImpl toActivity = processDefinition.findActivity("usertask4");
                if (toActivity != null) {
                    customTaskService.move(taskEntity, toActivity);
                }
            }

            if (1 > 2) {
                String taskId = "e8cf1870-f8fe-11eb-a208-167c690843de";
                List<String> abcd = Lists.newArrayList();
                abcd.add("ab");
                abcd.add("bc");
                abcd.add("cd");
                customTaskService.addMulti(taskId, "xwang", abcd);
            }

            if (1 > 2) {

                List<String> assigneeL = Lists.newArrayList();
                assigneeL.add("ab");
                assigneeL.add("bc");
                assigneeL.add("cd");
                String processInstanceId = "8c775611-f503-11eb-ac22-167c690843de";
                runtimeService.createProcessInstanceModification(processInstanceId)
                        .startBeforeActivity("usertask3")
                        .setVariable("assignee", assigneeL.get(0))
                        .execute();
                return;
            }

            String processKey = "Process_0amx1ap1";
            ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
                    .createProcessDefinitionQuery()
                    .processDefinitionKey(processKey)
                    .singleResult();
            if (processDefinition == null) {
                logger.info("can not find the process key [{}]", processKey);
                return;
            }

            if (1 > 3) {
                startProcess(processKey, identityService, runtimeService);
            }

            if (1 > 4) {
                String taskId = "c2c3b3d7-f448-11eb-ad5f-167c690843de";
                TaskEntity taskEntity = customTaskService.beforeSign(taskId, null, "before", "demo");
                logger.info("before assign [{}]", taskEntity);
                return;
            }

            List<Task> tasks = taskService.createTaskQuery().withCandidateUsers().taskCandidateUser("demo").includeAssignedTasks().list();
            tasks.forEach(x -> {
                List<String> users = jdbcTemplate.query("select USER_ID_ from ACT_RU_IDENTITYLINK where TASK_ID_ = '" + x.getId() + "'",
                        (ResultSet rs, int i) -> rs.getString(1)
                );
                logger.info("current task [{}], assignee to [{}], identity user [{}]", x, x.getAssignee(), users);
            });
        };
    }

    private void startProcess(String processKey,
                              final IdentityService identityService,
                              final RuntimeService runtimeService) {

        // 设置此值相当于设置是谁提交的流程
        identityService.setAuthenticatedUserId("demo");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey);

        logger.info("开启一个流程实例 [{}]", processInstance);
    }

    @SuppressWarnings("unused")
    private void learningCode(final TaskService taskService, final ManagementService managementService, final RuntimeService runtimeService) {

        ActivityInstance rootActivityInstance = runtimeService.getActivityInstance("adsfasda");
        rootActivityInstance.getExecutionIds();

        String table = managementService.getTableName(HistoricProcessInstance.class);
        System.out.println("HistoricProcessInstance table name is: " + table);
        table = managementService.getTableName(HistoricActivityInstance.class);
        System.out.println("HistoricActivityInstance table name is: " + table);
        taskService.createComment("taskId", "", "just a test");
    }
}
