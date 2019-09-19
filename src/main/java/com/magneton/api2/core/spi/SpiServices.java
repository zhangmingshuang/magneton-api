package com.magneton.api2.core.spi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public final class SpiServices {

    private static final Map<Class, Map<String, Object>> services = new HashMap<>();

    public static <T extends Spi> T getService(Class<T> clazz, String name) {
        Map<String, Object> entry = services.get(clazz);
        if (entry == null) {
            synchronized (SpiServices.class) {
                if (entry == null) {
                    Map map = SpiServices.load(clazz);
                    entry = services.putIfAbsent(clazz, map);
                    if (entry == null) {
                        entry = map;
                    }
                }
            }
        }
        Object object = entry.get(name);
        if (object == null) {
            return null;
        }
        return (T) object;
    }

    private static Map<String, Object> load(Class<? extends Spi> clazz) {
        ServiceLoader services = ServiceLoader.load(clazz);
        Iterator iterator = services.iterator();
        Map<String, Object> map = new HashMap<>();
        for (; iterator.hasNext(); ) {
            Object next = iterator.next();
            map.put(((Spi) next).name(), next);
        }
        return map;
    }

}
