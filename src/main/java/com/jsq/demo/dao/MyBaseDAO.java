package com.jsq.demo.dao;


import com.jsq.demo.common.utils.mybasedao.MyBaseDAOEnum;
import com.jsq.demo.common.utils.mybasedao.MyBaseSqlProvider;
import org.apache.ibatis.annotations.*;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * 数据库通用字段
 * @param <Entity>
 */
public interface MyBaseDAO<Entity> {
    /**
     * 根据id查询
     * @param id
     * @return
     */
    @SelectProvider(type = MyBaseSqlProvider.class,method = MyBaseDAOEnum.FIND_ONE)
    Entity findOne(String id);
    /**
     * 根据条件查询单个数据 根据主键返回最新一条数据
     *
     * @param entity
     * @return
     */
    @SelectProvider(type = MyBaseSqlProvider.class, method = MyBaseDAOEnum.FIND_ONE_BY_ENTITY)
    Entity findOneByEntity(Entity entity);

    /**
     * 插入一条记录
     * @param e
     * @return
     */
    @InsertProvider(type = MyBaseSqlProvider.class, method = MyBaseDAOEnum.INSERT)
    @Options(useGeneratedKeys = true)
    Integer insert(Entity e);

    /**
     * 根据主键id更新实体，若实体field为null，则对应数据库的字段也更新为null
     * @param entity
     * @return
     */
    @UpdateProvider(type = MyBaseSqlProvider.class, method = MyBaseDAOEnum.UPDATE_BY_ID)
    Integer updateById(Entity entity);

    /**
     * 根据主键id更新实体，若实体field为null 则不更新
     * @param entity
     * @return
     */
    @UpdateProvider(type = MyBaseSqlProvider.class, method = MyBaseDAOEnum.UPDATE_BY_ID_NOT_NULL)
    Integer updateByIdNotNull(Entity entity);

    /**
     * 逻辑删除
     * @param id
     * @return
     */
    @UpdateProvider(type = MyBaseSqlProvider.class, method = MyBaseDAOEnum.LOGIC_DELETE_BY_ID)
    Integer logicDeleteById(Object id);
//
//    /**
//     * 根据查询条件搜索
//     * @param entity
//     * @return
//     */
//    @SelectProvider(type = MyBaseSqlProvider.class, method = MyBaseDAOEnum.FIND_BY_ENTITY)
//    List<Entity> findByEntity(Entity entity);
//
//    /**
//     * 根据查询条件计数
//     * @param entity
//     * @return
//     */
//    @SelectProvider(type = MyBaseSqlProvider.class, method = MyBaseDAOEnum.COUNT_BY_ENTITY)
//    Long countByEntity(Entity entity);
}
