package com.jsq.demo.common.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;
import com.jsq.demo.DemoApplication;
import com.jsq.demo.dao.TestDAO;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;

/**
 * 测试通过查询数据库获取第三方数据库登陆信息
 * @author jsq
 */
@Component
public class JdbcComponent {
    /**
     * 数据源配置
     */
    private static Map<String, DataSource> dataSourceMap = Maps.newConcurrentMap();
    /**
     * jdbcsql
     */
    private static Map<String, NamedParameterJdbcTemplate> namedParameterJdbcTemplateMap = Maps.newConcurrentMap();

    @Resource
    private TestDAO testDAO;

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(String bookId) {
        if (!namedParameterJdbcTemplateMap.containsKey(bookId)) {
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource(bookId));
            namedParameterJdbcTemplateMap.put(bookId, namedParameterJdbcTemplate);
        }
        return namedParameterJdbcTemplateMap.get(bookId);
    }

    private DataSource getDataSource(String key) {
        if (dataSourceMap.containsKey(key)){
            return dataSourceMap.get(key);
        }

        //获取通用配置
        DruidDataSource druidDataSource = (DruidDataSource) DemoApplication.getApplicationContext().getBean("druid");
        //查询dao 进行相应数据替换

        return druidDataSource;
    }
}
