package com.jsq.demo.common.utils.mybasedao.annotation;

import java.lang.annotation.*;

/**
 * 逻辑删除字段 根据对应字段进行判断 初始化默认时为0
 * 可自定义为0或其他
 * @author Administrator
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogicDelete {
    int value() default 0;
}
