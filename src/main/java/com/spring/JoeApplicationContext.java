package com.spring;

import java.beans.Introspector;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Joe
 * @date 2022/10/01 当前时间 下午 05:09
 */
public class JoeApplicationContext {

    private Class configClass;
    private Map<String,BeanDefinition> beanDefinitionMap = new HashMap<>();
    /**
     * 单例池
     */
    private Map<String,Object> singletonObjects = new HashMap<>();
    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public JoeApplicationContext(Class configClass) {
        this.configClass = configClass;

        //扫描
        scan(configClass);

        beanDefinitionMap.forEach((beanName,beanDefinition)->{
            if(beanDefinition.getScope().equals("singleton")){
                //创建bean
               Object bean = createBean(beanName,beanDefinition);
               singletonObjects.put(beanName,bean);
            }
        });

    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();

        Object instance = null;
        try {
            instance = clazz.getConstructor().newInstance();

            //给类的属性赋值 从beanDefinitionMap找
            for (Field field : clazz.getDeclaredFields()) {

                if(field.isAnnotationPresent(AutoWired.class)){
                    field.setAccessible(true);
                    field.set(instance,getBean(field.getName()));

                }
                
            }

            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance,beanName);
            }



            if(instance instanceof InitializingBean){
                ((InitializingBean) instance).afterPropertiesSet();
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance,beanName);
            }





        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        //先写返回值 可以捕获的同时提升变量
        return instance;
    }




    public Object getBean(String beanName){
        if(!beanDefinitionMap.containsKey(beanName)){
            throw new NullPointerException();
        }

        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

        if(beanDefinition.getScope().equals("singleton")){
            //从单例池取bean
            Object singletonBean = singletonObjects.get(beanName);
            if(singletonBean==null){
                singletonBean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName,singletonBean);
            }
            return singletonBean;
        }else{
            //原型
            Object prototypeBean = createBean(beanName, beanDefinition);
            return prototypeBean;
        }

    }
    /**
     * 得到扫描路径
     * 遍历路径下class文件
     * 加载class文件得到对象
     * 判断注解 解析出beanName 解析scope
     * 得到beanDefinition 存储到Map
     * @param configClass
     */
    private void scan(Class configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value();
            //相对路径
            path = path.replace(".", "/");

//            System.out.println(path);

            //获取类加载器
            ClassLoader classLoader = JoeApplicationContext.class.getClassLoader();
            //找到target文件夹
            URL resource = classLoader.getResource(path);

            //转译中文路径
            //替换空格编码为空格
            path = resource.getPath().replace("%20"," ");
            //转译中文
            try {
                path = URLDecoder.decode(path,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            File file =new File(path);

            if(file.isDirectory()){
                for (File f : file.listFiles()) {
                    String absolute = f.getAbsolutePath();

                    //通过路径加载到jvm
                    absolute = absolute.substring(absolute.indexOf("com"),absolute.indexOf(".class"));
                    absolute = absolute.replace("\\",".");
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(absolute);

                        if(clazz.isAnnotationPresent(Component.class)){

                            //处理postProcessor 判断有没有实现postProcessor接口
                            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                BeanPostProcessor instance = (BeanPostProcessor) clazz.getConstructor().newInstance();
                                beanPostProcessorList.add(instance);
                            }else{
                                Component componentAnnotation = clazz.getAnnotation(Component.class);
                                String beanName = componentAnnotation.value();
                                if("".equals(beanName)){
                                    beanName = Introspector.decapitalize(clazz.getSimpleName());
                                }

                                //创建bean定义
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(clazz);

                                if(clazz.isAnnotationPresent(Scope.class)){
                                    Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
                                    String value = scopeAnnotation.value();
                                    beanDefinition.setScope(value);

                                }else{
                                    beanDefinition.setScope("singleton");
                                }

                                //保存到map 实现扫描
                                beanDefinitionMap.put(beanName,beanDefinition);

                            }


                        }
                    } catch (ClassNotFoundException | NoSuchMethodException e) {
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
    }

}
