package com.magneton.api2.core.fileprocessor;

import com.google.common.base.Joiner;
import com.magneton.api2.core.Api;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.builder.Extension;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API文档在不同的生成中，会存在不同的差异。
 * 如api-doc可以有文档头和尾。但是其他的类型可能不支持。
 * 所以，API的头以独立的方式支持而不对外开放配置。
 * 这里配置了一个api.md的文件格式支持，用来放置API头。
 * 如果对应的API生成工作器对api.md信息支持生成。
 * 则使用SpiService进行加载解析。
 * <p>
 * {@link com.magneton.api2.core.scan.AbstractFileScanner}扫描器工作机制中，会优先扫描附加目录文件。
 * 此时，如果扫描到了api.md，则会设置进去。
 * <p>
 * 如果主要目录中也存在api.md，则会进行覆盖。
 * <p>
 * 这个文件的注入在{@link com.magneton.api2.core.DefaultFileGarbageCollector}
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public class ApiMdFileProcessor implements CustomFileProcessor {

    public static final String NAME = "api.md";
    private Path filePath;

    @Override
    public void injectFile(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public String getFileContext() {
        if (filePath == null) {
            return null;
        }
        //读文件流
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath.toFile()), Api.INPUT_CHARSET));
            List<String> list = reader.lines().collect(Collectors.toList());
            String content = Joiner.on("\n").join(list);

            MutableDataSet options = new MutableDataSet();
            options.setFrom(ParserEmulationProfile.MARKDOWN);
            options.set(Parser.EXTENSIONS, Arrays.asList(new Extension[]{TablesExtension.create()}));
            Parser parser = Parser.builder(options).build();
            HtmlRenderer renderer = HtmlRenderer.builder(options).build();

            Node document = parser.parse(content);
            String html = renderer.render(document);
            return html;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public String name() {
        return NAME;
    }
}
