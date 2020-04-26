package com.jsq.demo.service;

import com.alibaba.fastjson.JSON;
import com.jsq.demo.common.utils.SpringUtil;
import com.jsq.demo.dao.TestDAO;
import com.jsq.demo.pojo.po.TestPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 测试
 * @author jsq
 */
@Service
public class TestService {
    @Autowired
    private TestDAO testDAO;

    public String get() {
        List<String> str = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String name = i == 2?null:"jsq" + i;
            String ans = SpringUtil.getBean(this.getClass()).sayHi(name,"jsq" + i);
            str.add(ans);
        }
        return JSON.toJSONString(str);
    }
    @Transactional(rollbackFor = Exception.class)
    public String sayHi(String name,String n){
        TestPO testPO = testDAO.findOne("1");
//        Random random=new Random();
//        Thread t1 = new Thread(()->{
//            TestPO testPO = testDAO.findOne("1");
////            testPO.setN(random.nextInt(100));
//            testPO.setId("222");
//            testDAO.insert(testPO);
//            try {
//                System.out.println("sleep----------------");
//                Thread.sleep(1000000000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        Thread t2 = new Thread(()->{
//            System.out.println("thread name :"+Thread.currentThread().getName());
//            TestPO testPO = testDAO.findOne("222");
//            testDAO.insert(testPO);
//            try {
//                System.out.println("sleep----------------");
//                Thread.sleep(1000000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        Thread t3 = new Thread(()->{
//            System.out.println("thread name :"+Thread.currentThread().getName());
//            TestPO testPO = testDAO.findOne("222");
//            testDAO.insert(testPO);
//            try {
//                System.out.println("sleep----------------");
//                Thread.sleep(1000000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        t1.start();
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        t2.start();
//        t3.start();
////        testDAO.insert(name,n);
        return JSON.toJSONString(testPO);
    }
}
