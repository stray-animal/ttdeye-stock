package com.ttdeye.stock.common.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BeanToMapUtil {

	/**
     * 将一个 JavaBean 对象转化为一个  Map
     * @param bean 要转化的JavaBean 对象
     * @return 转化出来的  Map 对象
     * @throws IntrospectionException 如果分析类属性失败
     * @throws IllegalAccessException 如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map convertBean(Object bean){
        Class type = bean.getClass();
        Map returnMap = new HashMap();
        BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(type);
 
	        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
	        for (int i = 0; i< propertyDescriptors.length; i++) {
	            PropertyDescriptor descriptor = propertyDescriptors[i];
	            String propertyName = descriptor.getName();
	            if (!"class".equals(propertyName)) {
	                Method readMethod = descriptor.getReadMethod();
	                Object result = readMethod.invoke(bean, new Object[0]);
	                if (result != null && !"".equals(result)) {
	                    returnMap.put(propertyName, result);
	                } 
	            }
	        }
        } catch (Exception e) {
			e.printStackTrace();
		}
        return returnMap;
    }
}
