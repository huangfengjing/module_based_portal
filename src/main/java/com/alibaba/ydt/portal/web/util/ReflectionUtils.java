/**
 *
 */
package com.alibaba.ydt.portal.web.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Reflection utility
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @since 2009-10-30 PM 12:35:45
 */
public class ReflectionUtils extends org.springframework.util.ReflectionUtils {

    /**
     * Get all fields include inherit from parent
     *
     * @param targetClass target class
     * @return field array
     */
    @SuppressWarnings("unchecked")
    public static Field[] getAllFields(Class targetClass) {
        final List<Field> fields = new ArrayList<Field>(32);
        doWithFields(targetClass, new FieldCallback() {
            public void doWith(Field field) throws IllegalArgumentException,
                    IllegalAccessException {
                fields.add(field);
            }
        });
        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * Get all fields include inherit from parent
     *
     * @param targetClass target class
     * @return field array
     */
    @SuppressWarnings("unchecked")
    public static Field[] getAllFields(Class targetClass, final String[] ignored) {
        final List<Field> fields = new ArrayList<Field>(32);
        doWithFields(targetClass, new FieldCallback() {
            public void doWith(Field field) throws IllegalArgumentException,
                    IllegalAccessException {
                for (String p : ignored) {
                    if (field.getName().equals(p)) {
                        return;
                    }
                }
                fields.add(field);
            }
        });
        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * Get field value, support nested field. Note that field must have a Getter
     *
     * @param o            object to get
     * @param propertyName field name
     * @return property name
     */
    public static Object getProperty(Object o, String propertyName) {
        try {
            int dot = propertyName.indexOf(".");
            if (dot == -1) {
                String methodName = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
                Method method = o.getClass().getMethod(methodName, new Class[]{});
                return method.invoke(o);
            } else {
                String parentProperty = propertyName.substring(0, dot);
                String subProperty = propertyName.substring(dot + 1);
                Object parent = getProperty(o, parentProperty);
                return getProperty(parent, subProperty);
            }
        } catch (Exception e) {
            // will ignore the NoSuchMethod exception or AccessDenied and so on
            return null;
        }
    }
}