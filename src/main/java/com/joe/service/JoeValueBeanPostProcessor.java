package com.joe.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Joe
 * @date 2022/10/03 当前时间 下午 06:24
 */
@Component
public class JoeValueBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(JoeValue.class)) {
                field.setAccessible(true);
                try {
                    field.set(bean,field.getAnnotation(JoeValue.class).value());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return bean;
    }
}

