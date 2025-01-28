package org.baihialang.activtiti;

import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.baihialang.activtiti.util.SecurityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringActivitiApplicationTests {


    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private SecurityUtil securityUtil;


   /**
    * @Description:查询流程定义使用分页并输出流程定义的总数
    * @Param:
    * @return:
    * @Author: 白海浪
    * @Date: 2025/1/28
    */
    @Test
    public void testDefinition(){
        securityUtil.logInAs("john");
        Page<ProcessDefinition> processDefinitionPage = processRuntime.processDefinitions(Pageable.of(0, 10));
        System.out.printf("总共有多少流程"+processDefinitionPage.getTotalItems());
        for (ProcessDefinition processDefinition : processDefinitionPage.getContent()) {
            System.out.println(processDefinition.getName());
            System.out.println(processDefinition.getKey());
            System.out.println(processDefinition.getId());
        }
    }


    /** 
    * @Description: 生成一个实例根据processesKey
    * @Param: []
    * @return: void
    * @Author: 白海浪
    * @Date: 2025/1/28
    */
    @Test
    public void testStartProcesses(){
        securityUtil.logInAs("john");
        ProcessInstance pi = processRuntime.start(ProcessPayloadBuilder.start().withProcessDefinitionKey("myprocess_1").build());
        System.out.println(pi.getId());
    }


    /** 
    * @Description: 查询任务并完成任务
    * @Param: []
    * @return: void
    * @Author: 白海浪
    * @Date: 2025/1/28
    */
    @Test
    public void testCompleteTask(){
        securityUtil.logInAs("john");
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 10));
        for (Task task : tasks.getContent()) {
            System.out.println(task.getName());
            System.out.println("指定的执行人:"+task.getAssignee());
            System.out.println("任务id:"+task.getId());
              //拾取并执行任务
//            taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
//           taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());

        }
    }

}
