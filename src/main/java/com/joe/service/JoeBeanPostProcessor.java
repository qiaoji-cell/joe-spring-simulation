package com.joe.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Joe
 * @date 2022/10/03 当前时间 下午 06:24
 */
@Component
public class JoeBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if(beanName.equals("userService")){
            //jdk动态代理
            Object proxyInstance = Proxy.newProxyInstance(JoeBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    // 切面
                    System.out.println("切面逻辑");

                    return method.invoke(bean,args);
                }
            });
            return proxyInstance;
        }
        return bean;
    }
}

