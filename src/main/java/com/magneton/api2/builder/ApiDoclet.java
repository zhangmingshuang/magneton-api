package com.magneton.api2.builder;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.RootDoc;

import java.util.*;

/**
 * javadoc doclet 的支持类
 *
 * @author zhangmingshuang
 * @since 2019/6/18
 */
public class ApiDoclet extends Doclet {

    private static final ThreadLocal THREAD_LOCAL = new ThreadLocal();

    public static ClassDoc getClassDoc(String qualifiedTypeName) {
        Map<String, ClassDoc> map = (Map<String, ClassDoc>) THREAD_LOCAL.get();
        if (map == null) {
            return null;
        }
        return map.get(qualifiedTypeName);
    }

    /**
     * Doclet start
     *
     * @param rootDoc RootDoc
     * @return boolean
     */
    public static boolean start(RootDoc rootDoc) {
        ClassDoc[] classDoces = rootDoc.classes();
        Map<String, ClassDoc> map = new HashMap<>();
        for (ClassDoc classDoc : classDoces) {
            map.put(classDoc.qualifiedTypeName(), classDoc);
        }
        THREAD_LOCAL.set(map);
        return true;
    }

    public static Collection getClassDocs() {
        Map<String, ClassDoc> map = (Map<String, ClassDoc>) THREAD_LOCAL.get();
        if (map == null) {
            return Collections.EMPTY_LIST;
        }
        return map.values();
    }
}
