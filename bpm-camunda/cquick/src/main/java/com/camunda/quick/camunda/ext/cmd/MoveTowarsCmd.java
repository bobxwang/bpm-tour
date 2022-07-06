package com.camunda.quick.camunda.ext.cmd;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.cfg.CommandChecker;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionManager;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.camunda.bpm.engine.impl.pvm.runtime.operation.PvmAtomicOperation;

import java.util.List;
import java.util.Map;

import static org.camunda.bpm.engine.impl.util.EnsureUtil.ensureNotNull;

/**
 * @author bob
 */
@Slf4j
public class MoveTowarsCmd extends BaseCmd implements Command<TaskEntity> {

    public static final String DELETE_REASON_DELETED = "move_towards_action";

    private String taskId;
    private String activityId;
    private Map<String, Object> variables;

    /**
     * @param taskId     当前任务标识
     * @param activityId 目标节点
     * @param variables  变量
     */
    public MoveTowarsCmd(String taskId, String activityId, Map<String, Object> variables) {
        this.taskId = taskId;
        this.activityId = activityId;
        this.variables = variables;
    }

    @Override
    public TaskEntity execute(CommandContext commandContext) {

        TaskEntity taskEntity = commandContext.getTaskManager().findTaskById(taskId);
        ensureNotNull("Cannot find task with id [" + taskId + "]", "task", taskEntity);
        checkCompleteTask(taskEntity, commandContext);

        log.info("find current task taskId:[{}],taskKey:[{}]", taskEntity.getId(), taskEntity.getName());
        if (variables != null) {
            taskEntity.setExecutionVariables(variables);
        }

        ExecutionEntity currentExecution = taskEntity.getExecution();
        ProcessDefinitionImpl processDefinition = currentExecution.getProcessDefinition();
        ActivityImpl targetActivity = processDefinition.findActivity(this.activityId);
        ensureNotNull("Cannot find target node with id [" + this.activityId + "]", "task", targetActivity);

        List<TaskEntity> newTasks;
        ActivityImpl currentActivity = processDefinition.findActivity(taskEntity.getTaskDefinitionKey());

        if (currentExecution.isProcessInstanceExecution()) {
            // 说明是同一个 Execution_ID_

            taskEntity.fireEvent(TaskListener.EVENTNAME_COMPLETE);
            Context.getCommandContext().getTaskManager().deleteTask(taskEntity, DELETE_REASON_DELETED, false, false);
            currentExecution.setActivity(targetActivity);
            currentExecution.performOperation(PvmAtomicOperation.ACTIVITY_START);
            if (currentActivity != null) {
                // 有可能为 null 的原因是因为它是加签加出来的节点
                currentExecution.dispatchDelayedEventsAndPerformOperation(PvmAtomicOperation.FIRE_ACTIVITY_END);
            }
            currentExecution.removeTask(taskEntity);
            newTasks = currentExecution.getTasks();
        } else {
            // 说明是会签或有并行网关，出现了分叉

            // 要判断目标节点跟当前节点是不是在网关内的同一条线上或目标节点已经在网关外

            final ExecutionManager executionManager = commandContext.getExecutionManager();
            ExecutionEntity parentExecutionEntity = executionManager.findExecutionById(currentExecution.getParentId());
            // 根据父找到其所有子执行流程节点
            List<ExecutionEntity> childExecutionEntities = executionManager
                    .findChildExecutionsByParentExecutionId(parentExecutionEntity.getId());
            // 设置需要删除参数的流程实例
            for (ExecutionEntity childExecutionEntity : childExecutionEntities) {
                executionManager.deleteProcessInstance(childExecutionEntity.getId(), DELETE_REASON_DELETED, false, false);
            }
            parentExecutionEntity.setActivity(targetActivity);
            parentExecutionEntity.performOperation(PvmAtomicOperation.ACTIVITY_START);
            if (currentActivity != null) {
                // 有可能为 null 的原因是因为它是加签加出来的节点
                parentExecutionEntity.dispatchDelayedEventsAndPerformOperation(PvmAtomicOperation.FIRE_ACTIVITY_END);
            }
            parentExecutionEntity.removeTask(taskEntity);
            newTasks = parentExecutionEntity.getTasks();
        }

        if (newTasks.size() > 0) {
            return newTasks.get(0);
        } else {
            return null;
        }
    }

    protected void checkCompleteTask(TaskEntity task, CommandContext commandContext) {
        for (CommandChecker checker : commandContext.getProcessEngineConfiguration().getCommandCheckers()) {
            checker.checkTaskWork(task);
        }
    }
}
