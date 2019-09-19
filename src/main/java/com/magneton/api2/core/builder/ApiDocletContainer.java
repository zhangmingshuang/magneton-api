package com.magneton.api2.core.builder;

import com.sun.javadoc.ClassDoc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangmingshuang
 * @since 2019/8/19
 */
public class ApiDocletContainer {
    /**
     * 用来保存Doclet读取到的文档
     */
    private Map<String, ClassDoc> classDocs = new HashMap<>();

    public void addClassDoc(ClassDoc classDoc) {
        classDocs.put(classDoc.name(), classDoc);
    }

    public Map<String, ClassDoc> getClassDocs() {
        return new HashMap<>(classDocs);
    }
}
