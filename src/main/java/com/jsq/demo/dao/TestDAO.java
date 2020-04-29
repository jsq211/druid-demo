package com.jsq.demo.dao;

import com.jsq.demo.pojo.po.TestPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;


/**
 * 测试数据库连接
 * @author jsq
 */

@Mapper
public interface TestDAO extends MyBaseDAO<TestPO>{

    /**
     * 禁止 select * 查询会报错
     * @return
     */
    TestPO selectTest();
}
