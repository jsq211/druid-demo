package com.jsq.demo.common.config;


import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jsq.demo.common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * testFilter
 */
public class TestFilter extends FilterEventAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TestFilter.class);
    private static long timeMessage;
    protected  volatile ReentrantLock lock = new ReentrantLock();
    private SqlTimeOutAlarm sqlTimeOutAlarm;
    //慢sql数
    private volatile Stack<String> slowSql = new Stack<>();

    @Override
    public void init(DataSourceProxy dataSource) {
        logger.info("初始化Filter");
        super.init(dataSource);
        initSlowSql();
    }

    private void initSlowSql() {
        if (sqlTimeOutAlarm == null){
            sqlTimeOutAlarm = new SqlTimeOutAlarm();
            sqlTimeOutAlarm.start();
            return;
        }

    }

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
//        logger.info("自定义拦截，在执行操作前执行该方法，如打印执行sql："+sql);
        timeMessage = System.nanoTime();
        String type = statement.getConnectionProxy().getDirectDataSource().getDbType();
//        logger.info("---------dbtype-------"+type);
        super.statementExecuteBefore(statement, sql);
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        long nanos = timeMessage - statement.getLastExecuteStartNano();
        super.statementExecuteAfter(statement, sql, result);
//        logger.info("自定义拦截器，在执行操作后执行该方法，如打印执行sql：  "+sql);
        if (true){
//            logger.error("Error Sql : " + sql);
            slowSql.push(statement.getRawObject().toString());
        }
    }
    public class SqlTimeOutAlarm extends Thread{
        public void run() {
            logger.info("慢sql监控线程启动-----");
            for(;;){
                if (!slowSql.empty()){
                    lock.lock();
                    try {
                        String sql = slowSql.pop();
                        logger.error("慢SQL告警------Error Sql : 【{}】\n" ,sql.substring(sql.indexOf("ClientPreparedStatement")) );
                    }catch (Exception e){
                        //正常告警不做处理
                    }finally {
                        lock.unlock();
                    }
                }
            }
        }
    }
}
