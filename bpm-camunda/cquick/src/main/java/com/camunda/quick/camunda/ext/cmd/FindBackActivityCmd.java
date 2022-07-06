package com.camunda.quick.camunda.ext.cmd;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.PvmTransition;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.TransitionImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈〉
 *
 * @author bob
 * @create 2021/8/10
 */
@Slf4j
public class FindBackActivityCmd extends BaseCmd implements Command<List<ActivityImpl>> {

    private String taskId;

    public FindBackActivityCmd(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public List<ActivityImpl> execute(CommandContext commandContext) {

        Triple<TaskEntity, ExecutionEntity, ActivityImpl> o = findObjectByTaskId(taskId, commandContext);
        try {
            return iteratorBackActivity(commandContext, taskId, o.getRight(), Lists.newArrayList(), Lists.newArrayList());
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    private List<ActivityImpl> iteratorBackActivity(final CommandContext commandContext, String taskId, ActivityImpl currActivity,
                                                    List<ActivityImpl> rtnList, List<ActivityImpl> tempList) throws Exception {
        if (currActivity == null) {
            // 加减签加出来的节点
            return Lists.newArrayList();
        }

        //当前节点的流入来源
        List<PvmTransition> incomingTransitions = currActivity.getIncomingTransitions();
        //条件分支节点集合，userTask节点遍历完毕，迭代遍历此集合，查询条件分支对应的userTask节点
        List<ActivityImpl> exclusiveGateways = Lists.newArrayList();
        //并行节点集合，userTask节点遍历完毕，迭代遍历此集合，查询并行节点对应的userTask节点
        List<ActivityImpl> parallelGateways = Lists.newArrayList();

        //遍历当前节点所流入的路径
        for (PvmTransition pvmTransition : incomingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
            ActivityImpl activityImpl = transitionImpl.getSource();
            String type = (String) activityImpl.getProperty("type");

            if ("parallelGateway".equals(type)) {
                String gatewayId = activityImpl.getId();
                String gatewayType = gatewayId.substring(gatewayId.lastIndexOf("_") + 1);
                if (gatewayType.toUpperCase().equals("START")) {
                    //并行起点，停止递归
                    return rtnList;
                } else {
                    parallelGateways.add(activityImpl);
                }
            } else if (type.equals("startEvent")) {
                return rtnList;
            } else if (type.equals("userTask")) {
                tempList.add(activityImpl);
            } else if (type.equals("exclusiveGateway")) {
                // 分支路线，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
                currActivity = transitionImpl.getSource();
                exclusiveGateways.add(currActivity);
            }
        }

        return null;
    }

    private List<ActivityImpl> reverList(List<ActivityImpl> list) {

        List<ActivityImpl> rtnList = new ArrayList<>();
        // 由于迭代出现重复数据，排除重复
        for (int i = list.size(); i > 0; i--) {
            if (!rtnList.contains(list.get(i - 1))) {
                rtnList.add(list.get(i - 1));
            }
        }
        return rtnList;
    }
}
