package com.spring;

/**
 * @author Joe
 * @date 2022/10/03 当前时间 下午 05:06
 */
public class BeanDefinition {

    private Class type;
    private String scope;
    private String isLazy;

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getIsLazy() {
        return isLazy;
    }

    public void setIsLazy(String isLazy) {
        this.isLazy = isLazy;
    }
}
