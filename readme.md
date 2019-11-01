# Magneton-Api
根据JAVADOC文档生成对应的API文档


##示例用法
```
 -oc utf-8 -s 要生成的API项目目录
 -se 扩展目录，多模块时的Class依赖目录
 -sf .*com\\project\\controller\\.*Controller.java #指定扫描文件过滤
 -o API文档输出目录
 -ignore [attribute] #指定注释中包含[attribute]的注释方法或字段过滤
 -ignore [ignore] #指定注释中包含[attribute]的注释方法或字段过滤
 apidoc #指定API生成模板
  # 模板子命令
  -h api.md #指定ApiDoc的API指南文件
  -e com.sgcc.wx.common.error.ErrorCode #指定全局错误定义代码文件
```

##主参数说明
   - -env
      强制刷新API生成目录文档
      Default: false
   - -h, -help, -?
      帮助文档
   - -ignore
      注释指定过滤文本，如果注释文本首行或者Param注释中包含文本（忽略大小写），则过滤该方法或参数
      Default: [[Ignore]]
   - -o, -output
      API文档输出目录
      Default: E:\magneton-projects\magneton-api
   - -oc, -output-charset
      配置输出文件编码
      Default: utf-8
   - -ptf, -param-type-filter
      指定过滤参数类型
      Default: [HttpServletRequest, HttpServletResponse]
   - -s, -scan
      配置API文件目录，默认为当前目录
      Default: [E:\magneton-projects\magneton-api]
   - -se, -scan-extend
      配置扩展关联目录，可以用来关联类引用
      Default: []
   - -sef, -scan-extend-file-filter
      配置扩展关联目录文件过滤规则
      Default: .*.
   - -sf, -scan-file-filter
      配置要API文件过滤规则
      Default: .*Controller\.java
   - -sc, -source-charset
      配置解析文件编码
      Default: utf-8
      
## 子参数说明
  `Example: java -jar Magneton-api.jar apidoc`
#### apidoc [options]
  ```
  Options:
      -e, -error
        错定全局错误码类名
      -h, -header
        指定API头，支持MD格式
  ```