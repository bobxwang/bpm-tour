package com.camunda.quick.camunda.ext;

import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈〉
 *
 * @author bob
 * @create 2020/9/17
 */
public class InformAssigneeParseListenerPlugin extends AbstractProcessEnginePlugin {

    private InformAssigneeTaskListener informAssigneeTaskListener;

    @Autowired
    public InformAssigneeParseListenerPlugin(InformAssigneeTaskListener informAssigneeTaskListener) {

        this.informAssigneeTaskListener = informAssigneeTaskListener;
    }

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

        List<BpmnParseListener> preParseListeners = processEngineConfiguration.getCustomPreBPMNParseListeners();
        if (preParseListeners == null) {
            preParseListeners = new ArrayList<>();
            processEngineConfiguration.setCustomPreBPMNParseListeners(preParseListeners);
        }

        preParseListeners.add(new AbstractBpmnParseListener() {
            @Override
            public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {

                ActivityBehavior activityBehavior = activity.getActivityBehavior();
                if (activityBehavior instanceof UserTaskActivityBehavior) {
                    UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
                    TaskDefinition taskDefinition = userTaskActivityBehavior.getTaskDefinition();
                    taskDefinition.addTaskListener(TaskListener.EVENTNAME_CREATE, informAssigneeTaskListener);
                    taskDefinition.addTaskListener(TaskListener.EVENTNAME_ASSIGNMENT, informAssigneeTaskListener);
                }
            }
        });
    }
}
