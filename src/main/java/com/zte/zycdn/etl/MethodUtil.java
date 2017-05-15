package com.zte.zycdn.etl;

import com.zte.zycdn.core.config.SysConfig;

import java.util.Map;

/**
 * Created by 10190203 on 2017/5/8.
 */
public class MethodUtil {
    public String pipe(String[] args){
        return args[0] + ":hello";
    }
    public String pipe(){
        return "hello world";
    }

    public String getDimenData(String dimenType,String key){
        Map<String,String> dimenMap = SysConfig.dimenData.get(dimenType);
        String value = dimenMap.get(key);
        return value!=null?value:"default";
    }

    public String hello(String[] args){
        return args[0] + "hello";
    }
}
