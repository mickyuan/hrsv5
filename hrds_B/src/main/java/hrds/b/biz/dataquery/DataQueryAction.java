package hrds.b.biz.dataquery;

import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.RequestParam;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.ResponseUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * <p>类名: DataQueryAction</p>
 * <p>类说明: Web服务查询数据界面后台处理类</p>
 *
 * @author BY-HLL
 * @date 2019/9/3 0003 下午 03:26
 * @since JDK1.8
 */
public class DataQueryAction extends BaseAction {
	/**
	 * <p>方法名: getFileDataSource</p>
	 * <p>方法说明: 获取部门的包含文件采集任务的数据源信息</p>
	 * 1.根据部门id获取该部门下所有包含文件采集任务的数据源信息的list
	 *
	 * @param depId Long
	 *              含义：部门id
	 *              取值范围: long类型值
	 * @return List<Map < String, Object>>
	 * 含义: 数据源查询结果的list集合
	 * 取值范围: 不为NULL
	 */
	public List<Map<String, Object>> getFileDataSource(long depId) {
		//数据可访问权限处理方式: 根据 Agent_info 的 user_id 进行权限检查
		//1.根据部门id获取该部门下所有包含文件采集任务的数据源信息的list
		return Dbo.queryList(
				" select ds.source_id,ds.datasource_name" +
						" from " + Source_relation_dep.TableName + " srd" +
						" join " + Data_source.TableName + " ds on srd.source_id = ds.source_id" +
						" join " + Agent_info.TableName + " ai on ds.source_id = ai.source_id" +
						" where srd.dep_id = ? AND ai.agent_type = ? AND ai.user_id = ?" +
						" GROUP BY ds.source_id,ds.datasource_name",
				depId, AgentType.WenJianXiTong.getCode(), getUserId()
		);
	}

	/**
	 * <p>方法名: getFileCollectionTask</p>
	 * <p>方法说明: 根据数据源id获取数据源下所有文件采集任务</p>
	 * 1.根据数据源id获取该数据源下所有文件采集任务的list
	 *
	 * @param sourceId 含义: 数据源id
	 *                 取值范围: long类型值
	 * @return List<Map < String, Object>>
	 * 含义: 返回文件采集任务的map
	 * 取值范围: 不为NULL
	 */
	public List<Map<String, Object>> getFileCollectionTask(long sourceId) {
		//数据可访问权限处理方式: 根据 Agent_info 的 user_id 进行权限检查
		//1.根据数据源id获取该数据源下所有文件采集任务的list
		return Dbo.queryList(
				" select * from " + File_collect_set.TableName + " fc" +
						" join " + Agent_info.TableName + " ai on fc.agent_id = ai.agent_id" +
						" where ai.source_id = ? AND ai.agent_type = ? and ai.user_id = ?",
				sourceId, AgentType.WenJianXiTong.getCode(), getUserId()
		);
	}

