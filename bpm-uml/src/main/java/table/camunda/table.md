#### sql

```sql
select convert(bytes_ using utf8) as cxml,a.name_,a.id_ from ACT_GE_BYTEARRAY a;
```

#### table

##### 资源定义系列

<table>
 <thead><tr><td></td><td>字段</td><td>描述</td></tr></thead>
 <tbody>
     <tr><td>ACT_RE_PROCDEF</td><td>deployment_id_</td><td style="color: red">关联到布署</td></tr>
     <tr><td></td><td>tenant_id_</td><td></td></tr>
     <tr><td></td><td>version_</td><td></td></tr>
     <tr><td></td><td>key_</td><td></td></tr>
     <tr><td></td><td>name_</td><td></td></tr>
     <tr><td colspan="3" style="text-align: center; color:red">流程定义</td></tr>
   </tbody>
</table>

##### 运行实例系列

| 执行实例          | 字段           | 描述                                                         |
| ---------------- | -------------- | -----------------------------------------------------------|
| ACT_RU_EXECUTION | is_active_     | 激活否，1为激活                                              |
|                  | is_concurrent_ | 是否并发，1为并发                                            |
|                  | rev_           | 数据库更新次数                                               |
|                  | id_            | 可能跟 PROC_INST_ID_ 相同也可能不同，相同时表明这条记录为主实例记录 |
|                  | PROC_INST_ID_  | 一个实例不管有多少条分支实例，这个值都是一样的                    |
|                  | BUSINESS_KEY_  | 业务主键，有唯一性约束                                       |
|                  | SUPER_EXEC     | 如果存在表示这个实例记录为一个外部子流程记录，对应主流程的主键ID      |
|                  | ACT_ID_        | 流程运行到的节点标识，<font color=red>存的是xml中的节点标识</font> |
|                  | ACT_INST_ID_   | <font color=red>关联到ACT_HI_ACTINST表 ID_ </font>           |
|                  |                |                                                             |

<table>
 <thead><tr><td></td><td>字段</td><td>描述</td></tr></thead>
 <tbody>
     <tr><td>ACT_RU_TASK</td><td>EXECUTION_ID_</td><td style="color: red">关联到ACT_RU_EXECUTION表 ID_</td></tr>
     <tr><td></td><td>NAME_</td><td style="color: red">当前任务节点名称,存的是xml中的节点名称</td></tr>
     <tr><td></td><td>TASK_DEF_KEY_</td><td style="color: red">当前任务节点标识,存的是xml中的节点标识</td></tr>
     <tr><td></td><td>SUSPENSION_STATE_</td><td>任务状态，1 为激活 2 为挂起</td></tr>
     <tr><td></td><td>PROC_INST_ID</td><td></td></tr>
     <tr><td colspan="3" style="text-align: center; color:red">代办任务表</td></tr>
   </tbody>
</table>

<table>
 <thead><tr><td></td><td>字段</td><td>描述</td></tr></thead>
 <tbody>
     <tr><td>ACT_RU_IDENTITYLINK</td><td>TYPE_</td><td>candidate 候选者 <br/>participant 参与者 <br/>owner 拥有者<br/>assignee 签收人或被委托人<br/>starter 起动者</td></tr>
     <tr><td></td><td>TASK_ID_</td><td></td></tr>
     <tr><td></td><td>PROC_DEF_ID_</td><td></td></tr>
     <tr><td></td><td>PROC_INST_ID_</td><td></td></tr>
     <tr><td colspan="3" style="text-align: center; color:red">节点参与者信息,任务参与者数据表</td></tr>
   </tbody>
</table>

<table>
 <thead><tr><td></td><td>字段</td><td>描述</td></tr></thead>
 <tbody>
     <tr><td>ACT_RU_VARIABLE</td><td>EXECUTION_ID_</td><td></td></tr>
     <tr><td></td><td>PROC_INST_ID_</td><td></td></tr>
     <tr><td></td><td>TASK_ID_</td><td></td></tr>
     <tr><td></td><td>VAR_SCOPE_</td><td></td></tr>
     <tr><td colspan="3" style="text-align: center; color:red">流程执行变量表</td></tr>
   </tbody>
