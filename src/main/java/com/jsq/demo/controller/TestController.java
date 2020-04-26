package com.jsq.demo.controller;

import com.jsq.demo.dao.TestDAO;
import com.jsq.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 方法测试类
 * @author jsq
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestService testService;
    @Autowired
    private TestDAO testDAO;

    @GetMapping("/hello")
    public String sayHello(String name,String n){
        if (StringUtils.isEmpty(n)){
            throw new RuntimeException("名称不能为空！！");
        }
        return "hello，" + name + " and " + n;
    }

    @GetMapping("/hi")
    public String sayHi(){
        return testService.sayHi("jsq","test");
    }

}
