package com.jsq.demo.common.utils;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ReflectionUtil extends ReflectionUtils {
    public static Field[] getFields(Class<?> clazz, Predicate<Field> fieldFilter) {
        List<Field> fields = new ArrayList<>(32);
        while (Object.class != clazz && clazz != null) {
            // 获得该类的所有声明的字段，即包括public、private和protected，但是不包括父类的申明字段，
            // getFields：获得某个类的所有的公共（public）的字段，包括父类中的字段
            for (Field field : clazz.getDeclaredFields()) {
                if (fieldFilter != null && !fieldFilter.test(field)) {
                    continue;
                }
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    public static Field[] getFields(Class<?> clazz) {
        return getFields(clazz, null);
    }


}
