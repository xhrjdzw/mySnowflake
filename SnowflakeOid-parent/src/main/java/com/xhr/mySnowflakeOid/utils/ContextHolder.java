package com.xhr.mySnowflakeOid.utils;

import org.springframework.context.ApplicationContext;

/**
 * @author 徐浩然
 * @version ContextHolder, 2017-09-14
 */
public class ContextHolder
{
    private static ApplicationContext context;

    public ContextHolder() {
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext context) {
        if(context != null) {
            ContextHolder.context = context;
        }

    }
}
