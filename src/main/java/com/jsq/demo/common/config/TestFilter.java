package com.jsq.demo.common.config;


import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * testFilter
 */
public class TestFilter extends FilterEventAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TestFilter.class);
    protected volatile ReentrantLock lock = new ReentrantLock();
    private SqlTimeOutAlarm sqlTimeOutAlarm;
    //慢sql数
    protected long slowSqlTime = 1*1000;
    private volatile Set<String> slowSql = new HashSet<>();
    @Override
    public void init(DataSourceProxy dataSource) {
        logger.info("初始化Filter");
        super.init(dataSource);
        slowSql = Collections.synchronizedSet(slowSql);
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
        String type = statement.getConnectionProxy().getDirectDataSource().getDbType();
//        logger.info("---------dbtype-------"+type);
        super.statementExecuteBefore(statement, sql);
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        super.statementExecuteAfter(statement, sql, result);
        final long nowNano = System.nanoTime();
        final long nanos = nowNano - statement.getLastExecuteStartNano();
//        logger.info("自定义拦截器，在执行操作后执行该方法，如打印执行sql：  "+sql);
        long millis = nanos / (1000 * 1000);
        if (millis>slowSqlTime){
//            logger.error("Error Sql : " + sql);
            slowSql.add(statement.getRawObject().toString());
        }
    }
    public class SqlTimeOutAlarm extends Thread{
        public void run() {
            logger.info("慢sql监控线程启动-----");
            for(;;){
                if (!slowSql.isEmpty()){
                    lock.lock();
                    try {
                        Iterator it = slowSql.iterator();
                        while (it.hasNext()){
                            String sql = (String)it.next();
                            logger.info("慢SQL告警: 【{}】\n" ,sql.substring(sql.indexOf(":")+1).replace('\n',' '));
                            it.remove();
                        }
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
