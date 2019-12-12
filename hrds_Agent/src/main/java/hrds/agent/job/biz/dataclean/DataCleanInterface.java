package hrds.agent.job.biz.dataclean;

import hrds.commons.entity.Column_split;
import org.apache.parquet.example.data.Group;

import java.util.List;
import java.util.Map;

/**
 * <p>标    题: 海云数服务</p>
 * <p>描    述: 数据清洗接口</p>
 * <p>版    权: Copyright (c) 2016  </p>
 * <p>公    司: 上海泓智信息科技有限公司</p>
 * <p>创建时间: 2016年9月5日 上午10:44:41</p>
 * <p>@author mashimaro</p>
 * <p>@version 1.0</p>
 * <p>DataCleanInterface.java</p>
 * <p></p>
 */
public interface DataCleanInterface {

	/**
	 * 字符替换
	 *
	 * @param deleSpecialSpace {@link Map<String, Map<String, String>>}
	 *                         key(列清洗、表清洗、任务清洗) value(清洗规则，key(原字符串、要替换的字符串))
	 * @param columnData       {@link String} 列内容
	 * @param columnname       {@link String} 列名称
	 * @return {@link String} 清洗后的内容
	 */
	public String replace(Map<String, Map<String, String>> deleSpecialSpace, String columnData, String columnname);

	/**
	 * 字符补齐
	 *
	 * @param strFilling {@link Map<String, String>}
	 *                   key(列清洗、表清洗、任务清洗) value(1!@!4!@!8)补齐长度!@!补齐方式!@!要补齐的字符串
	 * @param columnData {@link String} 列内容
	 * @param columnname {@link String} 列名称
	 * @return {@link String} 清洗后的内容
	 */
	public String filling(Map<String, String> strFilling, String columnData, String columnname);

	/**
	 * 日期转换
	 *
	 * @param columnData
	 * @param columnname
	 * @return
	 */
	public String dateing(Map<String, String> dateing, String columnData, String columnname);

	/**
	 * 列拆分
	 *
	 * @param spliting
	 * @param columnData
	 * @param columnname
	 * @param group
	 * @param type
	 * @param fileType
	 * @return
	 */
	public String split(Map<String, Map<String, Column_split>> spliting, String columnData, String columnname, Group group, String type, String fileType, List<Object> list);

	/**
	 * 列合并
	 *
	 * @param mergeing
	 * @param arrColString
	 * @param columns
	 * @param lineData
	 * @param filetype
	 * @return
	 */
	public String merge(Map<String, String> mergeing, String[] arrColString, String[] columns, Group group, List<Object> lineData, String filetype);

	/**
	 * 码值转换
	 *
	 * @param coding
	 * @param columnData
	 * @param columnname
	 * @return
	 */
	public String codeTrans(Map<String, String> coding, String columnData, String columnname);

	/**
	 * 码值转换
	 *
	 * @param columnData
	 * @param columnname
	 * @return
	 */
	public String trim(Map<String, String> Triming, String columnData, String columnname);
}
