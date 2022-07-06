package com.camunda.quick.camunda.ext.cmd;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionManager;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.instance.MultiInstanceLoopCharacteristicsImpl;
import org.camunda.bpm.model.bpmn.impl.instance.UserTaskImpl;
import org.camunda.bpm.model.bpmn.instance.LoopCharacteristics;

import java.util.List;

/**
 * 〈〉
 *
 * @author bob
 * @create 2021/8/4
 */
@Slf4j
public class AddMultiInstanceExecutionCmd implements Command<String> {

    private String taskId;
    private String assignee;
    private List<String> assigneeList;

    public AddMultiInstanceExecutionCmd(String taskId, String assignee, List<String> assigneeList) {
        super();

        if (assigneeList == null || assigneeList.size() == 0) {
            throw new RuntimeException("assigneeList 参数不能为空!");
        }

        this.taskId = taskId;
        this.assignee = assignee;
        this.assigneeList = assigneeList;
    }

    @Override
    public String execute(CommandContext commandContext) {

        TaskEntity taskEntity = commandContext.getTaskManager().findTaskById(taskId);

        ProcessDefinitionEntity processDefinitionEntity = taskEntity.getProcessDefinition();
        BpmnModelInstance modelInstance = commandContext
                .getProcessEngineConfiguration()
                .getDeploymentCache()
                .findBpmnModelInstanceForProcessDefinition(processDefinitionEntity.getId());
        // 当前节点
        UserTaskImpl userTask = modelInstance.getModelElementById(taskEntity.getTaskDefinitionKey());
        LoopCharacteristics loopCharacteristics = userTask.getLoopCharacteristics();
        if (loopCharacteristics == null) {
            log.info("当前节点任务 [{}] 不是会签节点任务", this.taskId);
            return null;
        }

        if (!(loopCharacteristics instanceof MultiInstanceLoopCharacteristicsImpl)) {
            log.info("当前节点任务 [{}] 不是会签节点任务", this.taskId);
            return null;
        }

        MultiInstanceLoopCharacteristicsImpl multiInstanceLoopCharacteristics = (MultiInstanceLoopCharacteristicsImpl) loopCharacteristics;

        final RuntimeService runtimeService = commandContext.getProcessEngineConfiguration().getRuntimeService();

        // 会签不管是串行还是并行，都会有新的 Execution_Id_ 产生，即存在父
        ExecutionEntity parentExecution = taskEntity.getExecution().getParent();
        int nrOfInstance = (int) runtimeService.getVariable(parentExecution.getId(), MultiInstanceActivityBehavior.NUMBER_OF_INSTANCES);
        int nrOfActiveInstances = (int) runtimeService.getVariable(parentExecution.getId(), MultiInstanceActivityBehavior.NUMBER_OF_ACTIVE_INSTANCES);

        ExecutionManager executionManager = commandContext.getExecutionManager();

        ActivityImpl activity = processDefinitionEntity.findActivity(taskEntity.getTaskDefinitionKey());
        if (multiInstanceLoopCharacteristics.isSequential()) {
            // 串行会签
        } else {
            // 并行会签
            runtimeService.setVariable(parentExecution.getId(), MultiInstanceActivityBehavior.NUMBER_OF_INSTANCES, nrOfInstance + this.assigneeList.size());
            runtimeService.setVariable(parentExecution.getId(), MultiInstanceActivityBehavior.NUMBER_OF_ACTIVE_INSTANCES, nrOfActiveInstances + this.assigneeList.size());
            MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior)activity.getFlowScope()
                    .getActivityBehavior();
            for (String x : assigneeList) {
                ExecutionEntity child = parentExecution.createExecution();
                child.setActive(true);
                child.setVariableLocal(MultiInstanceActivityBehavior.LOOP_COUNTER, nrOfInstance);
                child.setVariableLocal("assignee", x);
                child.setActivity(activity);
                nrOfInstance = nrOfInstance + 1;
                try {
                    commandContext.getExecutionManager().insertExecution(child);
                } catch (Exception e) {
                    log.error("会签并行节点[{}]加签[{}]失败", this.taskId, x, e);
                }
            }
        }

        return null;
    }
}
