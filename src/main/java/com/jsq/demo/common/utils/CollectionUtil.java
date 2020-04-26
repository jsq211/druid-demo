package com.jsq.demo.common.utils;

import com.google.common.collect.Lists;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author jsq
 */
public class CollectionUtil {
    /**
     *
     * list 转 map
     * 去除重复数据
     * @param collection 集合
     * @param propertyName 属性名
     * @param exchange 当Key重复时操作状态 True 覆盖 False 跳过,默认为true
     * @param <K>
     * @param <V>
     * @jdk 1.7及以上
     * @return
     */
    public static <K,V> Map<K,V> transferMap(Collection collection ,String propertyName,Boolean exchange){
        checkInputParam(collection,propertyName);
        Map<K,V> map = new HashMap<>(collection.size());
        Boolean trans = null != exchange?exchange : true;
        for (Object obj : collection) {
            V value = (V) obj;
            try {
                Object object = PropertyUtils.getProperty(obj,propertyName);
                if (null == object){
                    continue;
                }
                if (null != object && trans){
                    map.put((K) object,value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("class has no such property value :" + propertyName);
            }  catch (InvocationTargetException e) {
                throw new RuntimeException("get property value error :" + propertyName);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("class has no such property name :"+ propertyName);
            }
        }
        return map;
    }

    /**
     *
     * list 转 map
     * 去除重复数据 当发生key重复时，进行覆盖
     * @param collection 集合
     * @param propertyName 属性名
     * @param <K>
     * @param <V>
     * @jdk 1.7及以上
     * @return
     */
    public static <K,V> Map<K,V> transferMap(Collection collection ,String propertyName){
        checkInputParam(collection,propertyName);
        Map<K,V> map = new HashMap<>(collection.size());
        for (Object obj : collection) {
            V value = (V) obj;
            try {
                Object object = PropertyUtils.getProperty(obj,propertyName);
                if (null == object){
                    continue;
                }
                if (null != object){
                    map.put((K) object,value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("class has no such property value :" + propertyName);
            }  catch (InvocationTargetException e) {
                throw new RuntimeException("get property value error :" + propertyName);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("class has no such property name :"+ propertyName);
            }
        }
        return map;
    }

    /**
     * 集合入参校验值 不能为空
     * @param collection
     * @param propertyName
     */
    private static void checkInputParam(Collection collection, String... propertyName) {
        if (CollectionUtils.isEmpty(collection)){
            throw new RuntimeException("collection is empty");
        }
        if (StringUtils.isEmpty(propertyName)){
            throw new RuntimeException("propertyName is empty");
        }
    }

    /**
     * list获取属性 默认过滤null值
     * @param collection
     * @param propertyName
     * @jdk 1.7及以上
     * @return
     */
    public static <E> List<E> transferListValue(Collection collection, String propertyName){
        checkInputParam(collection,propertyName);
        List list = Lists.newArrayList();
        for (Object obj :collection) {
            try {
                Object object = PropertyUtils.getProperty(obj,propertyName);
                if (null != object){
                    list.add(object);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("class has no such property value :" + propertyName);
            }  catch (InvocationTargetException e) {
                throw new RuntimeException("get property value error :" + propertyName);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("class has no such property name :"+ propertyName);
            }
        }
        return list;
    }

    /**
     * 根据入参属性值进行分组
     * @param collection 入参集合
     * @param propertyName 分类依据key
     * @param <K> key
     * @param <V>value
     * @jdk 1.7及以上
     * @return map<K,List>
     */
    public static <K,V extends Map<K, V>>Map<K,List> transferMapGroup(Collection collection, String propertyName){
        checkInputParam(collection,propertyName);
        Map<K,List> ans = new HashMap<>(collection.size()>8?8:collection.size());
        List list = new ArrayList();
        for (Object obj :collection){
            try {
                Object object = PropertyUtils.getProperty(obj,propertyName);
                if (null == object){
                    continue;
                }
                if (ans.containsKey(object)){
                    list = Lists.newArrayList(ans.get(object));
                    list.add(obj);
                    ans.put((K) object,list);
                }else {
                    ans.put((K) object,Lists.newArrayList(obj));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("class has no such property value :" + propertyName);
            }  catch (InvocationTargetException e) {
                throw new RuntimeException("get property value error :" + propertyName);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("class has no such property name :"+ propertyName);
            }
        }
        return ans;
    }

    /**
     * 将list按照规定条数划分子list
     * @param list 入参集合
     * @param size 划分子集的长度
     * @param <E>
     * @return
     */
    public static <E> List<List<E>> getSubList(List<E> list,int size){
        if (isEmpty(list)){
            return new ArrayList<>();
        }
        List<List<E>> result = new ArrayList<>();
        for (int begin = 0; begin < list.size(); begin = begin + size) {
            int end = (begin + size > list.size() ? list.size() : begin + size);
            result.add(list.subList(begin, end));
        }
        return result;
    }

    /**
     * 根据入参属性进行排序
     * @param list 集合
     * @param propertyName 排序字段
     * @param sort 正序、倒叙
     * @param
     * @return
     */
    public static void sortByPropertyConfig(List list ,String propertyName,Boolean sort){
        checkInputParam(list,propertyName);
        List ans = new ArrayList<>();
        Collections.sort(list, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                try {
                    Object obj1;
                    Object obj2;
                    if (sort){
                        obj1 = PropertyUtils.getProperty(o1,propertyName);
                        obj2 = PropertyUtils.getProperty(o2,propertyName);
                    }else {
                        obj1 = PropertyUtils.getProperty(o2,propertyName);
                        obj2 = PropertyUtils.getProperty(o1,propertyName);
                    }
                    if (null == obj1){
                        return  -1;
                    }
                    if (obj1 instanceof Number){
                        return ((Comparable)obj1).compareTo(obj2);
                    }
                    if (obj1 instanceof Date){
                        return (((Date) obj1).compareTo((Date)obj2));
                    }
                    if (obj1 instanceof String){
                        return obj1.toString().compareTo(obj2.toString());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("class has no such property value :" + propertyName);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("fieldName is empty :" + propertyName);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }


    public static void sortByMuiltProperty(List list ,Boolean sort,String... propertyName){
        AtomicInteger atomicInteger = new AtomicInteger(propertyName.length);

    }

        /**
         * 为空判断
         * @param collection
         * @return
         */
    public static Boolean isEmpty(@Nullable Collection collection){
        return (collection == null || collection.isEmpty());
    }
    /**
     * 非空判断
     * @param collection
     * @return
     */
    public static Boolean isNotEmpty(@Nullable Collection collection){
        return (collection != null && !collection.isEmpty());
    }
    /**
     * 为空判断
     * @param map
     * @return
     */
    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
    /**
     * 非空判断
     * @param map
     * @return
     */
    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }



}
