package com.camunda.quick.camunda.ext.cmd;

import org.apache.commons.lang3.tuple.Triple;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.ProcessEngineLogger;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.db.EnginePersistenceLogger;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.SuspensionState;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.camunda.bpm.engine.impl.task.TaskDecorator;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author bob
 */
public class BaseCmd {

    protected Logger log = LoggerFactory.getLogger(getClass());

    private boolean skipCustomListeners = true;

    protected static final EnginePersistenceLogger LOG = ProcessEngineLogger.PERSISTENCE_LOGGER;

    /**
     * 返回 taskId 所在的相关信息，ActivityImpl 有可能为null(可能是加签加出来的)
     *
     * @param taskId
     * @param commandContext
     * @return
     */
    protected Triple<TaskEntity, ExecutionEntity, ActivityImpl> findObjectByTaskId(String taskId, CommandContext commandContext) {

        TaskEntity task = commandContext.getTaskManager().findTaskById(taskId);
        if (task == null) {
            throw new RuntimeException("流程任务[" + taskId + "]实例未找到");
        }

        ExecutionEntity executionEntity = task.getExecution();
        if (executionEntity == null) {
            throw new RuntimeException("流程实例未找到");
        }

        ProcessDefinitionEntity processDefinitionEntity = executionEntity.getProcessDefinition();
        ActivityImpl taskActivity = processDefinitionEntity.findActivity(task.getTaskDefinitionKey());

        return Triple.of(task, executionEntity, taskActivity);
    }

    /**
     * 完成这个任务
     *
     * @param task
     * @param deleteReasonDeleted
     * @param variables
     * @return
     */
    protected ExecutionEntity completeTaskOnlyNotLeave(TaskEntity task, String deleteReasonDeleted, Map<String, Object> variables) {

        log.info("complete : [{}] ,reason : [{}]", task.getId(), deleteReasonDeleted);

        /**
         * if the task is associated with a case execution then call complete on the
         * associated case execution. The case execution handles the completion of the task.
         * */
        if (task.getCaseExecutionId() != null) {
            task.getCaseExecution().manualComplete();
            return task.getExecution();
        }

        /**
         * in the other case:
         *  ensure the the Task is not suspended
         * */
        if (task.getSuspensionState() == SuspensionState.SUSPENDED.getStateCode()) {
            throw LOG.suspendedEntityException("task", task.getId());
        }

        // trigger TaskListener.complete event
        task.fireEvent(TaskListener.EVENTNAME_COMPLETE);

        // delete the task,this is for historyActivityInstance
        Context.getCommandContext().getTaskManager().deleteTask(task, deleteReasonDeleted, false, skipCustomListeners);

        /**
         * if the task is associated with a  execution (and not a case execution)
         *  then call signal an the associated  execution.
         * */
        if (task.getExecutionId() != null) {
            ExecutionEntity execution = task.getExecution();
            execution.removeTask(task);
            return execution;
        }

        return null;
    }

    /**
     * 根据活动标识（activityId）克隆出来一个活动定义
     *
     * @param activityId
     * @param processDefinition
     * @return
     */
    protected ActivityImpl onlyCloneActivity(String activityId, ProcessDefinitionImpl processDefinition) {
        ActivityImpl tmp = processDefinition.createActivity(activityId);
        TaskDefinition definition = new TaskDefinition(null);
        definition.setKey(activityId);
        Expression nameExpression = new FixedValue(activityId);
        definition.setNameExpression(nameExpression);
        tmp.setActivityBehavior(new UserTaskActivityBehavior(new TaskDecorator(definition,
                Context.getProcessEngineConfiguration().getExpressionManager())));
        return tmp;
    }

    protected String getSuspendedTaskException() {
        return "Cannot execute operation: task is suspended";
    }
}
