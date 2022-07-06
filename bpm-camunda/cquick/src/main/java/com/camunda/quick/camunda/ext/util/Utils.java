package com.camunda.quick.camunda.ext.util;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.RuntimeServiceImpl;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.camunda.bpm.engine.repository.ProcessDefinition;

/**
 * 〈〉
 *
 * @author bob
 * @create 2021/7/16
 */
public class Utils {

    private TaskService taskService;
    private RepositoryService repositoryService;
    private ProcessEngine processEngine;

    private ActivityImpl findActivitiImpl(String taskId, String activityId) throws Exception {

        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();

        // 取得流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId())
                .singleResult();

        // 获取当前活动节点ID
        if (activityId == null) {
            activityId = task.getTaskDefinitionKey();
        }

        ((RuntimeServiceImpl)processEngine.getRuntimeService()).getCommandExecutor().execute(commandContext ->  {

            String ab = "A";
            return null;
        });

        // 根据节点ID，获取对应的活动节点
        return ((ProcessDefinitionImpl) processDefinition).findActivity(activityId);
    }
}
