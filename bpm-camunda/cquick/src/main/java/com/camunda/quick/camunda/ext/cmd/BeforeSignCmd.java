package com.camunda.quick.camunda.ext.cmd;

import com.camunda.quick.camunda.ext.util.ActivityManagerUtil;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.impl.ProcessEngineLogger;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.db.EnginePersistenceLogger;
import org.camunda.bpm.engine.impl.el.ExpressionManager;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.camunda.bpm.engine.impl.pvm.process.TransitionImpl;
import org.camunda.bpm.engine.impl.pvm.runtime.operation.PvmAtomicOperation;
import org.camunda.bpm.engine.impl.task.TaskDecorator;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class BeforeSignCmd extends BaseCmd implements Command<TaskEntity> {

    private Logger log = LoggerFactory.getLogger(BeforeSignCmd.class);
    protected static final EnginePersistenceLogger LOG = ProcessEngineLogger.PERSISTENCE_LOGGER;

    public static final String DELETE_REASON_DELETED = "before_sign_action";

    private String taskId;
    private Map<String, Object> variables;
    private String assignee;
    private String signType;

    public BeforeSignCmd(String taskId, Map<String, Object> variables, String signType, String assignee) {
        setAssignee(assignee);
        setTaskId(taskId);
        setVariables(variables);
        setSignType(signType);
    }

    @Override
    public TaskEntity execute(CommandContext commandContext) {
        log.info("BeforeSignCmd:{},", this.taskId);
        TaskEntity task = commandContext.getTaskManager().findTaskById(taskId);

        if (task == null) {
            throw new IllegalArgumentException("Cannot find task with id " + taskId);
        }

        if (task.isSuspended()) {
            throw new IllegalArgumentException(getSuspendedTaskException());
        }
        // ??????????????????,???????????????,???????????????????????????.
        task.setVariableLocal("endByBeforeSign", "true");
        log.info("find current task taskId:{},taskKey:{}", task.getId(), task.getName());
        ExecutionEntity execution = completeTaskOnlyNotLeave(task, DELETE_REASON_DELETED, this.getVariables());
        execution.dispatchDelayedEventsAndPerformOperation(PvmAtomicOperation.FIRE_ACTIVITY_END);
        ProcessDefinitionImpl processDefinition = task.getExecution().getProcessDefinition();
        //??????????????????????????????????????????????????????????????????????????????????????????Task?????????????????????
        ActivityImpl desactiviti = findDestinationActivity(task, processDefinition);

        leave(execution, processDefinition, desactiviti, signType);

        if (execution.getTasks() != null && !execution.getTasks().isEmpty()) {
            return execution.getTasks().get(0);
        } else {
            return null;
        }
    }

    private void leave(ExecutionEntity execution, ProcessDefinitionImpl processDefinition, ActivityImpl desactiviti, String signType) {
        //construct new activiti as the insert task which refer to
        ActivityImpl newactivity = cloneBeforeSign(desactiviti, processDefinition, signType);
        if (newactivity != null) {
            log.info("clone activiti:{}", newactivity.toString());
            execution.setActivity(newactivity);
            //this taskentity refter to destination activity
            execution.setVariable(ActivityManagerUtil.getDestinationActivityIdName(newactivity.getId()), desactiviti.getId());
            //this taskentity refter to current activity
            execution.setVariable(ActivityManagerUtil.getCurrentActivityIdName(newactivity.getId()), newactivity.getId());
            //this para store activiti`s assigen
            execution.setVariable(ActivityManagerUtil.getCurrentActivityAssigneeName(newactivity.getId()), this.assignee);
            execution.performOperation(PvmAtomicOperation.ACTIVITY_START);
        }
    }

    // temporary construct sign activiti???
    private ActivityImpl cloneBeforeSign(ActivityImpl desactiviti, ProcessDefinitionImpl processDefinition, String signType) {
        String beforeId = desactiviti.getId();
        String activityId = signType + "@" + beforeId + "@" + System.currentTimeMillis();
        ActivityImpl tmp = processDefinition.createActivity(activityId);

        TaskDefinition definitions = new TaskDefinition(null);
        definitions.setKey(activityId);
        Expression nameExpression = new FixedValue(beforeId + "@" + signType);
        definitions.setNameExpression(nameExpression);
        Expression assigneeExpression = new FixedValue(this.assignee);
        definitions.setAssigneeExpression(assigneeExpression);
        ExpressionManager expressionManager = Context.getProcessEngineConfiguration().getExpressionManager();

        tmp.setActivityBehavior(new UserTaskActivityBehavior(new TaskDecorator(definitions, expressionManager)));
        TransitionImpl transition = tmp.createOutgoingTransition();
        transition.setDestination(desactiviti);
        ActivityManagerUtil.getInstance().cache(tmp);
        return tmp;
    }

    /**
     * ?????????????????????????????????????????????
     */
    private ActivityImpl findDestinationActivity(TaskEntity task, ProcessDefinitionImpl processDefinition) {
        String definitionKey = task.getTaskDefinitionKey();
        //insert before task action ,this task refter to activity is  destination activity
        //?????????????????????????????????????????????
        ActivityImpl desactiviti = processDefinition.findActivity(definitionKey);
        if (desactiviti == null) {
            //??????????????????????????????null??????????????????????????????????????????????????????????????????????????????????????????
            //???????????????????????????????????????
            desactiviti = onlyCloneActivity(definitionKey, processDefinition);
        }
        return desactiviti;
    }

    @Override
    protected String getSuspendedTaskException() {
        return "Cannot execute operation: task is suspended";
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }
}
