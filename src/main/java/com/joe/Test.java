package com.joe;

import com.joe.service.UserInterface;
import com.joe.service.UserService;
import com.spring.JoeApplicationContext;

/**
 * @author Joe
 * @date 2022/10/01 当前时间 下午 05:08
 */
public class Test {
    public static void main(String[] args) {

        //扫描-->创建单例bean
        JoeApplicationContext applicationContext = new JoeApplicationContext(AppConfig.class);

        UserInterface userService = (UserInterface) applicationContext.getBean("userService");
        userService.test();
        System.out.println(userService);
    }
}
