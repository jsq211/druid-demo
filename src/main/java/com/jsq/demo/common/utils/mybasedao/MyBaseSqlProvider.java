package com.jsq.demo.common.utils.mybasedao;

import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;
import java.util.stream.Stream;

/**
 * @author jsq
 */
public class MyBaseSqlProvider extends SqlProviderSupport{
    /**
     * 根据主键查询一条记录
     * @param context
     * @return
     */
    public String findOne(ProviderContext context) {
        TableInfo table = tableInfo(context);
        return new SQL()
                .SELECT(table.getSelectColumns())
                .FROM(table.getTableName())
                .WHERE(table.getPrimaryKeyColumn() + " = #{id}")
                .toString();

    }

    /**
     * 根据搜索条件进行搜索
     * @param entity
     * @param context
     * @return
     */
    public String findOneByEntity(Object entity,ProviderContext context) {
        TableInfo table = tableInfo(context);
        return new SQL()
                .SELECT(table.getSelectColumns())
                .FROM(table.getTableName())
                .WHERE(Stream.of(table.getFields())
                        .filter(field -> value(entity, field) != null)
                        .map(field -> columnName(field) + " = " + bindParameter(field))
                        .toArray(String[]::new)).ORDER_BY(table.getPrimaryKeyColumn() + " DESC").LIMIT(1).toString();
    }

    /**
     * 插入一条记录
     * @param context
     * @return
     */
    public String insert(ProviderContext context) {
        TableInfo table = tableInfo(context);
        return new SQL()
                .INSERT_INTO(table.getTableName())
                .INTO_COLUMNS(table.getColumns())
                .INTO_VALUES(Stream.of(table.getFields()).map(this::bindParameter).toArray(String[]::new))
                .toString();
    }

    /**
     *  根据主键更新 当字段值不存在时更新为null
     * @param context
     * @return
     */
    public String updateById(ProviderContext context){
        TableInfo table = tableInfo(context);
        return new SQL()
                .UPDATE(table.getTableName())
                .SET(Stream.of(table.getFields())
                        .filter(field -> !table.getPrimaryKeyColumn().equals(columnName(field)))
                        .map(field -> columnName(field) + " = " + bindParameter(field)).toArray(String[]::new))
                .WHERE(table.getPrimaryKeyColumn() + " = #{id}")
                .toString();
    }
    /**
     * 更新非空的字段
     * @param entity
     * @param context
     * @return
     */
    public String updateByIdNotNull(Object entity,ProviderContext context){
        TableInfo table = tableInfoNotNull(context);
        return new SQL()
                .UPDATE(table.getTableName())
                .SET(Stream.of(table.getFields())
                        .filter(field -> value(entity, field) != null && !table.getPrimaryKeyColumn().equals(columnName(field)))
                        .map(field -> columnName(field) + " = " + bindParameter(field)).toArray(String[]::new))
                .WHERE(table.getPrimaryKeyColumn() + " = #{id}")
                .toString();
    }

    /**
     * 逻辑删除 根据主键id更新logicDelete注解
     * @param entity
     * @param context
     * @return
     */
    public String logicDeleteById(Object entity,ProviderContext context){
        TableInfo table = tableInfo(context);
        return new SQL()
                .UPDATE(table.getTableName())
                .SET(Stream.of(table.getFields())
                        .filter(field -> value(entity, field) != null && !table.getPrimaryKeyColumn().equals(columnName(field)))
                        .map(this::getCondition).toArray(String[]::new))
                .WHERE(table.getPrimaryKeyColumn() + " = #{id}")
                .toString();
    }

}
