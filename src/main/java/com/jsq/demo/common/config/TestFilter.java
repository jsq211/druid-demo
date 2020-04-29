package com.jsq.demo.common.config;


import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TestFilter extends FilterEventAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TestFilter.class);
    @Override
    public void init(DataSourceProxy dataSource) {
        logger.info("初始化Filter");
        super.init(dataSource);
    }

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        logger.info("调用sql："+sql);
        super.statementExecuteBefore(statement, sql);
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        logger.info("调用sql："+sql);
        super.statementExecuteAfter(statement, sql, result);
    }
}
