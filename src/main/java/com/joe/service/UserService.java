package com.joe.service;

import com.spring.*;

/**
 * @author Joe
 * @date 2022/10/01 当前时间 下午 05:08
 */
@Component("userService")
@Scope
public class UserService implements UserInterface, BeanNameAware {

    @AutoWired
    private OrderService orderService;

    @JoeValue("joe")
    private String name;

    private String beanName;

    @Override
    public void test(){
        System.out.println(beanName);
        System.out.println(name);
        System.out.println(orderService);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
