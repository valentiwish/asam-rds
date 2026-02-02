package com.asam.common.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * @Description: 判断对象是否为空
 * @Author: mln
 * @Create: 2018-08-22 11:09
 */
public class ObjectUtil {

    /**
     * 可以用于判断 String,Long,Map,Collection,Array,数组等  是否为空
     * @param o
     * @return 空：false   不空：true
     */
    @SuppressWarnings("all")
    public static boolean isNotEmpty(Object o)  {
        if(o == null) {
            return false;
        }
        if(o instanceof String) {
            if(((String)o).length() == 0){
                return false;
            }
        } else if(o instanceof Collection) {
            if(((Collection)o).isEmpty()){
                return false;
            }
        } else if(o.getClass().isArray()) {
            if(Array.getLength(o) == 0){
                return false;
            }
        } else if(o instanceof Map) {
            if(((Map)o).isEmpty()){
                return false;
            }
        }else {
            return true;
        }
        return true;
    }

    /**
     * 可以用于判断 String,Long,Map,Collection,Array,数组等  是否为空
     * @param o
     * @return  空：true    不空：false
     */
    @SuppressWarnings("all")
    public static boolean isEmpty(Object o)  {
        return !isNotEmpty(o);
    }
}
