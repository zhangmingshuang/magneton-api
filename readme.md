# Magneton-Api
根据JAVADOC文档生成对应的API文档

##模板支持
- API-DOC

##用法
java -jar magneton-api.jar -oc utf-8 -se ${project}

##参数说明
  - `-otype/-outputType` 输出类型，默认`apidoc`，当前只支持`apidoc`
  - `-f` 配置读取的API文件格式，默认`*Controller.java`
  - `-s/-scan` 配置搜索目录，默认为运行时目录
  - `-se` 配置搜索关联的目录，用来关联类引用。 如API扫描目录在`projectA`而引用的类在`projectB`。
  则使用 `-s projectA -se projectB`就可以进行关联引用。 支持多目录配置，以`,`分隔。
  目录的配置支持使用`${project}`来表示为当前目录的根目录。如:`${project}/test`表示运行时目录下的`test`目录。
  - `-o/-output` 配置输出目录，默认为运行目录下+模板类型。如：`${project}/api-doc`
  - `-oc` 配置输出文件编码
  - `-sc` 配置读取文件编码
  - `-pf/-param-filter` 以添加的方式配置解析时参数类型过滤，如：HttpServletRequest。多类型以,分隔。
   默认过滤HttpServletRequest&HttpServletResponse
  - `-pfn/-param-filter-new` 强制过滤过滤为参数-pf/-param-filter指定的过滤


