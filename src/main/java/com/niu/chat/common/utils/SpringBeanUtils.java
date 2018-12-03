package com.niu.chat.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author: justinniu
 * @date: 2018-11-27 18:18
 * @desc:
 **/
public class SpringBeanUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (null == SpringBeanUtils.applicationContext) {
            SpringBeanUtils.applicationContext = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> c) {
        return getApplicationContext().getBean(c);
    }

    public static <T> T getBean(String name, Class<T> c) {
        return getApplicationContext().getBean(name, c);
    }
}
