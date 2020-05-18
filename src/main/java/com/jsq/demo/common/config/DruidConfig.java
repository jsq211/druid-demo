package com.jsq.demo.common.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.google.common.collect.Lists;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.sql.DataSource;
import java.util.*;

/**
 * druid-config
 * @author jsq
 */
@Configuration
public class DruidConfig {


    @ConfigurationProperties(prefix = "spring.datasource.druid")
    @Bean
    public DataSource druid(){
        DruidDataSource druidDataSource = new DruidDataSource();
        //设置自定义filter
        druidDataSource.setProxyFilters(proxyFilters());
        return druidDataSource;
    }
    @Bean
    public ServletRegistrationBean statViewServlet(){
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String,String> initParams = new HashMap<>();

        initParams.put("loginUsername","admin");
        initParams.put("loginPassword","123456");
        initParams.put("allow","");
        initParams.put("deny","172.19.162.1");

        bean.setInitParameters(initParams);
        return bean;
    }


    @Bean
    public FilterRegistrationBean webStatFilter(){
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());

        Map<String,String> initParams = new HashMap<>();
        initParams.put("exclusions","*.js,*.css,/druid/*");

        bean.setInitParameters(initParams);

        bean.setUrlPatterns(Arrays.asList("/*"));

        return  bean;
    }

    /**
     * 通知
     * @return
     */
    @Bean(value = "druid-stat-interceptor")
    public DruidStatInterceptor DruidStatInterceptor() {
        DruidStatInterceptor druidStatInterceptor = new DruidStatInterceptor();
        return druidStatInterceptor;
    }

    /**
     * 切点
     * @return
     */
    @Bean(name = "druid-stat-pointcut")
    @Scope("prototype")
    public JdkRegexpMethodPointcut druidStatPointcut() {
        JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
        //对正则匹配的类进行spring监听
        pointcut.setPatterns("com.jsq.demo.*");
        return pointcut;
    }

    /**
     * 织入监听
     * @param druidStatInterceptor
     * @param druidStatPointcut
     * @return
     */
    @Bean
    public DefaultPointcutAdvisor druidStatAdvisor(@Qualifier("druid-stat-interceptor") DruidStatInterceptor druidStatInterceptor,
                                                   @Qualifier("druid-stat-pointcut") JdkRegexpMethodPointcut druidStatPointcut) {
        DefaultPointcutAdvisor defaultPointAdvisor = new DefaultPointcutAdvisor();
        defaultPointAdvisor.setPointcut(druidStatPointcut);
        defaultPointAdvisor.setAdvice(druidStatInterceptor);

        return defaultPointAdvisor;
    }
    @Bean
    public List<Filter> proxyFilters(){
        //将自定义filter加入druid
        return Lists.newArrayList(new TestFilter());
    }
}
