package com.camunda.quick.camunda.ext.util;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

/**
 * 〈〉
 *
 * @author bob
 * @create 2021/7/14
 */
public class GraphUtilsTest {

    @Test
    public void testGraph() throws Exception {

        String bpm = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:camunda=\"http://camunda.org/schema/1.0/bpmn\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:tns=\"http://www.activiti.org/test\" typeLanguage=\"http://www.w3.org/2001/XMLSchema\" expressionLanguage=\"http://www.w3.org/1999/XPath\" targetNamespace=\"http://www.activiti.org/test\" id=\"m1569319529879\" name=\"\">\n" +
                "    <process id=\"parallel-gateway\" name=\"Student Leave Process5\" isExecutable=\"true\" isClosed=\"false\" processType=\"None\">\n" +
                "        <startEvent id=\"_2\" name=\"开始\"></startEvent>\n" +
                "        <userTask id=\"_4\" name=\"学生请假\" camunda:assignee=\"张三\"></userTask>\n" +
                "        <parallelGateway id=\"_5\" name=\"并行网关\"></parallelGateway>\n" +
                "        <userTask id=\"_6\" name=\"班长审批\" camunda:assignee=\"李四,李四1,李四2\"></userTask>\n" +
                "        <userTask id=\"_7\" name=\"班主任审批\" camunda:assignee=\"王五\"></userTask>\n" +
                "        <sequenceFlow id=\"_8\" sourceRef=\"_2\" targetRef=\"_4\"></sequenceFlow>\n" +
                "        <sequenceFlow id=\"_9\" sourceRef=\"_4\" targetRef=\"_5\"></sequenceFlow>\n" +
                "        <sequenceFlow id=\"_10\" sourceRef=\"_5\" targetRef=\"_6\"></sequenceFlow>\n" +
                "        <sequenceFlow id=\"_11\" sourceRef=\"_5\" targetRef=\"_7\"></sequenceFlow>\n" +
                "        <parallelGateway id=\"_14\" name=\"并行网关\"></parallelGateway>\n" +
                "        <userTask id=\"_30\" name=\"校长审批呀\" camunda:assignee=\"赵六\"></userTask>\n" +
                "        <endEvent id=\"_16\" name=\"结束\"></endEvent>\n" +
                "        <sequenceFlow id=\"_17\" sourceRef=\"_6\" targetRef=\"_14\"></sequenceFlow>\n" +
                "        <sequenceFlow id=\"_18\" sourceRef=\"_7\" targetRef=\"_14\"></sequenceFlow>\n" +
                "        <sequenceFlow id=\"_19\" sourceRef=\"_14\" targetRef=\"_30\"></sequenceFlow>\n" +
                "        <sequenceFlow id=\"_20\" sourceRef=\"_30\" targetRef=\"_16\"></sequenceFlow>\n" +
                "    </process>\n" +
                "    <bpmndi:BPMNDiagram id=\"BPMNDiagram_studentLeaveProcess5\">\n" +
                "        <bpmndi:BPMNPlane bpmnElement=\"parallel-gateway\" id=\"BPMNPlane_studentLeaveProcess5\">\n" +
                "            <bpmndi:BPMNShape bpmnElement=\"_2\" id=\"BPMNShape__2\">\n" +
                "                <omgdc:Bounds height=\"35.0\" width=\"35.0\" x=\"85.0\" y=\"200.0\"></omgdc:Bounds>\n" +
                "            </bpmndi:BPMNShape>\n" +
                "            <bpmndi:BPMNShape bpmnElement=\"_4\" id=\"BPMNShape__4\">\n" +
                "                <omgdc:Bounds height=\"55.0\" width=\"85.0\" x=\"245.0\" y=\"180.0\"></omgdc:Bounds>\n" +
                "            </bpmndi:BPMNShape>\n" +
                "            <bpmndi:BPMNShape bpmnElement=\"_5\" id=\"BPMNShape__5\">\n" +
                "                <omgdc:Bounds height=\"40.0\" width=\"40.0\" x=\"425.0\" y=\"185.0\"></omgdc:Bounds>\n" +
                "            </bpmndi:BPMNShape>\n" +
                "            <bpmndi:BPMNShape bpmnElement=\"_6\" id=\"BPMNShape__6\">\n" +
                "                <omgdc:Bounds height=\"55.0\" width=\"85.0\" x=\"600.0\" y=\"90.0\"></omgdc:Bounds>\n" +
                "            </bpmndi:BPMNShape>\n" +
                "            <bpmndi:BPMNShape bpmnElement=\"_7\" id=\"BPMNShape__7\">\n" +
                "                <omgdc:Bounds height=\"55.0\" width=\"85.0\" x=\"605.0\" y=\"245.0\"></omgdc:Bounds>\n" +
                "            </bpmndi:BPMNShape>\n" +
                "            <bpmndi:BPMNShape bpmnElement=\"_14\" id=\"BPMNShape__14\">\n" +
                "                <omgdc:Bounds height=\"40.0\" width=\"40.0\" x=\"760.0\" y=\"165.0\"></omgdc:Bounds>\n" +
                "            </bpmndi:BPMNShape>\n" +
                "            <bpmndi:BPMNShape bpmnElement=\"_30\" id=\"BPMNShape__30\">\n" +
                "                <omgdc:Bounds height=\"55.0\" width=\"85.0\" x=\"905.0\" y=\"165.0\"></omgdc:Bounds>\n" +
                "            </bpmndi:BPMNShape>\n" +
                "            <bpmndi:BPMNShape bpmnElement=\"_16\" id=\"BPMNShape__16\">\n" +
                "                <omgdc:Bounds height=\"35.0\" width=\"35.0\" x=\"1125.0\" y=\"175.0\"></omgdc:Bounds>\n" +
                "            </bpmndi:BPMNShape>\n" +
                "            <bpmndi:BPMNEdge bpmnElement=\"_8\" id=\"BPMNEdge__8\">\n" +
                "                <omgdi:waypoint x=\"120.0\" y=\"217.0\"></omgdi:waypoint>\n" +
                "                <omgdi:waypoint x=\"245.0\" y=\"207.0\"></omgdi:waypoint>\n" +
                "            </bpmndi:BPMNEdge>\n" +
                "            <bpmndi:BPMNEdge bpmnElement=\"_9\" id=\"BPMNEdge__9\">\n" +
                "                <omgdi:waypoint x=\"330.0\" y=\"207.0\"></omgdi:waypoint>\n" +
                "                <omgdi:waypoint x=\"425.0\" y=\"205.0\"></omgdi:waypoint>\n" +
                "            </bpmndi:BPMNEdge>\n" +
                "            <bpmndi:BPMNEdge bpmnElement=\"_10\" id=\"BPMNEdge__10\">\n" +
                "                <omgdi:waypoint x=\"445.0\" y=\"185.0\"></omgdi:waypoint>\n" +
                "                <omgdi:waypoint x=\"642.0\" y=\"145.0\"></omgdi:waypoint>\n" +
                "            </bpmndi:BPMNEdge>\n" +
                "            <bpmndi:BPMNEdge bpmnElement=\"_11\" id=\"BPMNEdge__11\">\n" +
                "                <omgdi:waypoint x=\"445.0\" y=\"225.0\"></omgdi:waypoint>\n" +
                "                <omgdi:waypoint x=\"647.0\" y=\"245.0\"></omgdi:waypoint>\n" +
                "            </bpmndi:BPMNEdge>\n" +
                "            <bpmndi:BPMNEdge bpmnElement=\"_17\" id=\"BPMNEdge__17\">\n" +
                "                <omgdi:waypoint x=\"642.0\" y=\"145.0\"></omgdi:waypoint>\n" +
                "                <omgdi:waypoint x=\"780.0\" y=\"165.0\"></omgdi:waypoint>\n" +
                "            </bpmndi:BPMNEdge>\n" +
                "            <bpmndi:BPMNEdge bpmnElement=\"_18\" id=\"BPMNEdge__18\">\n" +
                "                <omgdi:waypoint x=\"647.0\" y=\"245.0\"></omgdi:waypoint>\n" +
                "                <omgdi:waypoint x=\"780.0\" y=\"205.0\"></omgdi:waypoint>\n" +
                "            </bpmndi:BPMNEdge>\n" +
                "            <bpmndi:BPMNEdge bpmnElement=\"_19\" id=\"BPMNEdge__19\">\n" +
                "                <omgdi:waypoint x=\"800.0\" y=\"185.0\"></omgdi:waypoint>\n" +
                "                <omgdi:waypoint x=\"905.0\" y=\"192.0\"></omgdi:waypoint>\n" +
                "            </bpmndi:BPMNEdge>\n" +
                "            <bpmndi:BPMNEdge bpmnElement=\"_20\" id=\"BPMNEdge__20\">\n" +
                "                <omgdi:waypoint x=\"990.0\" y=\"192.0\"></omgdi:waypoint>\n" +
                "                <omgdi:waypoint x=\"1125.0\" y=\"192.0\"></omgdi:waypoint>\n" +
                "            </bpmndi:BPMNEdge>\n" +
                "        </bpmndi:BPMNPlane>\n" +
                "    </bpmndi:BPMNDiagram>\n" +
                "</definitions>";

        InputStream inputStream = IOUtils
                .toInputStream(bpm, StandardCharsets.UTF_8.name());
        BpmnModelInstance instance = Bpmn.readModelFromStream(inputStream);
        Stack<String> stringStack = GraphUtils.retrievePath(instance,"_2","_16");
        System.out.println(stringStack);
    }
}