</table>

<table>
 <thead><tr><td></td><td>字段</td><td>描述</td></tr></thead>
 <tbody>
     <tr><td>ACT_RU_INCIDENT</td><td>PROC_INST_ID_</td><td style="color:red">xml中节点标识</td></tr>
     <tr><td></td><td>INCIDENT_MSG_</td><td></td></tr>
     <tr><td colspan="3" style="text-align: center; color:red">流程执行异常事件记录表</td></tr>
   </tbody>
</table>

##### 历史记录系列

<table>
  <thead><tr><td></td><td>字段</td><td>描述</td></tr></thead>
  <tbody>
    <tr><td>ACT_HI_PROCINST</td><td>PROC_INST_ID_</td><td></td></tr>
    <tr><td></td><td>BUSINESS_KEY_</td><td></td></tr>
    <tr><td></td><td>PROC_DEF_ID_</td><td></td></tr>
    <tr><td></td><td>START_TIME_</td><td ></td></tr>
    <tr><td></td><td>END_TIME_</td><td ></td></tr>
    <tr><td></td><td>DURATION_</td><td>节点耗时，单位为毫秒</td></tr>
    <tr><td></td><td>START_USER_ID_</td><td></td></tr>
    <tr><td></td><td>START_ACT_ID_</td><td style="color: red">xml节点标识</td></tr>
    <tr><td></td><td>END_ACT_ID_</td><td></td></tr>
    <tr><td></td><td>DELETE_REASON_</td><td></td></tr>
    <tr><td></td><td>STATE_</td><td style="color: red">COMPLETED/ACTIVE/INTERNALLY_TERMINATED</td></tr>
    <tr><td colspan="3" style="text-align: center; color:red">历史流程实例</td></tr>
  </tbody>
</table>

<table>
  <thead><tr><td></td><td>字段</td><td>描述</td></tr></thead>
  <tbody>
    <tr><td>ACT_HI_ACTINST</td><td>ACT_ID_</td><td style="color:red">xml中节点标识</td></tr>
    <tr><td></td><td>ACT_NAME_ </td><td style="color:red">xml中节点名称</td></tr>
    <tr><td></td><td>ACT_TYPE_</td><td style="color:red">xml中节点类型 userTask/startEvent/noneEndEvent/parallelGateway 等</td></tr>
    <tr><td></td><td>PROC_INST_ID_</td><td ></td></tr>
    <tr><td></td><td>ACT_INST_STATE_</td><td ></td></tr>
    <tr><td></td><td>DURATION_</td><td>节点耗时，单位为毫秒</td></tr>
    <tr><td></td><td>ASSIGNEE_</td><td>签收人</td></tr>
    <tr><td></td><td>TASK_ID_</td><td>userTask 情况下关联到是哪个任务</td></tr>
    <tr><td colspan="3" style="text-align: center; color:red">流程历史流转节点，记录所有节点</td></tr>
  </tbody>
</table>

<table>
  <thead><tr><td></td><td>字段</td><td>描述</td></tr></thead>
  <tbody>
    <tr><td>ACT_HI_TASKINST</td><td>TASK_DEF_KEY_</td><td style="color:red">xml中节点标识</td></tr>
    <tr><td></td><td>NAME_</td><td style="color:red">xml中节点名称</td></tr>
    <tr><td></td><td>PROC_INST_ID_</td><td ></td></tr>
    <tr><td></td><td>OWNER_</td><td ></td></tr>
    <tr><td></td><td>ASSIGNEE_</td><td ></td></tr>
    <tr><td></td><td>DELETE_REASON_</td><td >completed/deleted</td></tr>
    <tr><td colspan="3" style="text-align: center; color:red">流程历史流转节点，只记录userTask节点</td></tr>
  </tbody>
</table>
