package com.camunda.quick.camunda.ext;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.task.IdentityLinkType;
import org.springframework.util.CollectionUtils;

import java.util.stream.Collectors;

/**
 * 〈〉
 *
 * @author bob
 * @create 2020/9/17
 */
@Slf4j
public class InformAssigneeTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {

        StringBuilder sb = new StringBuilder("\r\n");
        sb.append("任务事件: [" + delegateTask.getEventName() + "]\r\n");
        sb.append("流程实例: [" + delegateTask.getProcessInstanceId() + "]\r\n");
        sb.append("代理人: [" + delegateTask.getAssignee() + "]\r\n");
        sb.append("任务名称：[" + delegateTask.getName() + "]\r\n");
        if (!CollectionUtils.isEmpty(delegateTask.getCandidates())) {
            sb.append("参与者: [");
            sb.append(StringUtils.join(delegateTask.getCandidates().stream()
                    .filter(x -> x.getType().contentEquals(IdentityLinkType.CANDIDATE))
                    .map(x -> x.getUserId())
                    .collect(Collectors.toList()), ","));
            sb.append("]\r\n");
        }
        if (!CollectionUtils.isEmpty(delegateTask.getVariables())) {
            sb.append("任务参数: [");
            sb.append(delegateTask.getVariables());
            sb.append("]\r\n");
        }
        sb.append("截止时间: [" + delegateTask.getDueDate() + "]\r\n");

        log.info(sb.toString());
    }
}
