package com.glory.zycdn.core.util;

import com.glory.zycdn.etl.MethodUtil;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

public class ToolUtil {
    private Logger log = Logger.getLogger(ToolUtil.class);

    public static String callMethod(String methodname){
        try{
            Class jobclass = Class.forName("com.glory.zycdn.etl.MethodUtil");
            @SuppressWarnings("uncheck")
            Method method = jobclass.getMethod(methodname);
            return method.invoke(jobclass.newInstance()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String callMethod(String methodname,String[] args){
        try{
            Class jobclass = Class.forName("com.glory.zycdn.etl.MethodUtil");
            Class argsClass = args.getClass();
            @SuppressWarnings("uncheck")
            Method method = jobclass.getMethod(methodname,argsClass);
            return method.invoke(jobclass.newInstance(),new Object[]{args}).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
