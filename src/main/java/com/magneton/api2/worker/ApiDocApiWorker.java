package com.magneton.api2.worker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.magneton.api2.builder.ApiDoclet;
import com.magneton.api2.commander.ApiCommander;
import com.magneton.api2.commander.CommonApiCommander;
import com.magneton.api2.core.ApiConstant;
import com.magneton.api2.generater.ApiFile;
import com.magneton.api2.generater.ApiFileGenerater;
import com.magneton.api2.core.requestmapping.RequestMappingBuilder;
import com.magneton.api2.builder.doc.*;
import com.magneton.api2.core.requestmapping.RequestMappingBuilderChain;
import com.magneton.api2.core.ApiWorker;
import com.magneton.api2.scanner.FileCollector;
import com.magneton.api2.util.ApiLog;
import com.magneton.api2.util.ApiTagUtil;
import com.magneton.service.core.util.StringUtil;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.tools.javadoc.ClassDocImpl;
import com.sun.tools.javadoc.MethodDocImpl;

import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ApiDoc文档实际生成器
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public class ApiDocApiWorker implements ApiWorker {

    public static final String[] NAME = {"apidoc"};

    private ApiDocCommander apiDocCommander = new ApiDocCommander();
    private ApiDocHeaderCollector apiDocHeaderCollector;
    private CommonApiCommander commonApiCommander;

    @Override
    public ApiCommander apiCommander() {
        return apiDocCommander;
    }

    @Override
    public FileCollector[] fileCollector() {
        this.apiDocHeaderCollector = new ApiDocHeaderCollector(this.apiDocCommander);
        return new FileCollector[]{
            apiDocHeaderCollector
        };
    }

    @Override
    public void afterApiCommanderSet(CommonApiCommander commonApiCommander) {
        this.commonApiCommander = commonApiCommander;
    }

    @Override
    public ApiFileGenerater createGenerateFiles(Apis apis) {
        ApiFileGenerater apiFileGenerater = new ApiFileGenerater();
        apiFileGenerater.setEnvLib("template-apidoc.zip");
        apiFileGenerater.setFolder("api-doc");

        HashSet apiDocOrders = new LinkedHashSet();
        String apiData = this.parseApiData(apis, apiDocOrders);
        ApiFile apiDataFile = new ApiFile();
        apiDataFile.setFileName("api_data.js");
        apiDataFile.setContent(apiData);

        String apiProject = this.parseApiProject(apis, apiDocOrders);
        ApiFile apiProjectFile = new ApiFile();
        apiProjectFile.setFileName("api_project.js");
        apiProjectFile.setContent(apiProject);

        apiFileGenerater.setApiFiles(new ArrayList<>(Arrays.asList(apiDataFile, apiProjectFile)));
        return apiFileGenerater;
    }

    private String parseApiProject(Apis apis, Collection apiDocOrders) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new Date());

        JSONObject project = new JSONObject();
        project.put("name", "");
        project.put("version", "0.0.0");
        project.put("sampleUrl", false);
        project.put("defaultVersion", "0.0.0");
        project.put("apdoc", "0.0.0");

        JSONObject generator = new JSONObject();
        generator.put("name", "magneton-api");
        generator.put("time", now);
        generator.put("url", "");
        generator.put("version", ApiConstant.VERSION);
        project.put("generator", generator);

        //APIDOC预期读取api.md
        String fileContext = apiDocHeaderCollector.doParse(commonApiCommander.getSourceCharset());
        if (fileContext != null && !fileContext.isEmpty()) {
            JSONObject header = new JSONObject();
            header.put("title", "Api Guide");
            header.put("content", fileContext);
            project.put("header", header);
        }

        if (apiDocOrders != null && apiDocOrders.size() > 0) {
            project.put("order", apiDocOrders);
        }
        return "define(" + project.toJSONString() + ");";
    }

    private JSONObject parseApiDocMethod(ApiClassDoc apiClass, ApiMethodDoc apiMethodDoc) {
        JSONObject methodApiDoc = new JSONObject();
        methodApiDoc.put("group", apiClass.getSimpleName());
        methodApiDoc.put("groupDescription", this.parseApiDocComment(apiClass.getApiComments()));
        methodApiDoc.put("groupTitle", apiClass.getSimpleName());

        ClassDoc classDoc = apiClass.getClassDoc();
        RequestMappingBuilder requestMappingBuilder
            = RequestMappingBuilderChain.getWithAnnotation(classDoc);

        String[] requestMappings;
        String url = "";
        String requestType;

        if (requestMappingBuilder == null) {
            return null;
        } else {
            requestMappings = requestMappingBuilder.getRequestMapping(classDoc);
        }

        MethodDoc methodDoc = apiMethodDoc.getMethodDoc();

        if (methodDoc.isPrivate()) {
            return null;
        }

        RequestMappingBuilder methodRequestMapingBuilder
            = RequestMappingBuilderChain.getWithAnnotation(methodDoc);

        if (methodRequestMapingBuilder == null) {
            return null;
        } else {
            if (requestMappings.length > 1) {
                url = Arrays.toString(requestMappings);
            } else {
                url = requestMappings[0];
            }

            requestType = requestMappingBuilder.getRequestMethod(methodDoc);
            String[] methodRequestMappings = requestMappingBuilder.getRequestMapping(methodDoc);
            if (methodRequestMappings.length > 1) {
                url += Arrays.toString(methodRequestMappings);
            } else {
                url += methodRequestMappings[0];
            }
        }

        methodApiDoc.put("type", requestType);
        methodApiDoc.put("url", url);

        //标题以注解中的第一行文本为标题
        String title = null;
        for (ApiComment apiComment : apiMethodDoc.getApiComments()) {
            if (apiComment.isLink()) {
                continue;
            }
            String[] split = apiComment.getText().split("\\n");
            for (String s : split) {
                if (s.equals("<p>")) {
                    continue;
                }
                if (s.equals("<pre>")) {
                    continue;
                }
                title = s;
                break;
            }
            if (title != null) {
                break;
            }
        }
        if (title == null) {
            title = apiMethodDoc.getSimpleName();
        }
        methodApiDoc.put("title", title);

        String version = this.getVersion(apiMethodDoc.getSince());

        methodApiDoc.put("name", apiMethodDoc.getSimpleName());
        methodApiDoc.put("version", version);
        methodApiDoc.put("filename", "");
        methodApiDoc.put("description", this.parseApiDocComment(apiMethodDoc.getApiComments()));
        return methodApiDoc;
    }

    private String getVersion(String version) {
        if (version == null || version.isEmpty()) {
            return "0.0.0";
        }
        if (version.indexOf(".") == -1) {
            ApiLog.error("error api " + version + " to api-doc. set default version.");
            return "0.0.0";
        }
        return version;
    }

    private String parseApiDocComment(List<ApiComment> apiComments) {
        if (apiComments == null || apiComments.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        apiComments.forEach(apiComment -> {
            if (apiComment.isLink()) {
                Tag tag = apiComment.getTag();
                String href = apiComment.getText();
                int methodIndex = href.indexOf("#");
                if (href.startsWith("http")) {
                    builder.append("<a href='")
                           .append(href)
                           .append("'")
                           .append(href)
                           .append(" target='_blank'>");
                } else {
                    builder.append("<a href='");
                    if (methodIndex == -1) {
                        builder.append("#api-").append(href).append("'>");
                        builder.append(href);
                        builder.append("</a>");
                    } else {
                        String controller, method;
                        if (href.startsWith("#")) {
                            //没有控制器
                            if (ApiTagUtil.isSee(apiComment.getTag())
                                && ((SeeTag) apiComment.getTag()).referencedClass() != null) {
                                controller = ((SeeTag) apiComment.getTag()).referencedClass().name();
                            } else {
                                controller = "#";
                            }
                            method = href.substring(1);
                        } else {
                            controller = href.substring(0, methodIndex);
                            method = href.substring(methodIndex + 1);
                            int endIndex = method.indexOf("(");
                            if (endIndex != -1) {
                                method = method.substring(0, endIndex);
                            }
                        }
                        builder.append("#api-").append(controller).append("'>")
                               .append(controller).append("</a>#<a href='");

                        builder.append("#api-" + controller + "-" + method);
                        builder.append("'>").append(method).append("</a>");
                    }
                }
            } else {
                builder.append(apiComment.getText());
            }
        });
        return builder.toString();
    }

    private String parseApiData(Apis apis, Collection apiDocOrders) {
        JSONArray apiDocs = new JSONArray();

        //APIDOC预期读取全局错误描述码
        JSONObject errorCodeMethod =
            this.parseApiErrorCode(this.apiDocCommander.getErrorClass());
        if (errorCodeMethod != null) {
            apiDocOrders.add(errorCodeMethod.get("group"));
            apiDocs.add(errorCodeMethod);
        }

        List<ApiClassDoc> apiClasses = apis.getApiClasses();
        for (ApiClassDoc apiClass : apiClasses) {
            if (apiClass.getSimpleName().equals("SmartPaymentController")) {
                System.out.println("11");
            }
            List<ApiMethodDoc> apiMethodDocs = apiClass.getApiMethodDocs();
            if (apiMethodDocs == null || apiMethodDocs.size() < 1) {
                continue;
            }
            for (ApiMethodDoc apiMethodDoc : apiMethodDocs) {
                JSONObject method = this.parseApiDocMethod(apiClass, apiMethodDoc);
                if (method == null) {
                    continue;
                }
                //解析参数
                JSONObject parameter = this.parseApiDocParam(apiClass, apiMethodDoc);
                if (parameter != null) {
                    method.put("parameter", parameter);
                }
                //解析响应参数
                JSONObject success = this.parseApiDocSuccess(apiClass, apiMethodDoc);
                if (success != null) {
                    method.put("success", success);
                }
                apiDocs.add(method);
                apiDocOrders.add(method.get("group"));
            }
        }
        //所有的引用，生成See
        JSONArray vos = this.parseApiVo(apis.getApiSessClasses());
        if (vos != null && vos.size() > 0) {
            for (int i = 0; i < vos.size(); i++) {
                JSONObject vo = vos.getJSONObject(i);
                apiDocOrders.add(vo.get("group"));
            }
            apiDocs.addAll(vos);
        }
        return "define({\"api\": " + apiDocs.toJSONString() + "})";
    }

    private JSONArray parseApiVo(List<ApiClassDoc> apiSessClasses) {
        if (apiSessClasses == null || apiSessClasses.size() < 1) {
            return null;
        }
        JSONArray vos = new JSONArray();
        for (ApiClassDoc apiClassDoc : apiSessClasses) {
            ClassDoc classDoc = apiClassDoc.getClassDoc();
            String name = apiClassDoc.getSimpleName();

            if ("CertificateTypeEnum".equals(name)) {
                System.out.println("1");
            }
            if (classDoc.superclassType().typeName().toLowerCase().equals("enum")) {
                //一个枚举

            }

            List<ApiFieldDoc> apiFieldDocs = ApiFieldDoc.parseApiFields(classDoc);

            JSONObject methodApiDoc = new JSONObject();
            methodApiDoc.put("group", name);
            methodApiDoc.put("groupTitle", name);
            methodApiDoc.put("type", name);
            methodApiDoc.put("url", apiClassDoc.getQualifiedTypeName());
            methodApiDoc.put("title", name);
            methodApiDoc.put("name", apiClassDoc.getQualifiedTypeName());
            methodApiDoc.put("version", getVersion(apiClassDoc.getVersion()));
            methodApiDoc.put("filename", "");

            JSONArray parameters = new JSONArray();

            if (apiFieldDocs != null) {
                for (ApiFieldDoc apiFieldDoc : apiFieldDocs) {
                    String value = apiFieldDoc.getFieldDoc().constantValueExpression();
                    JSONObject param = new JSONObject();
                    param.put("group", "Parameter");
                    param.put("type", "");
                    if (value != null) {
                        param.put("field", apiFieldDoc.getName() + " = " + value);
                    } else {
                        param.put("field", apiFieldDoc.getName());
                    }
                    param.put("description",
                              this.parseApiDocComment(
                                  ApiComment.parseApiComments(apiFieldDoc.getFieldDoc().inlineTags())));
                    parameters.add(param);
                }
            }
            JSONObject parameter = new JSONObject();
            JSONObject fields = new JSONObject();
            fields.put("Paramter", parameters);
            parameter.put("fields", fields);

            methodApiDoc.put("parameter", parameter);

            vos.add(methodApiDoc);
        }

        return vos;
    }

    private JSONObject parseApiErrorCode(String errorClass) {
        if (StringUtil.isEmpty(errorClass)) {
            return null;
        }
        ClassDoc classDoc = ApiDoclet.getClassDoc(errorClass);
        if (classDoc == null) {
            return null;
        }
        ApiClassDoc errorCodes = ApiClassDoc.parseApiClass(classDoc);
        String name = errorCodes.getSimpleName();
        List<ApiFieldDoc> apiFieldDocs = ApiFieldDoc.parseApiFields(classDoc);

        JSONObject methodApiDoc = new JSONObject();
        methodApiDoc.put("group", errorCodes.getQualifiedTypeName());
        methodApiDoc.put("groupTitle", name);
        methodApiDoc.put("type", name);
        methodApiDoc.put("url", name);
        methodApiDoc.put("title", name);
        methodApiDoc.put("name", name);
        methodApiDoc.put("version", getVersion(errorCodes.getVersion()));
        methodApiDoc.put("filename", "");

        JSONArray parameters = new JSONArray();

        if (apiFieldDocs != null) {
            for (ApiFieldDoc apiFieldDoc : apiFieldDocs) {
                JSONObject param = new JSONObject();
                param.put("group", "Parameter");
                param.put("type", "");
                param.put("field",
                          apiFieldDoc.getName() + " = " + apiFieldDoc.getFieldDoc()
                                                                     .constantValueExpression());
                param.put("description",
                          this.parseApiDocComment(
                              ApiComment.parseApiComments(apiFieldDoc.getFieldDoc().inlineTags())));
                parameters.add(param);
            }
        }
        JSONObject parameter = new JSONObject();
        JSONObject fields = new JSONObject();
        fields.put("ErrorCode", parameters);
        parameter.put("fields", fields);

        methodApiDoc.put("parameter", parameter);

        return methodApiDoc;
    }

    private JSONObject parseApiDocSuccess(ApiClassDoc apiClass, ApiMethodDoc apiMethodDoc) {
        ApiReturnDoc apiReturnDoc = apiMethodDoc.getApiReturnDoc();
        if (apiReturnDoc == null) {
            return null;
        }
        String group = apiReturnDoc.getTypeName();
        JSONArray parameters = new JSONArray();
        List<ApiFieldDoc> links = apiReturnDoc.getLinks();
        if (links != null && links.size() > 0) {
            for (ApiFieldDoc apiFieldDoc : links) {
                JSONObject param = new JSONObject();
                param.put("group", group);
                param.put("type", apiFieldDoc.getTypeName());
                param.put("field", apiFieldDoc.getName());
                param.put("description", this.parseApiDocComment(apiFieldDoc.getApiComments()));
                parameters.add(param);
            }
        } else {
            JSONObject param = new JSONObject();
            param.put("group", group);
            param.put("type", apiReturnDoc.getTypeName());
            param.put("field", apiReturnDoc.getTypeName());
            param.put("description", this.parseApiDocComment(apiReturnDoc.getApiComments()));
            parameters.add(param);
        }
        if (parameters.size() < 1) {
            return null;
        }
        JSONObject parameter = new JSONObject();
        JSONObject fields = new JSONObject();
        fields.put(group, parameters);
        parameter.put("fields", fields);
        return parameter;
    }

    private JSONObject parseApiDocParam(ApiClassDoc apiClass, ApiMethodDoc apiMethodDoc) {
        List<ApiParamDoc> apiParamDocs = apiMethodDoc.getApiParamDocs();
        if (apiParamDocs == null || apiParamDocs.size() < 1) {
            return null;
        }
        JSONArray parameters = new JSONArray();
        for (ApiParamDoc apiParamDoc : apiParamDocs) {
//            if(apiParamDoc.getTypeName().equals("com.sgcc.wx.service.entity.query.AccountPasswordChangeQuery")){
//                System.out.println("1");
//            }
            List<ApiFieldDoc> link = apiParamDoc.getLinks();
            if (link != null && link.size() > 0) {
                for (ApiFieldDoc apiFieldDoc : link) {
                    JSONObject param = new JSONObject();
                    param.put("group", "Parameter");
                    param.put("type", apiFieldDoc.getTypeName());
                    param.put("field", apiFieldDoc.getName());
                    param.put("description", this.parseApiDocComment(apiFieldDoc.getApiComments()));
                    parameters.add(param);
                }
            } else {
                JSONObject param = new JSONObject();
                param.put("group", "Parameter");
                param.put("type", apiParamDoc.getTypeName());
                param.put("field", apiParamDoc.getName());
                param.put("description", this.parseApiDocComment(apiParamDoc.getApiComments()));
                parameters.add(param);
            }
        }
        if (parameters.size() < 1) {
            return null;
        }
        JSONObject parameter = new JSONObject();
        JSONObject fields = new JSONObject();
        fields.put("Parameter", parameters);
        parameter.put("fields", fields);
        return parameter;
    }

    @Override
    public String[] name() {
        return NAME;
    }

}
