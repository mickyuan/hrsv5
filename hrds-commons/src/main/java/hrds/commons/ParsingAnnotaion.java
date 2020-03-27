package hrds.commons;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.*;
import fd.ng.core.exception.internal.FrameworkRuntimeException;
import fd.ng.core.exception.internal.RawlayerRuntimeException;
import fd.ng.core.utils.ClassUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.SystemUtil;
import fd.ng.core.utils.Validator;
import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.web.annotation.Action;
import fd.ng.web.annotation.UrlName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ParsingAnnotaion {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        try {
            // ClassLoader.getSystemClassLoader().getResource(resourceName);
            List<Class<?>> actionClassList = ClassUtil.getClassListByAnnotation("hrds", Action.class);
            Map<String, String> errorMethod = new HashMap<>();
            JSONArray jsonArray = new JSONArray();
            for (Class<?> actionClass : actionClassList) {
                final String className = actionClass.getName();
                Action actionAnnotation = actionClass.getAnnotation(Action.class); // 得到该类的注解

                final String packageName = actionClass.getPackage().getName();
                String actionLookupKey = actionAnnotation.UriExt(); // allActionMap 的key,
                if (StringUtil.isEmpty(actionLookupKey)) { // 使用包名作为 UriExt
                    actionLookupKey = "/" + packageName.replace(".", "/");
                } else { // 定义了 UriExt 属性
                    if (actionLookupKey.startsWith("^/")) { // UriExt的值作为全路径使用
                        actionLookupKey = actionLookupKey.substring(1);
                    } else {
                        // 把 UriExt 的值追加到包名路径后面
                        actionLookupKey = "/" + packageName.replace(".", "/") + "/" + actionLookupKey;
                    }
                }

                DocClass annotation = actionClass.getAnnotation(DocClass.class);

                String classdesc = "";
                String author = "";
                String createdate = "";
                if (annotation != null) {
                    classdesc = annotation.desc();
                    author = annotation.author();
                    createdate = annotation.createdate();
                }
                JSONObject jsonClass = new JSONObject();
                jsonClass.put("url", actionLookupKey);
                jsonClass.put("desc", classdesc);
                jsonClass.put("author", author);
                jsonClass.put("createdate", createdate);

                JSONArray jsonMethods = new JSONArray();//每个class有多少个方法
                // 得到当前 Action 类的所有方法
                Method[] actionMethods = actionClass.getDeclaredMethods();
                for (Method actionMethod : actionMethods) {

                    fd.ng.core.annotation.Method anMethod = actionMethod.getAnnotation(fd.ng.core.annotation.Method.class);// 得到该类的注解
                    if (anMethod == null) {
                        continue;
                    }
                    String apiDesc = anMethod.desc();//类的描述
                    //String apiLogicstep = anMethod.logicStep();//逻辑步骤
                    String apiLogicstep = StringUtil.EMPTY;
                    String example = anMethod.example();//样例代码

                    int methodModifier = actionMethod.getModifiers();
                    if (Modifier.isAbstract(methodModifier) || Modifier.isStatic(methodModifier))
                        continue; // 抽象和静态方法不处理
                    if (Modifier.isPublic(methodModifier)) { // 只检查 public 的方法
                        String curMethodName = actionMethod.getName();
                        UrlName urlName = actionMethod.getAnnotation(UrlName.class);
                        if (urlName != null) curMethodName = urlName.value();
                        Param[] params = new Param[0];

                        Params apiParams = actionMethod.getAnnotation(Params.class);
                        Map<String, Param> apiParamMap = new HashMap<String, Param>();
                        if (apiParams == null) {// 该方法没有多个注解
                            Param paramAnno = actionMethod.getAnnotation(Param.class);
                            if (paramAnno != null) {
                                params = new Param[]{paramAnno};
                            }
                        } else {
                            params = apiParams.value();
                        }

                        for (Param apiParam : params) {
                            String name = apiParam.name();
                            apiParamMap.put(name, apiParam);
                        }

                        //获取方法的参数
                        Parameter[] parameters = actionMethod.getParameters();
                        int paramSize = parameters.length;
                        /**
                         * 处理参数和注解不一致的情况
                         */
                        if (apiParamMap.size() != paramSize) {
                            errorMethod.put("paramError", actionLookupKey + "/" + curMethodName);
                        }

                        List<ParamsBean> pbList = new ArrayList<ParamsBean>();
                        for (int i = 0; i < paramSize; i++) {
                            Parameter param = parameters[i]; // 方法的参数对象
                            Class<?> paramType = param.getType(); // 方法的参数类型
                            String reqParamName = param.getName(); // 该参数在 request 中的名字。默认就是参数名
                            Param par = apiParamMap.get(reqParamName);
                            if (par == null) {
                                errorMethod.put("findName", actionLookupKey + "/" + curMethodName + ",参数名称" + reqParamName);
                                continue;
                            }
                            String desc = par.desc();//用于描述类、方法、参数、返回值的含义
                            boolean nullable = par.nullable();//该参数在 request 中是否可以不存在或为空。
                            String[] valueIfNull = par.valueIfNull();//对于可空变量，赋予的默认值（因为前端提交的数据有可能是字符数组）
                            boolean bean = par.isBean();//该参数是不是实体bean
                            String range = par.range();//用于描述参数和返回值的取值范围，"如：任意，4位数字，1-100之间的数字"。
                            String example1 = par.example();//例子数据，如10.78.90.22这样有特殊意义的数据

                            ParamsBean pb = new ParamsBean();
                            pb.setParamType(paramType);
                            pb.setReqParamName(reqParamName);
                            pb.setDesc(desc);
                            pb.setNullable(nullable);
                            pb.setValueIfNull(valueIfNull);
                            pb.setBean(bean);
                            pb.setRange(range);
                            pb.setExample1(example1);
                            pbList.add(pb);
                        }

                        Return parReurn = actionMethod.getAnnotation(Return.class);// 得到该类的注解
                        Class<?> returnType = actionMethod.getReturnType();
                        String desc = "";
                        String range = "";//逻辑步骤
                        boolean isbean = false;//是否为实体bean
                        if (parReurn != null) {
                            desc = parReurn.desc();//类的描述
                            range = parReurn.range();//逻辑步骤
                            isbean = parReurn.isBean();//样例代码
                        }
                        JSONObject jsonMethod = new JSONObject();
                        //方法描述
                        jsonMethod.put("url", actionLookupKey + "/" + curMethodName);
                        jsonMethod.put("apiDesc", apiDesc);
                        jsonMethod.put("apiLogicstep", apiLogicstep);
                        jsonMethod.put("example", example);
                        //参数
                        JSONArray jsonArrayParams = params(pbList);
                        //返回值
                        List<String> beanList = StringUtil.split(returnType.getName(), ".");
                        String datatype = beanList.get(beanList.size() - 1);
                        if ("void".equals(datatype)) {
                            desc = StringUtil.isEmpty(desc) ? "没有任何返回数据" : desc;
                            range = StringUtil.isEmpty(range) ? "空" : desc;
                        }
                        JSONObject jsonReturn = new JSONObject();
                        jsonReturn.put("returnDesc", desc);
                        jsonReturn.put("returnRange", range);
                        jsonReturn.put("returnType", datatype);

                        JSONObject mess = new JSONObject();
                        mess.put("method", jsonMethod);
                        mess.put("params", jsonArrayParams);
                        mess.put("return", jsonReturn);
                        jsonMethods.add(mess);
                    }
                }
                JSONObject jsonCC = new JSONObject();
                jsonCC.put("methods", jsonMethods);
                jsonCC.put("jsonClass", jsonClass);
                jsonArray.add(jsonCC);
            }
            if (errorMethod.size() != 0) {
                Set<Map.Entry<String, String>> entries = errorMethod.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    String key = entry.getKey();
                    if ("paramError".equals(key)) {
                        logger.error("方法：" + errorMethod.get(key) + "注解参数与实际参数不一致，请进行修改");
                    }
                    if ("findName".equals(key)) {
                        logger.error("方法：" + errorMethod.get(key) + "没有找到对应的注解描述信息，请修改");
                    }
                }
            }
            SaveWriteFile("d://data.json", jsonArray.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static JSONArray params(List<ParamsBean> apiParamsList) {
        JSONArray jsonParams = new JSONArray();
        for (ParamsBean pb : apiParamsList) {
            String name = pb.getReqParamName();
            Class<?> paramType = pb.getParamType();
            String desc = pb.getDesc();
            boolean isnullable = pb.isNullable();
            String[] valueIfNull = pb.getValueIfNull();
            String vnull = "";
            if (valueIfNull.length == 0) {
                vnull = "";
            } else {
                vnull = String.join(",", valueIfNull);
            }
            boolean isbean = pb.isBean();
            String range1 = pb.getRange();
            String example1 = pb.getExample1();
            if (isbean) {
                String paramRange = "实体:"+name;
                if (paramType.isArray()) {
                    paramType = paramType.getComponentType();
                    paramRange = "实体数组:"+name;
                }
                Field[] declaredFields = paramType.getDeclaredFields();
                for (Field field : declaredFields) {
                    DocBean apiModel = field.getAnnotation(DocBean.class);
                    if (apiModel != null) {
                        List<String> beanList = StringUtil.split(apiModel.dataType().getName(), ".");
                        String datatype = beanList.get(beanList.size() - 1);
                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("paramName", apiModel.name());
                        jsonParam.put("paramDesc", apiModel.value());
                        jsonParam.put("paramRange", paramRange);
                        jsonParam.put("paramRequired", apiModel.required());
                        jsonParam.put("paramDatatype", datatype);
                        jsonParam.put("paramDefault", "");
                        jsonParam.put("paramExample", "");
                        jsonParams.add(jsonParam);
                    }
                }
            } else {
                List<String> beanList = StringUtil.split(paramType.getName(), ".");
                String datatype = beanList.get(beanList.size() - 1);
                JSONObject jsonPara = new JSONObject();
                jsonPara.put("paramName", name);
                jsonPara.put("paramDesc", desc);
                jsonPara.put("paramRange", range1);
                jsonPara.put("paramRequired", isnullable);
                jsonPara.put("paramDatatype", datatype);
                jsonPara.put("paramDefault", vnull);
                jsonPara.put("paramExample", example1);
                jsonParams.add(jsonPara);
            }
        }
        return jsonParams;
    }


    private static String getHost() {
        return (StringUtil.isBlank(HttpServerConf.confBean.getHost()) ? "localhost" : HttpServerConf.confBean.getHost());
    }

    private static String getPort() {
        return String.valueOf(HttpServerConf.confBean.getHttpPort());
    }

    private static String getHostPort() {
        String ActionPattern = HttpServerConf.confBean.getActionPattern();
        if (ActionPattern.endsWith("/*")) ActionPattern = ActionPattern.substring(0, ActionPattern.length() - 2);
        return "http://" + getHost() + ":" + getPort();
    }

    private static String getUrlCtx() {
        return getHostPort() + HttpServerConf.confBean.getWebContext();
    }

    private static String getUrlActionPattern() {
        String ActionPattern = HttpServerConf.confBean.getActionPattern();
        if (ActionPattern.endsWith("/*")) ActionPattern = ActionPattern.substring(0, ActionPattern.length() - 2);
        return getUrlCtx() + ActionPattern;
    }

    public static class ParamsBean {
        private Class<?> paramType;// 方法的参数类型
        private String reqParamName; // 该参数在 request 中的名字。默认就是参数名
        private String desc;//用于描述类、方法、参数、返回值的含义
        private boolean nullable;//该参数在 request 中是否可以不存在或为空。
        private String[] valueIfNull;//对于可空变量，赋予的默认值（因为前端提交的数据有可能是字符数组）
        private boolean bean;//该参数是不是实体bean
        private String range;//用于描述参数和返回值的取值范围，"如：任意，4位数字，1-100之间的数字"。
        private String example1;//例子数据，如10.78.90.22这样有特殊意义的数据

        public Class<?> getParamType() {
            return paramType;
        }

        public void setParamType(Class<?> paramType) {
            this.paramType = paramType;
        }

        public String getReqParamName() {
            return reqParamName;
        }

        public void setReqParamName(String reqParamName) {
            this.reqParamName = reqParamName;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public boolean isNullable() {
            return nullable;
        }

        public void setNullable(boolean nullable) {
            this.nullable = nullable;
        }

        public String[] getValueIfNull() {
            return valueIfNull;
        }

        public void setValueIfNull(String[] valueIfNull) {
            this.valueIfNull = valueIfNull;
        }

        public boolean isBean() {
            return bean;
        }

        public void setBean(boolean bean) {
            this.bean = bean;
        }

        public String getRange() {
            return range;
        }

        public void setRange(String range) {
            this.range = range;
        }

        public String getExample1() {
            return example1;
        }

        public void setExample1(String example1) {
            this.example1 = example1;
        }
    }

    public static void SaveWriteFile(String fileName, String content) {

        //FileWriter fw = null;
        //PrintWriter out = null;
        Writer writer = null;
        try {
            File file1 = new File(fileName);
            writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file1), "UTF-8"));
            writer.write(content);
            writer.flush();
           /* fw = new FileWriter(fileName, true);
            out = new PrintWriter(fw);
            out.println(content);*/
        } catch (IOException ioe) {
            logger.error("shibai");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                    //out.close();
                } catch (Exception ioe) {
                }
            }
        }
    }
}
