package com.magneton.api2.core;

import com.magneton.api2.core.fileprocessor.ApiMdFileProcessor;
import com.magneton.api2.core.fileprocessor.CustomFileProcessor;
import com.magneton.api2.core.scan.FileCollector;
import com.magneton.api2.core.spi.SpiServices;
import com.magneton.api2.util.ApiLog;

import java.nio.file.Path;

/**
 * 默认的文件垃圾收集器
 * 可以用来收集文件扫描器不收集的文件
 *
 * @author zhangmingshuang
 * @since 2019/9/18
 */
public class DefaultFileGarbageCollector implements FileCollector {
    @Override
    public void chuck(Path path) {
        if (path == null) {
            return;
        }
        //扩展自定义文件处理
        String fileName = path.getFileName().toString();
        CustomFileProcessor service = SpiServices.getService(CustomFileProcessor.class, fileName);
        if (service != null) {
            service.injectFile(path);
            ApiLog.out("inject file " + path + " to " + service.getClass());
            return;
        }
//        ApiLog.out(path.toAbsolutePath().toString() + " == skip");
    }
}
