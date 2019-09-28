package com.magneton.api2.scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用来放置被扫描到的文件
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public class HFiles {

    private List<HFile> subordinates;
    private List<HFile> primaries;

    public boolean isEmpty() {
        return (subordinates == null || subordinates.size() < 1)
            && (primaries == null || primaries.size() < 1);

    }

    public List<HFile> getSubordinates() {
        return subordinates;
    }

    public List<HFile> getPrimaries() {
        return primaries;
    }

    public Map<String, HFile> getPrimariesMap() {
        if (primaries == null) {
            return null;
        }
        Map<String, HFile> map = new HashMap<>((int) (this.primaries.size() * 1.4));
        primaries.forEach(hFile -> map.put(hFile.getName(), hFile));
        return map;
    }

    public void addSubordinates(List<HFile> files) {
        if (this.subordinates == null) {
            this.subordinates = new ArrayList<>(
                files.size() + (files.size() >> 1));
        }
        this.subordinates.addAll(files);
    }

    public void addPrimaries(List<HFile> files) {
        if (this.primaries == null) {
            this.primaries = new ArrayList<>(
                files.size() + (files.size() >> 1));
        }
        this.primaries.addAll(files);
    }
}
