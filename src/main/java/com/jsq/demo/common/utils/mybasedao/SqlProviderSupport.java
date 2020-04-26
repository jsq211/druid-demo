package com.jsq.demo.common.utils.mybasedao;


import com.baomidou.mybatisplus.annotation.TableName;
import com.jsq.demo.common.utils.ReflectionUtil;
import com.jsq.demo.common.utils.StringUtil;
import com.jsq.demo.common.utils.mybasedao.annotation.LogicDelete;
import com.jsq.demo.common.utils.mybasedao.annotation.NoColumn;
import com.jsq.demo.common.utils.mybasedao.annotation.Primary;
import com.jsq.demo.dao.MyBaseDAO;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * @author jsq
 */
abstract class SqlProviderSupport {
    /**
     * 主键
     */
    private static final String DEFAULT_PRIMARY_KEY = "id";
    /**
     * 映射字段
     */
    private static Map<Class,TableInfo> tableInfoMap = new ConcurrentHashMap<>(256);


    /**
     * 获取表信息结构
     * @param context
     * @return
     */
    protected TableInfo tableInfo(ProviderContext context) {
        TableInfo info = tableInfoMap.get(context.getMapperType());
        if (info != null) {
            return info;
        }

        Class<?> entityClass = entityType(context);
        //获取不含有@NoColumn注解的fields
        Field[] fields = excludeNoColumnField(ReflectionUtil.getFields(entityClass));
        info = TableInfo.entityClass(entityClass)
                .fields(fields)
                .tableName(tableName(entityClass))
                .primaryKeyColumn(primaryKeyColumn(fields))
                .columns(columns(fields))
                .selectColumns(selectColumns(fields))
                .build();

        tableInfoMap.put(context.getMapperType(), info);
        return info;
    }
    /**
     * 获取非空表信息结构
     * @param context
     * @return
     */
    protected TableInfo tableInfoNotNull(ProviderContext context) {
        Class<?> entityClass = entityType(context);
        //获取不含有@NoColumn注解的非空fields
        Field[] fields = excludeNoColumnFieldNotNull(ReflectionUtil.getFields(entityClass));
        TableInfo info = TableInfo.entityClass(entityClass)
                .fields(fields)
                .tableName(tableName(entityClass))
                .primaryKeyColumn(primaryKeyColumn(fields))
                .columns(columns(fields))
                .selectColumns(selectColumns(fields))
                .build();

        tableInfoMap.put(context.getMapperType(), info);
        return info;
    }
    /**
     * 获取BaseMapper接口中的泛型类型
     * @param context
     * @return
     */
    protected Class<?> entityType(ProviderContext context) {
        return Stream.of(context.getMapperType().getGenericInterfaces())
                .filter(ParameterizedType.class::isInstance)
                .map(ParameterizedType.class::cast)
                .filter(type -> type.getRawType() == MyBaseDAO.class)
                .findFirst()
                .map(type -> type.getActualTypeArguments()[0])
                .filter(Class.class::isInstance).map(Class.class::cast)
                .orElseThrow(() -> new IllegalStateException("未找到mapper的泛型类 " + context.getMapperType().getName() + "."));
    }


    protected String tableName(Class<?> entityType) {
        if (entityType.isAnnotationPresent(TableName.class)){
            TableName tableName = entityType.getAnnotationsByType(TableName.class)[0];
            return tableName.value();
        }else {
            throw new RuntimeException("获取表名失败："+ entityType.getSimpleName());
        }
    }

    /**
     * 过滤含有@NoColumn注解的field
     * @param totalField  entityClass所有的字段
     * @return   不包含@NoColumn注解的fields
     */
    protected Field[] excludeNoColumnField(Field[] totalField) {
        return Stream.of(totalField)
                //过滤含有@NoColumn注解的field
                .filter(field -> !field.isAnnotationPresent(NoColumn.class))
                .toArray(Field[]::new);
    }
    /**
     * 过滤含有@NoColumn注解的非空field
     * @param totalField  entityClass所有的字段
     * @return   不包含@NoColumn注解的fields
     */
    protected Field[] excludeNoColumnFieldNotNull(Field[] totalField) {
        return Stream.of(totalField)
                //过滤含有@NoColumn注解的field
                .filter(field -> !field.isAnnotationPresent(NoColumn.class)).filter(Objects::nonNull)
                .toArray(Field[]::new);
    }
    /**
     * 获取查询对应的字段 (不包含pojo中含有@NoColumn主键的属性)
     *
     * @param fields p
     * @return
     */
    protected String[] selectColumns(Field[] fields) {
        return Stream.of(fields).map(this::selectColumnName).toArray(String[]::new);
    }

    /**
     * 获取所有pojo所有属性对应的数据库字段 (不包含pojo中含有@NoColumn主键的属性)
     *
     * @param fields entityClass所有fields
     * @return
     */
    protected String[] columns(Field[] fields) {
        return Stream.of(fields).map(this::columnName).toArray(String[]::new);
    }

    /**
     * 如果fields中含有@Primary的字段，则返回该字段名为主键，否则默认'id'为主键名
     * @param fields entityClass所有fields
     * @return  主键column(驼峰转为下划线)
     */
    protected String primaryKeyColumn(Field[] fields) {
        return Stream.of(fields).filter(field -> field.isAnnotationPresent(Primary.class))
                .findFirst()    //返回第一个primaryKey的field
                .map(this::columnName)
                .orElse(DEFAULT_PRIMARY_KEY);
    }

    /**
     * 获取单个属性对应的数据库字段（带有下划线字段将其转换为"字段 AS pojo属性名"形式）
     *
     * @param field
     * @return
     */
    protected String selectColumnName(Field field) {
        String camel = StringUtil.camel2Underscore(field.getName());
        return camel.contains("_") ? camel + " AS " + field.getName() : camel;
    }

    /**
     * 获取单个属性对应的数据库字段
     *
     * @param field  entityClass中的field
     * @return
     */
    protected String columnName(Field field) {
        return StringUtil.camel2Underscore(field.getName());
    }

    protected String bindParameter(Field field) {
        return "#{" + field.getName() + "}";
    }

    protected Object value(Object bean, Field field) {
        try {
            field.setAccessible(true);
            return field.get(bean);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } finally {
            field.setAccessible(false);
        }
    }

    protected String getCondition(Field field) {
        if (field.isAnnotationPresent(LogicDelete.class)){
            return columnName(field) + " = " + field.getAnnotation(LogicDelete.class).value();
        }
        return columnName(field) + " = " + bindParameter(field);
    }
}

