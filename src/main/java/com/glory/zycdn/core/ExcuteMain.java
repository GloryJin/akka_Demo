package com.glory.zycdn.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;


public class ExcuteMain
{
    private static final Log LOG = LogFactory.getLog(ExcuteMain.class);

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception
    {
        if (args.length < 1)
        {
            LOG.error(
                    "User command args less than 1, Usage: java -jar /home/mr/zycdnbi/bigdata-zycdnbi.jar com.glory.iptv.stat.JobTest");
        }
        Class jobclass = Class.forName(args[0]);
        Class argsClass = args.getClass();
        @SuppressWarnings("unchecked")
        Method method = jobclass.getMethod("exe", argsClass);
        System.exit((Integer) method.invoke(jobclass.newInstance(), new Object[]
                {args}));
    }
}
