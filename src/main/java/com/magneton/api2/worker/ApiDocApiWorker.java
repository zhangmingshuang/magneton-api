package com.magneton.api2.worker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.magneton.api2.core.ApiFile;
import com.magneton.api2.core.ApiFileGenerater;
import com.magneton.api2.core.fileprocessor.ApiMdFileProcessor;
import com.magneton.api2.core.fileprocessor.CustomFileProcessor;
import com.magneton.api2.core.requestmapping.RequestMappingBuilder;
import com.magneton.api2.core.apiclassdoc.*;
import com.magneton.api2.core.requestmapping.RequestMappingBuilderChain;
import com.magneton.api2.core.ApiWorker;
import com.magneton.api2.core.spi.SpiServices;
import com.magneton.api2.util.ApiLog;
import com.sun.javadoc.ClassDoc;
import com.sun.tools.javadoc.MethodDocImpl;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ApiDoc文档实际生成器
 *
 * @author zhangmingshuang
 * @since 2019/9/12
 */
public class ApiDocApiWorker implements ApiWorker {

    public static final String NAME = "apidoc";

    @Override
    public ApiFileGenerater createGenerateFiles(Apis apis) {
        ApiFileGenerater apiFileGenerater = new ApiFileGenerater();
        apiFileGenerater.setEnvLib("template-apidoc.zip");
        apiFileGenerater.setFolder("api-doc");

        String apiData = this.parseApiData(apis);
        ApiFile apiDataFile = new ApiFile();
        apiDataFile.setFileName("api_data.js");
        apiDataFile.setContent(apiData);

        String apiProject = this.parseApiProject(apis);
        ApiFile apiProjectFile = new ApiFile();
        apiProjectFile.setFileName("api_project.js");
        apiProjectFile.setContent(apiProject);

        apiFileGenerater.setApiFiles(new ArrayList<>(Arrays.asList(apiDataFile, apiProjectFile)));
        return apiFileGenerater;
    }

    private String parseApiProject(Apis apis) {
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
        generator.put("version", "2.0.0");
        project.put("generator", generator);

        //APIDOC预期读取api.md
        CustomFileProcessor apiMdFile
                = SpiServices.getService(CustomFileProcessor.class, ApiMdFileProcessor.NAME);
        if (apiMdFile != null) {
            String fileContext = apiMdFile.getFileContext();
            if (fileContext != null && !fileContext.isEmpty()) {
                JSONObject header = new JSONObject();
                header.put("title", "Api Guide");
                header.put("content", fileContext);
                project.put("header", header);
            }
        }

        return "define(" + project.toJSONString() + ");";
    }

    private JSONObject parseApiDocMethod(ApiClass apiClass, ApiMethod apiMethod) {
        JSONObject methodApiDoc = new JSONObject();
        methodApiDoc.put("group", apiClass.getSimpleName());
        methodApiDoc.put("groupDescription", this.parseApiDocComment(apiClass.getApiComments()));
        methodApiDoc.put("groupTitle", apiClass.getSimpleName());

        ClassDoc classDoc = apiClass.getClassDoc();
        RequestMappingBuilder requestMappingBuilder
                = RequestMappingBuilderChain.getWithAnnotation(classDoc);
        String requestMapping, requestType;
        if (requestMappingBuilder == null) {
            requestMapping = "unkown";
            requestType = "unkown";
            ApiLog.error("unkown request mapping builder. name : " + apiClass.getQualifiedTypeName());
        } else {
            requestMapping = requestMappingBuilder.getRequestMapping(classDoc);
            requestType = requestMappingBuilder.getRequestMethod(classDoc);
        }
        methodApiDoc.put("type", requestType);
        methodApiDoc.put("url", requestMapping);

        //标题以注解中的第一行文本为标题
        String title = null;
        for (ApiComment apiComment : apiMethod.getApiComments()) {
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
            title = apiMethod.getSimpleName();
        }
        methodApiDoc.put("title", title);

        String version = this.getVersion(apiMethod.getSince());

        methodApiDoc.put("name", apiMethod.getSimpleName());
        methodApiDoc.put("version", version);
        methodApiDoc.put("filename", "");
        methodApiDoc.put("description", this.parseApiDocComment(apiMethod.getApiComments()));
        return methodApiDoc;
    }

