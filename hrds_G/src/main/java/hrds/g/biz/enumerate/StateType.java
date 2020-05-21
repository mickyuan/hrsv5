package hrds.g.biz.enumerate;


import fd.ng.core.annotation.DocClass;
import hrds.commons.exception.AppSystemException;

import java.util.HashMap;
import java.util.Map;

@DocClass(desc = "接口响应状态码定义", author = "dhw", createdate = "2020/3/31 11:31")
public enum StateType {

	/**
	 * 正常
	 */
	NORMAL("200", "正常"),

	/**
	 * 账号或密钥错误
	 */
	NOT_REST_USER("400", "该用户非接口用户"),
	/**
	 * 账号或密钥错误
	 */
	UNAUTHORIZED("401", "账号或密钥错误"),
	/**
	 * 接口状态禁用错误
	 */
	INTERFACE_STATE("403", "接口状态错误,为禁用状态"),
	/**
	 * 使用有效期错误
	 */
	EFFECTIVE_DATE_ERROR("404", "使用有效期错误"),
	/**
	 * 表不存在或者为空
	 */
	TABLE_NOT_EXISTENT("405", "表不存在或者为空"),
	/**
	 * 列不存在
	 */
	COLUMN_DOES_NOT_EXIST("406", "列不存在"),
	/**
	 * 没有使用权限
	 */
	NO_USR_PERMISSIONS("407", "没有表使用权限或者该表不存在"),
	/**
	 * 没有接口使用权限
	 */
	NO_PERMISSIONS("408", "没有接口使用权限"),
	/**
	 * 接口开始使用日期未到
	 */
	START_DATE_ERROR("409", "接口开始使用日期未到"),
	/**
	 * 存储目录地址错误
	 */
	DIR_ERROR("410", "存储目录地址错误"),
	/**
	 * 文件名称未填写错误
	 */
	FILENAME_ERROR("411", "文件名称未填写错误"),
	/**
	 * 条件填写错误
	 */
	CONDITION_ERROR("412", "条件填写错误"),
	/**
	 * 参数错误
	 */
	ARGUMENT_ERROR("413", "参数错误"),
	/**
	 * 非自定义表
	 */
	UNCUSTOMIZE("414", "非自定义表"),
	/**
	 * SQL不正确
	 */
	SQL_IS_INCORRECT("415", "SQL为空或不正确"),
	/**
	 * SQL更新类型不正确
	 */
	SQL_UPDATETYPE_ERROR("416", "SQL更新类型不正确"),
	/**
	 * SQL删除类型不正确
	 */
	SQL_DELETETYPE_ERROR("417", "SQL删除类型不正确"),
	/**
	 * 词条填写错误
	 */
	KEY_EXPIRED("418", "词条填写错误"),
	/**
	 * ToKen填写错误
	 */
	TOKEN_ERROR("419", "ToKen填写错误"),
	/**
	 * 报表编码错误
	 */
	REPORT_CODE_ERROR("420", "报表编码错误"),
	/**
	 * JSON数据格式不正确
	 */
	JSON_DATA_ERROR("421", "JSON数据格式不正确"),
	/**
	 * 字段填写错误
	 */
	COLUMN_SPECIFICATIONS_ERROR("422", "字段填写错误"),
	/**
	 * 字段类型未填写
	 */
	COLUMN_TYPE_ERROR("423", "字段类型未填写"),
	/**
	 * 来源表名未填写
	 */
	SOURCE_TABLE_ERROR("424", "来源表名未填写"),
	/**
	 * 表已经存在
	 */
	TABLE_EXISTENT("425", "表已经存在"),
	/**
	 * 上传图片错误
	 */
	UPLOAD_PICTURE_ERROR("426", "上传图片错误"),
	/**
	 * 回调URL错误
	 */
	CALBACK_URL_ERROR("427", "响应回调url错误，请检查url是否正确"),
	/**
	 * 数据类型错误
	 */
	DATA_TYPE_ERROR("428", "数据类型dataType错误,未填写或填写错误"),
	/**
	 * 输出类型错误
	 */
	OUT_TYPE_ERROR("429", "输出类型outType错误,未填写或填写错误"),
	/**
	 * 索引未建立错误
	 */
	INDEX_ERROR("430", "索引未建立错误"),
	/**
	 * PK信息错误
	 */
	PK_ERROR("431", "PK信息错误"),
	/**
	 * 信号文件错误
	 */
	SIGNAL_FILE_ERROR("432", "生成信号文件失败，请检查filepath与filename是否正确"),
	/**
	 * 异步类型错误
	 */
	ASYNTYPE_ERROR("433", "异步类型参数asynType错误"),
	/**
	 * 文件路径错误
	 */
	FILEPARH_ERROR("434", "文件路径错误"),
	/**
	 * 操作类型错误
	 */
	OPERATION_TYPE_ERROR("435", "操作类型错误"),
	/**
	 * 获取token值失败
	 */
	TOKEN_EXCEPTION("436", "获取token值失败"),
	/**
	 * 获取IP与port失败
	 */
	IPANDPORT_EXCEPTION("437", "获取IP与port失败"),
	/**
	 * 检查接口失败
	 */
	INTERFACECHECK_EXCEPTION("438", "接口检查失败"),
	/**
	 * json转换对象失败
	 */
	JSONCONVERSION_EXCEPTION("439", "json转换对象失败"),
	/**
	 * 要删除的表不存在
	 */
	TABLE_NON_EXISTENT("440", "要删除的表不存在"),
	/**
	 * uuid不能为空或uuid错误
	 */
	UUID_NOT_NULL("441", "uuid不能为空或uuid错误"),
	/**
	 * 当前表对应的存储层信息不存在
	 */
	STORAGE_LAYER_INFO_NOT_EXIST_WITH_TABLE("442", "当前表对应的存储层信息不存在"),
	/**
	 * 当前表对应的存储层信息不存在
	 */
	ORACLE9I_NOT_SUPPORT("443", "当前表对应的存储层信息不存在"),
	/**
	 * 创建文件失败
	 */
	CREATE_FILE_ERROR("444", "创建文件失败"),
	/**
	 * 创建文件目录失败
	 */
	CREATE_DIRECTOR_ERROR("445", "创建文件目录失败"),
	/**
	 * url未填或填写错误
	 */
	URL_NOT_EXIST("446", "url未填或填写错误"),
	/**
	 * 系统错误
	 */
	EXCEPTION("500", "系统错误");

