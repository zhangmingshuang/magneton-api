package com.magneton.api2.core.builder;

import com.magneton.api2.core.Api;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.RootDoc;

import java.util.List;

/**
 * javadoc doclet 的支持类
 *
 * @author zhangmingshuang
 * @since 2019/6/18
 */
public class ApiDoclet extends Doclet {

    private static final ThreadLocal THREAD_LOCAL = new ThreadLocal();

    public static void addCollector(List<ClassDoc> list) {
        THREAD_LOCAL.set(list);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

    /**
     * Doclet start
     *
     * @param rootDoc RootDoc
     * @return boolean
     */
    public static boolean start(RootDoc rootDoc) {
        ClassDoc[] classDoces = rootDoc.classes();
        List<ClassDoc> list = (List<ClassDoc>) THREAD_LOCAL.get();
        for (ClassDoc classDoc : classDoces) {
            list.add(classDoc);
        }
        return true;
    }

}
