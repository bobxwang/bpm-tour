package com.camunda.quick.camunda.ext;

import com.camunda.quick.camunda.ext.cmd.*;
import com.google.common.collect.Maps;
import org.camunda.bpm.engine.impl.TaskServiceImpl;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;

import java.util.List;
import java.util.Map;

public class CustomTaskServiceImpl extends TaskServiceImpl {

    /**
     * @param taskId    当前任务的taskId
     * @param variables 参数
     * @return 后退的过程中，产生新的任务
     */
    public List<TaskEntity> back(String taskId, Map<String, Object> variables) {
        return commandExecutor.execute(new BackTaskCmd(taskId, variables));
    }

    /**
     * 前加签
     *
     * @param taskId
     * @param variables
     * @param signType
     * @param assignee
     * @return
     */
    public TaskEntity beforeSign(String taskId, Map<String, Object> variables, String signType, String assignee) {
        return commandExecutor.execute(new BeforeSignCmd(taskId, variables, signType, assignee));
    }

    /**
     * 后加签
     *
     * @param taskId
     * @param variables
     * @param assignee
     * @return
     */
    public TaskEntity afterSign(String taskId, Map<String, Object> variables, String assignee) {
        return commandExecutor.execute(new AfterSignCmd(taskId, variables, assignee));
    }

    /**
     * 自由跳转
     *
     * @param taskId
     * @param activityId
     * @param variables
     * @return
     */
    public TaskEntity move(String taskId, String activityId, Map<String, Object> variables) {
        return commandExecutor.execute(new MoveTowarsCmd(taskId, activityId, variables));
    }

    /**
     * 自由跳转
     *
     * @param current    当前节点所产生的任务
     * @param toActivity 目标节点
     * @return
     */
    public TaskEntity move(TaskEntity current, ActivityImpl toActivity) {
        return commandExecutor.execute(new MoveTowarsCmd(current.getId(), toActivity.getId(), null));
    }

    @Override
    public void complete(String taskId) {
        Map<String, Object> variables = Maps.newHashMap();
        commandExecutor.execute(new CusTomCompleteTaskCmd(taskId, variables));
    }

    @Override
    public void complete(String taskId, Map<String, Object> variables) {
        commandExecutor.execute(new CusTomCompleteTaskCmd(taskId, variables));
    }

    // 会签节点加人
    public void addMulti(String taskId, String assignee, List<String> assingeeList) {

        commandExecutor.execute(new AddMultiInstanceExecutionCmd(taskId, assignee, assingeeList));
    }
}
