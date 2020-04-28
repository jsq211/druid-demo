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
@Component
public interface TestDAO extends MyBaseDAO<TestPO>{

    void insert(@Param("name")String name, @Param("n") String n);

    TestPO select();
}
