package com.camunda.quick.camunda.ext.cmd;

import com.camunda.quick.Runner;
import com.camunda.quick.camunda.ext.CustomTaskServiceImpl;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 〈〉
 *
 * @author bob
 * @create 2021/7/12
 */
@SpringBootTest(classes = {Runner.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class CmdTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    private CustomTaskServiceImpl customTaskService;

    @Autowired
    private HistoryService historyService;

    @Before
    public void initEach() {
        this.customTaskService = (CustomTaskServiceImpl) taskService;
    }

    @Test
    public void testBack() {

//        String taskId = "72f6e6e4-ee75-11eb-a55e-167c690843de";
//        List<TaskEntity> tasks = customTaskService.back(taskId, null);
//        for (TaskEntity task : tasks) {
//            System.out.println(task);
//        }

//        runtimeService.createProcessInstanceModification("67cd1639-ee75-11eb-a55e-167c690843de")
//                .cancelActivityInstance("_30:2bbfac2e-ee78-11eb-a514-167c690843de")
//                .startBeforeActivity("_5")
//                .execute();

//        TaskEntity taskEntity = customTaskService
//                .beforeSign("2920d117-edd7-11eb-92d2-167c690843de", null, "addBeforeSign", null);
//        System.out.println(taskEntity.getId());


        customTaskService.complete("5d37321d-f0e4-11eb-8759-167c690843de");

        System.out.println("abcd");
    }


    @Test
    public void testJump() {

        TaskEntity taskEntity = (TaskEntity) customTaskService.createTaskQuery().taskId("ff7a1950-f5c0-11eb-9bcb-167c690843de").singleResult();
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
                .getProcessDefinition(taskEntity.getProcessDefinitionId());
        List<ActivityImpl> abcd = processDefinition.getActivities();
        ActivityImpl to = processDefinition.findActivity("usertask4");
        if (to == null) {
            return;
        }

        customTaskService.move(taskEntity, to);
    }

    @Test
    public void testBeforeAdd() {

        String taskId = "90339f3a-f5c0-11eb-bcd0-167c690843de";
        TaskEntity taskEntity = customTaskService.beforeSign(taskId, null, "addBefore", "demo");
        System.out.println(taskEntity.toString());
    }

}