	/**
	 * <p>方法名: downloadFileCheck</p>
	 * <p>方法说明: 检查文件的下载权限</p>
	 * 1.根据登录用户id和文件id获取文件检查后的结果集
	 * 2.检查申请的操作是否是下载
	 * 2-1.类型是下载，检查是否具有下载权限
	 *
	 * @param fileId 含义: 文件id
	 *               取值范围: String类型值的UUID（32位）
	 * @return boolean
	 * 含义: 文件是否具有下载权限
	 * 取值范围: true 或者 false
	 */
	private boolean downloadFileCheck(String fileId) {
		//数据可访问权限处理方式: 根据 Data_auth 的 user_id 进行权限检查
		//1.根据登录用户id和文件id获取文件检查后的结果集
		Result authResult = Dbo.queryResult(
				"select * from " + Data_auth.TableName + " da" +
						" join " + Source_file_attribute.TableName + " sfa" +
						" ON sfa.file_id = da.file_id WHERE da.user_id = ?" +
						" AND sfa.file_id = ? AND da.apply_type = ?",
				getUserId(), fileId, ApplyType.XiaZai.getCode()
		);
		if (authResult.isEmpty()) {
			throw new BusinessException("下载文件的文件不存在! fileId=" + fileId);
		}
		//2.检查申请的操作是否是下载
		ApplyType applyType = ApplyType.ofEnumByCode(authResult.getString(0, "apply_type"));
		if (ApplyType.XiaZai == applyType) {
			AuthType authType = AuthType.ofEnumByCode(authResult.getString(0, "auth_type"));
			//2-1.类型是下载，检查是否具有下载权限
			if (AuthType.YunXu == authType || AuthType.YiCi == authType) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>方法名: downloadFile</p>
	 * <p>方法说明: 根据文件id下载文件</p>
	 * 1.根据文件id检查文件是否有下载权限
	 * 2.通过文件id获取文件的 byte
	 * 3.写入输出流，返回结果
	 * 4.下载文件完成后修改文件下载计数信息
	 *
	 * @param fileId       含义:文件id
	 *                     取值范围: String类型值的 UUID（32位）
	 * @param fileName     含义: 文件名
	 *                     取值范围: String类型值
	 * @param queryKeyword 含义:文件查询关键字
	 *                     取值范围: 没有输入限制
	 */
	public void downloadFile(String fileId, String fileName, String queryKeyword) {
		//数据可访问权限处理方式: 无数据库操作不需要权限检查
		//1.根据文件id检查文件是否有下载权限
		if (!downloadFileCheck(fileId)) {
			throw new BusinessException("文件没有下载权限! fileName=" + fileName);
		}
		try (OutputStream out = ResponseUtil.getResponse().getOutputStream()) {
			//2.通过文件id获取文件的 byte
			byte[] bye = getFileBytesFromAvro(fileId);
			if (bye == null) {
				throw new BusinessException("文件已不存在! fileName=" + fileName);
			}
			//3.写入输出流，返回结果
			out.write(bye);
			out.flush();
			//4.下载文件完成后修改文件下载计数信息
			modifySortCount(fileId, queryKeyword);
		} catch (IOException e) {
			throw new AppSystemException("文件下载失败! fileName=" + fileName);
		}
	}

	/**
	 * <p>方法名: getFileBytesFromAvro</p>
	 * <p>方法说明: 占位方法，方法写完后删除</p>
	 */
	private byte[] getFileBytesFromAvro(String fileId) {
		byte[] bye = fileId.getBytes();
		return bye;
	}

	/**
	 * <p>方法名: modifySortCount</p>
	 * <p>方法说明: 修改文件计数</p>
	 * 1.通过文件id获取文件计数信息
	 * 2.获取不到文件计数信息则添加一条计数信息,获取到则修改文件计数信息
	 *
	 * @param fileId    含义:文件id
	 *                  取值范围: String类型值的UUID（32位）
	 * @param word_name 含义:查询关键字
	 *                  取值范围: String类型值，没有输入限制
	 */
	private void modifySortCount(String fileId, String word_name) {
		//数据可访问权限处理方式: 修改文件计数信息，不需要检查
		//1.通过文件id获取文件计数信息
		Result siResult = Dbo.queryResult(
				"select * from " + Search_info.TableName + " where file_id = ? and word_name = ?",
				fileId, word_name
		);
		Search_info searchInfo = new Search_info();
		searchInfo.setWord_name(word_name);
		//2.获取不到文件计数信息则添加一条计数信息,获取到则修改文件计数信息
		if (siResult.isEmpty()) {
			String nextId = PrimayKeyGener.getNextId();
			searchInfo.setFile_id(fileId);
			searchInfo.setSi_id(nextId);
			if (searchInfo.add(Dbo.db()) != 1) {
				throw new BusinessException("添加文件计数信息失败！data=" + searchInfo);
			}
		} else {
			searchInfo.setFile_id(fileId);
			int execute = Dbo.execute("update search_info set si_count = si_count+1 where" +
							" file_id = ? and word_name = ?",
					fileId, word_name);
			if (execute != 1) {
				throw new BusinessException("修改文件计数信息失败！data" + searchInfo);
			}
		}
	}

	/**
	 * <p>方法名: getCollectFile</p>
	 * <p>方法说明: 根据登录用户获取用户收藏的文件列表,返回结果显示最近9条收藏</p>
	 * 1.如果查询条数小于1条则显示默认9条，查询条数大于99条则显示99条，否则取传入的查询条数
	 * 2.返回当前登录的用户已经收藏的文件列表的List结果集
	 *
	 * @param queryNum 含义:查询条数
	 *                 取值范围: int类型值 1-99 默认为9
	 * @return List<Map < String, Object>>
	 * 含义: 用户收藏文件的结果集List
	 * 取值含义：登录用户收藏文件列表的结果集List
	 */
	public List<Map<String, Object>> getCollectFile(@RequestParam(valueIfNull = "9") int queryNum) {

		//1.如果查询条数小于1条则显示默认9条，查询条数大于99条则显示99条，否则取传入的查询条数
		queryNum = queryNum < 1 ? 9 : queryNum;
		queryNum = queryNum > 99 ? 99 : queryNum;
		//2.获取当前用户已经收藏的文件列表
		//数据可访问权限处理方式: 根据 User_fav 的 user_id 做权限检查
		return Dbo.queryList("SELECT * FROM " + User_fav.TableName +
						" WHERE user_id = ? AND fav_flag = ?" +
						" ORDER BY fav_id DESC LIMIT ?",
				getUserId(), IsFlag.Shi.getCode(), queryNum
		);
	}

	/**
	 * <p>方法名: saveFavoriteFile</p>
	 * <p>方法说明: 保存文件收藏方法</p>
	 * 1.根据文件id获取文件名
	 * 2.根据文件id和文件名收藏该文件
	 *
	 * @param fileId String
	 *               含义: 文件id
	 *               取值范围: String类型值的 UUID（32位）主键唯一
	 */
	public void saveFavoriteFile(String fileId) {
		//1.根据文件id获取文件名
		List<Object> originalNameList = Dbo.queryOneColumnList(
				"select original_name from " + Source_file_attribute.TableName + " where" +
						" file_id = ?", fileId);
		//2.根据文件id和文件名收藏该文件
		if (originalNameList.size() == 0) {
			throw new BusinessException("文件不存在！fileId=" + fileId);
		}
		User_fav userFav = new User_fav();
		userFav.setFav_id(PrimayKeyGener.getNextId());
		userFav.setOriginal_name(originalNameList.get(0).toString());
		userFav.setFile_id(fileId);
		userFav.setUser_id(getUserId());
		userFav.setFav_flag(IsFlag.Shi.getCode());
		if (userFav.add(Dbo.db()) != 1) {
			throw new BusinessException("收藏失败！fileId=" + userFav.getFile_id());
		}
	}

	/**
	 * <p>方法名: cancelFavoriteFile</p>
	 * <p>方法说明: 删除已收藏文件方法</p>
	 * 1.根据收藏文件id删除收藏记录
	 *
	 * @param favId long
	 *              含义: 收藏文件的id
	 *              取值范围: long类型值
	 */
	public void cancelFavoriteFile(@RequestParam(valueIfNull = "0") Long favId) {
		//1.根据收藏文件id删除收藏记录
		User_fav userFav = new User_fav();
		userFav.setFav_id(favId);
		int deleteUserFavNum = Dbo.execute(
				"delete from " + User_fav.TableName + " where fav_id=?", favId);
		if (deleteUserFavNum != 1) {
			if (deleteUserFavNum == 0) {
				throw new BusinessException("表中不存在该条记录！favId=" + favId);
			}
			throw new BusinessException("取消收藏失败！favId=" + favId);
		}
	}

	/**
	 * <p>方法名: getFileClassifySum</p>
	 * <p>方法说明: 文件采集分类统计</p>
	 * 1.根据登录用户的id获取用户文件采集统计的结果
	 * 2.根据统计类型设置返回的map结果集
	 *
	 * @return classificationSumMap
	 * 含义 返回的结果集map
	 * 取值范围: 不为NULL
	 * @author BY-HLL
	 */
	public Map<String, Object> getFileClassifySum() {
		//1.根据登录用户的id获取用户文件采集统计的结果
		List<Map<String, Object>> fcsList = Dbo.queryList("select count(1) sum_num,file_type" +
						" from source_file_attribute sfa join agent_info ai" +
						" on sfa.agent_id = ai.agent_id" +
						" where sfa.collect_type = ? AND ai.user_id = ?" +
						" GROUP BY file_type ORDER BY file_type",
				CollectType.WenJianCaiJi.getCode(), getUserId()
		);
		//2.根据统计类型设置返回的map结果集
		Map<String, Object> classificationSumMap = new HashMap<>();
		if (!fcsList.isEmpty()) {
			for (Map<String, Object> fcsMap : fcsList) {
				classificationSumMap.put(FileType.ofValueByCode((String) fcsMap.get("file_type")),
						fcsMap.get("sum_num"));
			}
		}
		return classificationSumMap;
	}

	/**
	 * <p>方法名: getSevenDayCollectFileSum</p>
	 * <p>方法说明: 7天内文件采集统计</p>
	 * 1.如果查询天数小于1条则显示默认7天，查询条数大于30天则显示30天，否则取传入的查询天数
	 * 2.根据登录用户的id获取用户最近7天的文件采集信息
	 * 3.根据查询结果设置返回的map结果集
	 *
	 * @param queryDays 含义:查询天数
	 *                  取值范围:int类型值 1-30 默认为7
	 * @return sevenDayCollectFileSumMap
	 * 含义 返回的结果集map
	 * 取值范围: 不为NULL
	 * @author BY-HLL
	 */
	public Map<String, Object> getSevenDayCollectFileSum(@RequestParam(valueIfNull = "7") int queryDays) {
		//1.如果查询天数数小于1条则显示默认7天，查询条数大于30天则显示30天，否则取传入的查询天数
		queryDays = queryDays < 1 ? 7 : queryDays;
		queryDays = queryDays > 30 ? 30 : queryDays;
		//2.根据登录用户的id获取用户最近7天的文件采集信息
		List<Map<String, Object>> scfList = Dbo.queryList("select count(1) count,storage_date" +
						" from source_file_attribute sfa join agent_info ai " +
						" on sfa.agent_id = ai.agent_id" +
						" where sfa.collect_type = ? AND ai.user_id = ? GROUP BY storage_date" +
						" ORDER BY storage_date desc LIMIT ?",
				CollectType.WenJianCaiJi.getCode(), getUserId(), queryDays
		);
		//3.根据查询结果设置返回的map结果集
		Map<String, Object> sevenDayCollectFileSumMap = new HashMap<>();
		if (!scfList.isEmpty()) {
			String collectDate = null;
			Integer collectSum = 0;
			for (Map<String, Object> scfMap : scfList) {
				collectDate = (String) scfMap.get("storage_date");
				collectSum = Integer.valueOf(scfMap.get("count").toString());
				if (sevenDayCollectFileSumMap.containsKey("collectDate")) {
					sevenDayCollectFileSumMap.put(collectDate, collectSum += collectSum);
				} else {
					sevenDayCollectFileSumMap.put(collectDate, collectSum);
				}
			}
		}
		return sevenDayCollectFileSumMap;
	}

	/**
	 * <p>方法名: getLast3FileCollections</p>
	 * <p>方法说明: 获取最近的3次文件采集信息</p>
	 * 1.如果查询最近次数小于1则显示默认最近3次，查询最近次数大于30天则显示最近30次，否则取传入的查询次数
	 * 2.根据登录用户的id获取用户最近3次的文件采集信息
	 * 3.根据查询结果设置返回的map结果集
	 *
	 * @param timesRecently 含义:查询最近采集的次数
	 *                      取值范围:int类型值 1-30 默认为3
	 * @return sevenDayCollectFileSumMap
	 * 含义 返回的结果集map
	 * 取值范围: 不为NULL
	 * @timesRecently
	 * @author BY-HLL
	 */
	public List<Map<String, Object>> getLast3FileCollections(@RequestParam(valueIfNull = "3") int timesRecently) {
		//1.如果查询最近次数小于1则显示默认最近3次，查询最近次数大于30天则显示最近30次，否则取传入的查询次数
		timesRecently = timesRecently < 1 ? 3 : timesRecently;
		timesRecently = timesRecently > 30 ? 30 : timesRecently;
		//2.根据登录用户的id获取用户最近3次的文件采集信息
		List<Map<String, Object>> l3fcList = Dbo.queryList("select storage_date, storage_time," +
						" max(concat(storage_date,storage_time)) max_date, count(1) count," +
						" fcs.fcs_name from source_file_attribute sfa join file_collect_set fcs on" +
						" sfa.collect_set_id = fcs.fcs_id join agent_info ai" +
						" on ai.agent_id = fcs.agent_id" +
						" where collect_type = ? and ai.user_id = ?" +
						" GROUP BY storage_date,storage_time,fcs.fcs_name" +
						" ORDER BY max_date desc limit ?",
				CollectType.WenJianCaiJi.getCode(), 5000L, timesRecently
		);
		//3.根据查询结果设置返回的map结果集
		List<Map<String, Object>> last3FileCollectionsMapList = new ArrayList<>();
		for (Map<String, Object> l3fcMap : l3fcList) {
			Map<String, Object> last3FileCollectionsMap = new HashMap<>();
			//采集日期
			String collectDate = (String) l3fcMap.get("storage_date");
			//采集时间
			String collectTime = (String) l3fcMap.get("storage_time");
			//采集总数
			Integer collectSum =  Integer.valueOf(l3fcMap.get("count").toString());
			//采集任务名称
			String collectName = (String) l3fcMap.get("fcs_name");
			//拼接待返回数据的map
			last3FileCollectionsMap.put("collectDate", collectDate);
			last3FileCollectionsMap.put("collectTime", collectTime);
			last3FileCollectionsMap.put("collectSum", collectSum);
			last3FileCollectionsMap.put("collectName", collectName);
			last3FileCollectionsMapList.add(last3FileCollectionsMap);
		}
		return last3FileCollectionsMapList;
	}
}
