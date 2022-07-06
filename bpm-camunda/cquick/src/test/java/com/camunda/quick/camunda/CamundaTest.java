package com.camunda.quick.camunda;

import com.camunda.quick.Runner;
import com.camunda.quick.camunda.ext.CustomTaskServiceImpl;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 〈〉
 *
 * @author bob
 * @create 2021/9/9
 */
@SpringBootTest(classes = {Runner.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class CamundaTest {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskService taskService;

    private CustomTaskServiceImpl customTaskService;

    @Before
    public void initEach() {
        this.customTaskService = (CustomTaskServiceImpl) taskService;
    }

    @Test
    public void testReject() {

        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionName("Process_rejectStart")
                .singleResult();
        if (processDefinition == null) {
            return;
        }

        if (1 > 0) {
            return;
        }

        VariableMap variables = Variables.createVariables();
        // 设置申请人
        identityService.setAuthenticatedUserId("0185251");
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceById(processDefinition.getId(), variables);
        System.out.println(processInstance);

        Task task = customTaskService.createTaskQuery()
                .processInstanceId(processInstance.getProcessInstanceId())
                .singleResult();
        // 自动审批
        customTaskService.complete(task.getId());
    }
}
