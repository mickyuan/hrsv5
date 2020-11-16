package hrds.commons.hadoop.loginAuth;

import fd.ng.core.utils.Validator;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.hadoop.readconfig.ConfigReader;

import java.lang.reflect.Constructor;

/**
 * 登录认证工厂类
 */
public class LoginAuthFactory {

	/**
	 * 认证默认值取 normal
	 *
	 * @return 认证实例
	 */
	public static ILoginAuth getInstance() {
		return getInstance(ConfigReader.PlatformType.normal.toString());
	}

	/**
	 * 指定平台创建认证实例
	 *
	 * @param platform 平台类型 {normal: 默认,cdh5: CDH5, fic80: FIC80}
	 * @return 认证实例
	 */
	public static ILoginAuth getInstance(String platform) {
		//参数校验
		Validator.notBlank(platform, "平台类型不能为空!");
		//初始化实现类路径
		String loginAuthClassPath;
		//根据平台类型设置实现类路径
		ConfigReader.PlatformType platformType = ConfigReader.PlatformType.valueOf(platform);
		if (platformType == ConfigReader.PlatformType.normal) {
			loginAuthClassPath = "hrds.commons.hadoop.loginAuth.impl.NormalLoginAuthImpl";
		} else if (platformType == ConfigReader.PlatformType.cdh5) {
			loginAuthClassPath = "hrds.commons.hadoop.loginAuth.impl.CDH5LoginAuthImpl";
		} else if (platformType == ConfigReader.PlatformType.fic80) {
			loginAuthClassPath = "hrds.commons.hadoop.loginAuth.impl.C80LoginAuthImpl";
		} else {
			throw new BusinessException("platform : " + platform + ", 不支持! 目前支持: {normal: 默认,cdh5: CDH5, fic80: FIC80}");
		}
		//构造具体的实现
		ILoginAuth iLoginAuth;
		try {
			iLoginAuth = (ILoginAuth) Class.forName(loginAuthClassPath).newInstance();
		} catch (Exception e) {
			throw new AppSystemException("初始化登录认证实例的实现类失败! ", e);
		}
		return iLoginAuth;
	}

	/**
	 * 指定平台和指定平台登录实现类
	 *
	 * @param platform   平台类型 {normal: 默认,cdh5: CDH5, fic80: FIC80}
	 * @param configPath 平台认证配置文件
	 * @return 认证实例
	 */
	public static ILoginAuth getInstance(String platform, String configPath) {
		//参数校验
		Validator.notBlank(platform, "平台类型不能为空!");
		//初始化实现类路径
		String loginAuthClassPath;
		//根据平台类型设置实现类路径
		ConfigReader.PlatformType platformType = ConfigReader.PlatformType.valueOf(platform);
		if (platformType == ConfigReader.PlatformType.normal) {
			loginAuthClassPath = "hrds.commons.hadoop.loginAuth.impl.NormalLoginAuthImpl";
		} else if (platformType == ConfigReader.PlatformType.cdh5) {
			loginAuthClassPath = "hrds.commons.hadoop.loginAuth.impl.CDH5LoginAuthImpl";
		} else if (platformType == ConfigReader.PlatformType.fic80) {
			loginAuthClassPath = "hrds.commons.hadoop.loginAuth.impl.C80LoginAuthImpl";
		} else {
			throw new BusinessException("platform : " + platform + ", 不支持! 目前支持: {normal: 默认,cdh5: CDH5, fic80: FIC80}");
		}
		//构造具体的实现
		ILoginAuth iLoginAuth;
		try {
			Class<?> cl = Class.forName(loginAuthClassPath);
			Constructor<?> cc = cl.getConstructor(String.class);
			iLoginAuth = (ILoginAuth) cc.newInstance(configPath);
		} catch (Exception e) {
			throw new AppSystemException("初始化登录认证实例的实现类失败! ", e);
		}
		return iLoginAuth;
	}
}
