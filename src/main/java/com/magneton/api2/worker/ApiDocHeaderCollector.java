package com.magneton.api2.worker;

import com.google.common.base.Joiner;
import com.magneton.api2.core.ApiConstant;
import com.magneton.api2.scanner.FileCollector;
import com.magneton.service.core.util.StringUtil;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.builder.Extension;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zhangmingshuang
 * @since 2019/9/28
 */
public class ApiDocHeaderCollector implements FileCollector {

    private ApiDocCommander apiDocCommander;
    private Path file;

    public ApiDocHeaderCollector(ApiDocCommander apiDocCommander) {
        this.apiDocCommander = apiDocCommander;
    }

    @Override
    public Pattern[] patterns() {
        String header = apiDocCommander.getHeader();
        if (StringUtil.isEmpty(header)) {
            return null;
        }
        return new Pattern[]{
            Pattern.compile(apiDocCommander.getHeader())
        };
    }

    @Override
    public void collect(Path path) {
        this.file = path;
    }

    public String doParse(String charset) {
        if (this.file == null) {
            return null;
        }
        //读文件流
        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file.toFile()), charset));
            List<String> list = reader.lines().collect(Collectors.toList());
            String content = Joiner.on("\n").join(list);

            MutableDataSet options = new MutableDataSet();
            options.setFrom(ParserEmulationProfile.MARKDOWN);
            options
                .set(Parser.EXTENSIONS, Arrays.asList(new Extension[]{TablesExtension.create()}));
            Parser parser = Parser.builder(options).build();
            HtmlRenderer renderer = HtmlRenderer.builder(options).build();

            Node document = parser.parse(content);
            String html = renderer.render(document);
            return html;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
