package com.spring;

/**
 * @author Joe
 * @date 2022/10/03 当前时间 下午 06:22
 */
public interface BeanPostProcessor {

    default Object postProcessBeforeInitialization(Object bean,String beanName){
        return bean;
    }

    default Object postProcessAfterInitialization(Object bean,String beanName){
        return bean;
    }
}
