package com.camunda.quick.controller;

import io.swagger.annotations.ApiOperation;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceDto;
import org.springframework.web.bind.annotation.*;

public interface CamundaRestfulApiClient {

    @ApiOperation(value = "启动流程", notes = "根据流程定义ID启动流程 ,StartProcessInstanceDto")
    @RequestMapping(value = "/process-definition/{id}/start", method = RequestMethod.POST, consumes = "application/json")
    ProcessInstanceDto startProcessById(@PathVariable("id") String id,
                                        @RequestBody(required = false) String entity) throws Exception;

    @ApiOperation(value = "启动流程", notes = "根据流程定义key启动最新版本并且无租户的流程")
    @RequestMapping(value = "/process-definition/key/{key}/start", method = RequestMethod.POST, consumes = "application/json")
    ProcessInstanceDto startProcessByKey(@PathVariable("key") String key,
                                         @RequestBody(required = false) String entity) throws Exception;

    @ApiOperation(value = "启动流程", notes = "根据流程定义key启动最新版本并且是所属租户的流程")
    @RequestMapping(value = "/process-definition/key/{key}/tenant-id/{tenantId}/start", method = RequestMethod.POST, consumes = "application/json")
    ProcessInstanceDto startProcessByKeyWithTenant(@RequestHeader("Authentication") String auth,
                                                   @PathVariable("key") String key, @PathVariable("tenantId") String tenantId,
                                                   @RequestBody(required = false) String entity) throws Exception;

    @ApiOperation(value = "查询运行任务", notes = "根据查询条件过滤运行任务,TaskQueryDto")
    @RequestMapping(value = "/task", method = RequestMethod.POST, consumes = "application/json")
    ProcessInstanceDto queryTask(@RequestBody(required = false) String entity) throws Exception;
}
