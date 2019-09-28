package com.magneton.api2.scanner;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

/**
 * @author zhangmingshuang
 * @since 2019/9/18
 */
@Setter
@Getter
public class HFile {
    private String name;
    private String fileName;
    private Path path;
}