	private final String code;

	private final String value;

	StateType(String code, String value) {
		this.code = code;
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

	/**
	 * 根据指定的代码值转换成对象
	 *
	 * @param code 本代码的代码值
	 * @return
	 */
	public static String ofValueByCode(String code) {
		for (StateType stateType : StateType.values()) {
			if (stateType.getCode().equals(code)) {
				return stateType.value;
			}
		}
		return null;
	}

	/**
	 * 根据指定的代码值转换成对象
	 *
	 * @param code 本代码的代码值
	 * @return
	 */
	public static StateType ofEnumByCode(String code) {
		for (StateType stateType : StateType.values()) {
			if (stateType.getCode().equals(code)) {
				return stateType;
			}
		}
		return null;
	}

	/**
	 * 根据接口状态对象获取接口响应状态信息
	 *
	 * @param stateType 接口响应状态对象
	 * @return 返回获取接口响应状态信息
	 */
	public static Map<String, Object> getResponseInfo(StateType stateType) {
		Map<String, Object> stateTypeMap = new HashMap<>();
		stateTypeMap.put("status", stateType.getCode());
		stateTypeMap.put("message", stateType.getValue());
		return stateTypeMap;
	}

	/**
	 * 自定义获取接口响应状态信息
	 *
	 * @param status  接口响应状态码
	 * @param message 接口响应状态信息
	 * @return 返回获取接口响应状态信息
	 */
	public static Map<String, Object> getResponseInfo(String status, Object message) {
		Map<String, Object> stateTypeMap = new HashMap<>();
		stateTypeMap.put("status", status);
		stateTypeMap.put("message", message);
		return stateTypeMap;
	}

}
