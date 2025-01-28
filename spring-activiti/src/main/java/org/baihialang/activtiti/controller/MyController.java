package org.baihialang.activtiti.controller;

import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.runtime.TaskRuntime;
import org.baihialang.activtiti.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: flowable-demo
 * @description: activiti的controller类
 * @author: 白海浪
 * @create: 2025-01-28 12:56
 **/
@RestController
@RequestMapping("/activiti")
public class MyController {

    @Autowired
    ProcessRuntime processRuntime;

    @Autowired
    TaskRuntime taskRuntime;

    @Autowired
    SecurityUtil securityUtil;


    @RequestMapping(value = "/getTasks", method = RequestMethod.GET)
    public void selectTask() {
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
