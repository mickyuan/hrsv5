package hrds.commons.apiannotation;

import fd.ng.core.conf.AppinfoConf;
import fd.ng.core.docannotation.*;
import fd.ng.core.utils.ClassUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.web.annotation.Action;
import fd.ng.web.annotation.UrlName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: hrsv5
 * @description: 解析api注解
 * @author: xchao
 * @create: 2019-09-12 11:14
 */
public class ParsingAnnotaion {
	private static final Logger logger = LogManager.getLogger(ParsingAnnotaion.class.getName());

	public static void main(String[] args) {
		try {
			List<Class<?>> actionClassList = ClassUtil.getClassListByAnnotation(AppinfoConf.AppBasePackage, Action.class);
			for (Class<?> actionClass : actionClassList) {
				final String className = actionClass.getName();
				Action actionAnnotation = actionClass.getAnnotation(Action.class); // 得到该类的注解
				DocClass annotation = actionClass.getAnnotation(DocClass.class);// 得到该类的注解
				String apiName = annotation.describe();
				System.out.println("模块名称:" + apiName);
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
				// 得到当前 Action 类的所有方法
				Method[] actionMethods = actionClass.getDeclaredMethods();
				for (Method actionMethod : actionMethods) {
					int methodModifier = actionMethod.getModifiers();
					if (Modifier.isAbstract(methodModifier) || Modifier.isStatic(methodModifier))
						continue; // 抽象和静态方法不处理
					if (Modifier.isPublic(methodModifier)) { // 只检查 public 的方法
						String curMethodName = actionMethod.getName();
						UrlName urlName = actionMethod.getAnnotation(UrlName.class);
						if (urlName != null) curMethodName = urlName.value();

						DocMethod api = actionMethod.getAnnotation(DocMethod.class);
						String description = api.description();//接口描述
						String s = api.httpMethod();//请求方式
						String version = api.version();//版本号

						List<Param> apiParamsList = new ArrayList<>();
						//获取方法的参数
						Parameter[] parameters = actionMethod.getParameters();
						Params apiParams = actionMethod.getAnnotation(Params.class);
						if (apiParams != null) {
							Param[] value1 = apiParams.value();
							for (Param apiParam : value1) {
								apiParamsList.add(apiParam);
							}
						}
						Param apiParam = actionMethod.getAnnotation(Param.class);
						if (apiParam != null) {
							apiParamsList.add(apiParam);
						}
						System.out.println("url:" + actionLookupKey + "/" + curMethodName + ",接口描述:" + description + ",请求方式:" + s + ",版本:" + version);
						params(apiParamsList);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


	public static void params(List<Param> apiParamsList) {
		for (Param apiParam : apiParamsList) {
			String param = apiParam.name();
			String intro = apiParam.intro();
			String range = apiParam.range();
			boolean required = apiParam.required();
			boolean requestBean = apiParam.isRequestBean();
			Class aClass = apiParam.dataType();
			List<String> split = StringUtil.split(aClass.getName(), ".");
			String name = split.get(split.size() - 1);
			if (requestBean) {
				Field[] declaredFields = aClass.getDeclaredFields();
				for (Field field : declaredFields) {
					DocBean apiModel = field.getAnnotation(DocBean.class);
					List<String> beanList = StringUtil.split(apiModel.dataType().getName(), ".");
					String datatype = beanList.get(beanList.size() - 1);
					System.out.println("参数名" + name + ":" + apiModel.value() + ",参数描述:" + apiModel.name() + ",是否为空:" + apiModel.required() + ",类型:" + datatype);
				}
			} else {
				System.out.println("参数名:" + param + ",参数描述:" + intro + ",参数范围:" + range + ",是否为空:" + required + "类型:" + name);
			}
		}
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
}