    private String getVersion(String version) {
        if (version == null || version.isEmpty()) {
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
                String href = apiComment.getText();
                int methodIndex = href.indexOf("#");
                builder.append("<a href='");
                if (href.startsWith("http")) {
                    builder.append(href);
                    builder.append("' target='_blank'>");
                } else {
                    if (methodIndex == -1) {
                        builder.append("#api-" + href);
                    } else {
                        String controller, method;
                        if (href.startsWith("#")) {
                            //没有控制器
                            controller = ((MethodDocImpl) (apiComment.getTag().holder())).containingClass().name();
                            method = href.substring(1);
                        } else {
                            controller = href.substring(0, methodIndex);
                            method = href.substring(methodIndex + 1);
                            int endIndex = method.indexOf("(");
                            if (endIndex != -1) {
                                method = method.substring(0, endIndex);
                            }
                        }
                        builder.append("#api-" + controller + "-" + method);
                    }
                    builder.append("'>");
                }
                builder.append(href);
                builder.append("</a>");
            } else {
                builder.append(apiComment.getText());
            }
        });
        return builder.toString();
    }

    private String parseApiData(Apis apis) {
        JSONArray api = new JSONArray();
        List<ApiClass> apiClasses = apis.getApiClasses();
        for (ApiClass apiClass : apiClasses) {
            List<ApiMethod> apiMethods = apiClass.getApiMethods();
            if (apiMethods == null || apiMethods.size() < 1) {
                continue;
            }
            for (ApiMethod apiMethod : apiMethods) {
                JSONObject method = this.parseApiDocMethod(apiClass, apiMethod);
                //解析参数
                JSONObject parameter = this.parseApiDocParam(apiClass, apiMethod);
                if (parameter != null) {
                    method.put("parameter", parameter);
                }
                //解析响应参数
                JSONObject success = this.parseApiDocSuccess(apiClass, apiMethod);
                if (success != null) {
                    method.put("success", success);
                }
                api.add(method);
            }
        }
        return "define({\"api\": " + api.toJSONString() + "})";
    }

    private JSONObject parseApiDocSuccess(ApiClass apiClass, ApiMethod apiMethod) {
        ApiReturn apiReturn = apiMethod.getApiReturn();
        if (apiReturn == null) {
            return null;
        }
        String group = apiReturn.getTypeName();
        JSONArray parameters = new JSONArray();
        List<ApiField> link = apiReturn.getLink();
        if (link != null && link.size() > 0) {
            for (ApiField apiField : link) {
                JSONObject param = new JSONObject();
                param.put("group", group);
                param.put("type", apiField.getTypeName());
                param.put("field", apiField.getName());
                param.put("description", this.parseApiDocComment(apiField.getApiComments()));
                parameters.add(param);
            }
        } else {
            JSONObject param = new JSONObject();
            param.put("group", group);
            param.put("type", apiReturn.getTypeName());
            param.put("field", apiReturn.getTypeName());
            param.put("description", this.parseApiDocComment(apiReturn.getApiComments()));
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

    private JSONObject parseApiDocParam(ApiClass apiClass, ApiMethod apiMethod) {
        List<ApiParam> apiParams = apiMethod.getApiParams();
        if (apiParams == null || apiParams.size() < 1) {
            return null;
        }
        JSONArray parameters = new JSONArray();
        for (ApiParam apiParam : apiParams) {
            List<ApiField> link = apiParam.getLink();
            if (link != null && link.size() > 0) {
                for (ApiField apiField : link) {
                    JSONObject param = new JSONObject();
                    param.put("group", "Parameter");
                    param.put("type", apiField.getTypeName());
                    param.put("field", apiField.getName());
                    param.put("description", this.parseApiDocComment(apiField.getApiComments()));
                    parameters.add(param);
                }
            } else {
                JSONObject param = new JSONObject();
                param.put("group", "Parameter");
                param.put("type", apiParam.getTypeName());
                param.put("field", apiParam.getName());
                param.put("description", this.parseApiDocComment(apiParam.getApiComments()));
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
    public String name() {
        return NAME;
    }

}
