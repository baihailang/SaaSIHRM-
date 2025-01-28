package com.baihailang.test;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.baihialang.activiti.Holiday;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;
import org.junit.Test;
/**
 * @program: flowable-demo
 * @description: Activiti测试类
 * @author: 白海浪
 * @create: 2025-01-27 11:25
 **/
public class Activiti_test {


    /**
     * 创建表数据库
     */
    @Test
    public void test_createtable() {
        //创建ProcessEngineConfiguration对象
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        //通过ProcessEngineConfiguration创建ProcessEngine，此时会创建数据库
        ProcessEngine processEngine = configuration.buildProcessEngine();
        System.out.println("表初始化成功");
    }


    /**
     * 流程部署图片和bpmn文件
     */
    @Test
    public void test_importbpn() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        DeploymentBuilder deployment = processEngine.getRepositoryService().createDeployment();
        Deployment deployment1 = deployment.addClasspathResource("diagram/holiday4.bpmn")
                .addClasspathResource("diagram/holiday4.png")
                .name("请假流程")
                .key("holiday")
                .deploy();
    }


    /**
     * 流程部署zip文件
     */
    @Test
    public void test_importbpmnzip() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        DeploymentBuilder deployment = processEngine.getRepositoryService().createDeployment();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("diagram/holiday.zip");
        ZipInputStream gzipInputStream = new ZipInputStream(inputStream);
        Deployment deployment1 = deployment.name("李四请假流程").key("holiday").addZipInputStream(gzipInputStream)
                .deploy();
    }


    /**
     * 流程实例启动
     */
    @Test
    public void test_startbpm() {
        RuntimeService runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();
        //没有标识businesskey
        //ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("请假流程");
        //有标识businesskey(第二个参数)
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("请假流程", "1122233");
        System.out.println("流程实例启动成功");
        System.out.println("流程实例id：" + processInstance.getId());
        System.out.println("流程定义id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程定义key：" + processInstance.getProcessDefinitionKey());
        System.out.println("流程定义name：" + processInstance.getProcessDefinitionName());
        System.out.println("流程定义version：" + processInstance.getProcessDefinitionVersion());
    }



    /**
     * 查询是否有任务需要审批
     */
    @Test
    public void test_selectusertak() {
        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
        List<Task> tasks = taskService.createTaskQuery().list();
        //查询所有任务
        for (Task task : tasks) {
            System.out.printf("11任务id：%s 任务名称：%s 任务负责人：%s \n", task.getId(), task.getName(), task.getAssignee());
        }
        //查询张三的任务
        List<Task> list = taskService.createTaskQuery().processDefinitionKey("holiday").list();
        for (Task task : list) {
            System.out.printf("22任务id：%s 任务名称：%s 任务负责人：%s \n", task.getId(), task.getName(), task.getAssignee());
        }
    }


    /**
     * 审批通过任务
     */
    @Test
    public void test_completetask() {
        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
        //参数为任务id
        taskService.complete("125002");
    }

    /**
     * 流程定义的查询
     */
    @Test
    public void test_cxlc() {
        //创建ProcessEngineConfiguration对象
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        //通过ProcessEngineConfiguration创建ProcessEngine，此时会创建数据库
        ProcessEngine processEngine = configuration.buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        for (ProcessDefinition processDefinition : processDefinitionQuery.orderByProcessDefinitionVersion().desc().list()) {
            System.out.printf("流程定义id：%s 流程定义key：%s 流程定义name：%s 流程定义version：%s \n", processDefinition.getId(), processDefinition.getKey(), processDefinition.getName(), processDefinition.getVersion());
        }


    }


    /**
     * 流程定义删除
     */
    @Test
    public void test_dellc() {
        //创建ProcessEngineConfiguration对象
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        //通过ProcessEngineConfiguration创建ProcessEngine，此时会创建数据库
        ProcessEngine processEngine = configuration.buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.deleteDeployment("72501");
    }


    /**
     * 读取资源文件
     */
    @Test
    public void get_res() throws IOException {
        //创建ProcessEngineConfiguration对象
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        //通过ProcessEngineConfiguration创建ProcessEngine，此时会创建数据库
        ProcessEngine processEngine = configuration.buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        ProcessDefinition holiday = processDefinitionQuery.processDefinitionKey("请假流程").singleResult();
        String deploymentId = holiday.getDeploymentId();
        InputStream pngIs = repositoryService.getResourceAsStream(deploymentId, holiday.getDiagramResourceName());
        InputStream bpmnIs = repositoryService.getResourceAsStream(deploymentId, holiday.getResourceName());
        OutputStream bpmnoutputStream = new FileOutputStream("F:\\xiazai\\"+holiday.getResourceName());
        OutputStream pngoutputStream = new FileOutputStream("F:\\xiazai\\"+holiday.getDiagramResourceName());
        IOUtils.copy(pngIs,pngoutputStream);
        IOUtils.copy(bpmnIs,bpmnoutputStream);
        pngoutputStream.close();
        bpmnoutputStream.close();
        pngIs.close();
        bpmnIs.close();
    }


    /**
     * 历史数据查看
     */
    @Test
    public void get_his() throws IOException {
        //创建ProcessEngineConfiguration对象
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        //通过ProcessEngineConfiguration创建ProcessEngine，此时会创建数据库
        ProcessEngine processEngine = configuration.buildProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();
        HistoricActivityInstanceQuery hisQuery = historyService.createHistoricActivityInstanceQuery();
        List<HistoricActivityInstance> list = hisQuery.processInstanceId("12501").orderByHistoricActivityInstanceStartTime().asc().list();;
        for (HistoricActivityInstance hisInstance : list) {
            System.out.println(hisInstance.getAssignee());
            System.out.println(hisInstance.getActivityId());
            System.out.println(hisInstance.getStartTime());
            System.out.println(hisInstance.getEndTime());
            System.out.println(hisInstance.getDurationInMillis());
        }
    }


    /**
     * 使用UEL表达式表示每个节点审批人
     *
     */
    @Test
    public void test_uelAssignee() {
        ProcessEngine processengine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processengine.getRuntimeService();
        HashMap<String, Object> map = new HashMap<>();
        map.put("assignee0", "张三");
        map.put("assignee1", "李四");
        map.put("assignee2", "王五");
        ProcessInstance holiday = runtimeService.startProcessInstanceByKey("请假流程", map);
        System.out.println(holiday.getId());
        System.out.println(holiday.getProcessDefinitionId());
        System.out.println(holiday.getBusinessKey());
    }


    /**
     * 使用全局属性变量-发起时
     *
     */
    @Test
    public void test_useglobal_propertity() {
        ProcessEngine processengine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processengine.getRuntimeService();
        Holiday holiday = new Holiday();
        holiday.setNum(4);
        holiday.setReason("不想上班");
        holiday.setApplicant_name("张三");
        holiday.setBegindate(new Date("2024/01/01"));
        holiday.setEnddate(new Date("2024/01/04"));
        Map<String, Object> map = new HashMap<>();
        map.put("holiday", holiday);
        map.put("assignee0", "张三");
        map.put("assignee1", "李四");
        map.put("assignee2", "王五");
        ProcessInstance holiday1 = runtimeService.startProcessInstanceByKey("holiday", map);
    }

    /**
     * 使用全局属性变量-审批时
     */
    @Test
    public void test_useglobal_propertity2() {
        ProcessEngine processengine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processengine.getRuntimeService();
        Holiday holiday = new Holiday();
        holiday.setNum(3);
        runtimeService.setVariable("107501","holiday",holiday);
        TaskService taskService = processengine.getTaskService();
        taskService.complete("110002");
    }


    /**
     * 使用局部变量-审批
     *
     */
    @Test
    public void test_uselocal_propertity() {
        ProcessEngine processengine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processengine.getTaskService();
        Holiday holiday = new Holiday();
        holiday.setNum(4);
        holiday.setReason("不想上班");
        holiday.setApplicant_name("张三");
        taskService.setVariableLocal("107501","holiday",holiday);
        taskService.complete("110002");
    }
}
