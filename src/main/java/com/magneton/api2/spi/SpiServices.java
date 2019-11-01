package com.magneton.api2.spi;

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

    public static <T extends Spi> void regService(Class<T> clazz, LazySpi lazySpi) {
        Map<String, Object> entry = services.get(clazz);
        if (entry == null) {
            entry = new HashMap<>();
            services.put(clazz, entry);
        }
        entry.put(lazySpi.name(), lazySpi);
    }

    public static <T extends Spi> T getService(Class<T> clazz, String name) {
        Map<String, Object> entry = getServices(clazz);
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
            if (LazySpi.class.isAssignableFrom(next.getClass())) {
                throw new RuntimeException("lazyspi can't in service spi.");
            }
            String[] names = ((Spi) next).name();
            for (String name : names) {
                map.put(name, next);
            }
        }
        return map;
    }

    public static <T extends Spi> Map<String, Object> getServices(Class<T> clazz) {
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
        return entry;
    }
}
