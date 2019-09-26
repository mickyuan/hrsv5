package hrds.b.biz.datasource;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.utils.CodecUtil;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.annotation.RequestParam;
import fd.ng.web.annotation.UploadFile;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.FileUploadUtil;
import fd.ng.web.util.ResponseUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;

/**
 * 数据源增删改，导入、下载类
 *
 * @author dhw
 * @date 2019-09-03 16:44:25
 */
public class DataSourceAction extends BaseAction {
	private static final Logger logger = LogManager.getLogger();

	/**
	 * 新增/编辑数据源
	 * <p>
	 * 1.数据可访问权限处理方式，新增时会设置创建用户ID，会获取当前用户ID，所以不需要权限验证
	 * 2.字段合法性检查
	 * 3.对data_source初始化一些非页面传值
	 * 4.保存data_source信息
	 * 5.保存source_relation_dep表信息
	 *
	 * @param dataSource data_source
	 *                   含义：data_source表实体类
	 *                   取值范围：datasource_name不为空以及不为空格，datasource_number不为空以及不为空格
	 *                   *                   ，source_remark可为空，其余字段取系统值
	 * @param depIds     long[]
	 *                   含义：存储source_relation_dep表主键ID的数组，定义为Long是为了
	 *                   判断是否为null
	 *                   取值范围：不为空以及不为空格
	 */
	public void saveDataSource(@RequestBean Data_source dataSource, Long[] depIds) {
		// 1.数据可访问权限处理方式，新增时会设置创建用户ID，会获取当前用户ID，所以不需要权限验证
		// 2.字段做合法性检查
		fieldLegalityValidation(dataSource.getDatasource_name(), dataSource.getDatasource_number(),
				depIds);
		// 3.对data_source初始化一些非页面传值
		// 数据源主键ID
		dataSource.setSource_id(PrimayKeyGener.getNextId());
		// 数据源创建用户ID
		dataSource.setCreate_user_id(getUserId());
		// 数据源创建日期
		dataSource.setCreate_date(DateUtil.getSysDate());
		// 数据源创建时间
		dataSource.setCreate_time(DateUtil.getSysTime());
		// 4.保存data_source信息
		dataSource.add(Dbo.db());
		// 5.保存source_relation_dep表信息
		saveSourceRelationDep(dataSource.getSource_id(), depIds);
	}

	/**
	 * 更新数据源信息
	 * <p>
	 * 1.数据可访问权限处理方式，通过sourceId与user_id关联检查
	 * 2.验证sourceId是否合法
	 * 3.字段合法性检查
	 * 4.将data_source实体数据封装
	 * 5.更新数据源信息
	 * 6.先删除数据源与部门关系信息
	 * 7.保存source_relation_dep表信息
	 *
	 * @param sourceId         long
	 *                         含义：data_source表主键，source_relation_dep表外键
	 *                         取值范围：不为空，10位数字
	 * @param sourceRemark     String
	 *                         含义：备注
	 *                         取值范围：没有限制
	 * @param datasourceName   String
	 *                         含义：数据源名称
	 *                         取值范围：不为空且不为空格
	 * @param datasourceNumber String
	 *                         含义：数据源编号
	 *                         取值范围：不为空且不为空格，长度不超过四位
	 * @param depIds           Long[]
	 *                         含义：存储source_relation_dep表主键ID的数组，定义为Long是为了
	 *                         判断是否为null
	 *                         取值范围：不为空以及不为空格
	 */
	public void updateDataSource(Long sourceId, String sourceRemark, String datasourceName,
	                             String datasourceNumber, Long[] depIds) {
		// 1.数据可访问权限处理方式，通过sourceId与user_id关联检查
		if (Dbo.queryNumber("select count(1) from data_source where sourceId=? and " +
				" create_user_id=?", sourceId, getUserId()).orElse(Long.MIN_VALUE) == 0) {
			throw new BusinessException("数据权限校验失败，数据不可访问！");
		}
		//sourceId长度
		// 2.验证sourceId是否合法
		if (sourceId == null) {
			throw new BusinessException("sourceId不为空以及不为空格，新增时自动生成");
		}
		// 3.字段合法性检查
		fieldLegalityValidation(datasourceName, datasourceNumber, depIds);
		// 4.将data_source实体数据封装
		Data_source dataSource = new Data_source();
		dataSource.setSource_id(sourceId);
		dataSource.setDatasource_name(datasourceName);
		dataSource.setDatasource_number(datasourceNumber);
		dataSource.setSource_remark(sourceRemark);
		// 5.更新数据源信息
		dataSource.update(Dbo.db());
		// 6.先删除数据源与部门关系信息
		int num = Dbo.execute("delete from source_relation_dep where sourceId=?",
				dataSource.getSource_id());
		if (num < 1) {
			throw new BusinessException("编辑时会先删除原数据源与部门关系信息，删除错旧关系时错误，" +
					"sourceId=" + dataSource.getSource_id());
		}
		// 7.保存source_relation_dep表信息
		saveSourceRelationDep(sourceId, depIds);
	}

	/**
	 * 字段合法性验证
	 * <p>
	 * 1.数据可访问权限处理方式，这是个私有方法，不会单独被调用，所以不需要权限验证
	 * 2.循环遍历获取source_relation_dep主键ID，验证dep_id合法性
	 * 3.验证datasource_name是否合法
	 * 4.datasource_number是否合法
	 * 5.更新前查询数据源编号是否已存在
	 *
	 * @param datasourceName   String
	 *                         含义：数据源名称
	 *                         取值范围：不为空及空格
	 * @param datasourceNumber String
	 *                         含义：数据源编号
	 *                         取值范围：不为空以及不为空格，长度不超过4
	 * @param depIds           Long[]
	 *                         含义：存储source_relation_dep主键ID(dep_id)的数组,定义为Long是为了
	 *                         判断是否为null
	 *                         取值范围：10为数字
	 */
	private void fieldLegalityValidation(String datasourceName, String datasourceNumber, Long[] depIds) {
		// 1.数据可访问权限处理方式，通过create_user_id检查
		// 2.循环遍历获取source_relation_dep主键ID，验证dep_id合法性
		for (Long depId : depIds) {
			if (depId == null) {
				throw new BusinessException("部门不能为空或者空格，新增部门时通过主键生成!");
			}
		}
		// 3.验证datasource_name是否合法
		if (StringUtil.isBlank(datasourceName)) {
			throw new BusinessException("数据源名称不能为空以及不能为空格，datasource_name=" + datasourceName);
		}
		// 数据源编号长度
		int len = 4;
		// 4.datasource_number是否合法
		if (StringUtil.isBlank(datasourceNumber) || datasourceNumber.length() > len) {
			throw new BusinessException("数据源编号不能为空以及不能为空格或数据源编号长度不能超过四位，" +
					"datasource_number=" + datasourceNumber);
		}
		// 5.更新前查询数据源编号是否已存在
		if (Dbo.queryNumber("select count(1) from " + Data_source.TableName + "  where datasource_number=? "
				+ " and create_user_id=?", datasourceNumber, getUserId()).orElse(Long.MIN_VALUE) > 0) {
			// 判断数据源编号是否重复
			throw new BusinessException("数据源编号重复,datasource_number=" + datasourceNumber);
		}
	}

	/**
	 * 保存数据源与部门关系表信息
	 * <p>
	 * 1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以这里不需要做权限验证
	 * 2.创建source_relation_dep对象
	 * 2.1设置source_relation_dep表外键ID
	 * 3.分隔部门depIds获取到封装所有部门ID的数组
	 * 3.1设置source_relation_dep表主键ID
	 * 4.循环遍历存储部门ID的数组
	 * 4.1设置source_relation_dep表主键ID
	 * 5.循环保存source_relation_dep表信息
	 *
	 * @param sourceId long
	 *                 含义：source_relation_dep表外键ID
	 *                 取值范围，不能为空以及不能为空格
	 * @param depIds   Long[]
	 *                 含义：存放source_relation_dep表主键ID的数组
	 *                 取值范围：不为空
	 */
	private void saveSourceRelationDep(long sourceId, Long[] depIds) {
		// 1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以这里不需要做权限验证
		// 2.创建source_relation_dep对象
		Source_relation_dep sourceRelationDep = new Source_relation_dep();
		// 2.1设置source_relation_dep表外键ID
		sourceRelationDep.setSource_id(sourceId);
		// 3.分隔部门depIds获取到封装所有部门ID的数组
		// 4.循环遍历存储部门ID的数组
		for (long depId : depIds) {
			// 4.1设置source_relation_dep表主键ID
			sourceRelationDep.setDep_id(depId);
			// 5.循环保存source_relation_dep表信息
			sourceRelationDep.add(Dbo.db());
		}
	}

	/**
	 * 根据数据源编号查询数据源及数据源与部门关系信息
	 *
	 * <p>
	 * 1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查
	 * 2.关联查询data_source表与source_relation_dep表信息
	 *
	 * @param sourceId long
	 *                 含义：data_source表主键，source_relation_dep表外键
	 *                 取值范围：不能为空或空格
	 * @return java.util.List                                                           <
	 * 含义：返回关联查询data_source表与source_relation_dep表信息结果
	 * 取值范围：无限制
	 */
	public List<Map<String, Object>> searchDataSource(long sourceId) {
		// 1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查
		// 2.关联查询data_source表与source_relation_dep表信息
		return Dbo.queryList("select ds.*,srd.dep_id from data_source ds " +
				" join source_relation_dep srd on ds.sourceId=srd.sourceId " +
				"  where ds.sourceId = ? and ds.create_user_id=?", sourceId, getUserId());
	}

	/**
	 * 删除数据源信息
	 * <p>
	 * 1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查
	 * 2.先查询该datasource下是否还有agent,有不能删除，没有，可以删除
	 * 3.删除data_source表信息
	 * 4.删除source_relation_dep信息,因为一个数据源可能对应多个部门，所以这里无法使用DboExecute的删除方法
	 * 5.删除source_relation_dep信息
	 *
	 * @param sourceId long
	 *                 含义：data_source表主键，agent_info表外键
	 *                 取值范围：不可为空以及不可为空格
	 */
	public void deleteDataSource(long sourceId) {

		// 1.数据可访问权限处理方式，以下SQL关联sourceId与user_id检查
		// 2.先查询该datasource下是否还有agent
		if (Dbo.queryNumber("SELECT count(1) FROM agent_info  WHERE sourceId=?" +
				" and user_id=?", sourceId, getUserId()).orElse(Long.MIN_VALUE) > 0) {
			throw new BusinessException("此数据源下还有agent，不能删除,sourceId=" + sourceId);
		}
		// 3.删除data_source表信息
		DboExecute.deletesOrThrow("删除数据源信息表data_source失败，sourceId=" + sourceId,
				"delete from data_source where sourceId=? and create_user_id=?", sourceId, getUserId());
		// 4.删除source_relation_dep信息,因为一个数据源可能对应多个部门，所以这里无法使用DboExecute的删除方法
		int srdNum = Dbo.execute("delete from source_relation_dep where sourceId=?", sourceId);
		if (srdNum < 1) {
			// 如果数据源存在，那么部门一定存在，所以这里不需要判断等于0的情况
			throw new BusinessException("删除该数据源下source_relation_dep表数据错误，sourceId="
					+ sourceId);
		}
	}

	/**
	 * 上传文件，数据源下载文件提供的文件中涉及到的所有表的数据导入数据库中对应的表中（修改中，未完成测试）
	 *
	 * <p>
	 * 1.通过文件名称获取文件
	 * 2.获取文件名
	 * 3.处理获取到的上传文件的文件名的路径部分，只保留文件名部分
	 * 4.获得该文件的缓冲输入流
	 * 5.创建一个缓存区，一次读取1kb
	 * 6.把bis里的东西读到bytes数组里去
	 * 7.循环读取写入，将读取的字节转为字符串对象
	 * 8.关闭输入流
	 * 9.使用base64对数据进行编码
	 * 10.导入数据源数据，将涉及到的所有表的数据导入数据库中对应的表中
	 *
	 * @param agentIp       String
	 *                      含义：agent地址
	 *                      取值范围：不能为空，服务器ip地址
	 * @param agentPort     String
	 *                      含义：agent端口
	 *                      取值范围：1024-65535
	 * @param userCollectId long
	 *                      含义：数据采集用户ID
	 *                      取值范围：不能为空以及空格，页面传值
	 * @param file          String
	 *                      含义：上传文件名称（全路径）
	 *                      取值范围：不能为空以及空格
	 */
	@UploadFile
	public void uploadFile(String agentIp, String agentPort, long userCollectId, String file) {
		try {
			// 1.通过文件名称获取文件
			File uploadedFile = FileUploadUtil.getUploadedFile(file);
			/*// 2.获取文件名
			String fileName = FileUploadUtil.getOriginalFileName(file);
			*//*注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，
			如： c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt*//*
			// 3.处理获取到的上传文件的文件名的路径部分，只保留文件名部分
			// FIXME File.separator与原始文件的分隔符不见得一致！ 应该分别找一次
			fileName = fileName.substring(fileName.lastIndexOf(File.separator +
					File.separator) + 1);
			// 4.获得该文件的缓冲输入流
			BufferedInputStream bis =
					new BufferedInputStream(new FileInputStream(uploadedFile));
			// 5.创建一个缓存区，一次读取1kb
			byte[] bytes = new byte[1024];
			int len = 0;
			StringBuilder sb = new StringBuilder();
			// 6.把bis里的东西读到bytes数组里去
			while ((len = bis.read(bytes)) != 0) {
				// 7.循环读取写入，将读取的字节转为字符串对象
				sb.append((new String(bytes, 0, len, CodecUtil.UTF8_CHARSET)));
			}
			// try catch
			// 8.关闭输入流
			bis.close();
			// 9.使用base64编码
			String strTemp = new String(Base64.getDecoder().decode(sb.toString()),
					CodecUtil.UTF8_CHARSET);*/
			String strTemp = new String(Base64.getDecoder().decode(Files.readAllBytes(uploadedFile.toPath())));

			// 10.导入数据源数据，将涉及到的所有表的数据导入数据库中对应的表中
			importDclData(strTemp, agentIp, agentPort, userCollectId, getUserId());
		} catch (Exception e) {
			throw new AppSystemException(e);
		}
	}

	/**
	 * 导入数据源数据，将涉及到的所有表的数据导入数据库中对应的表中(修改中,未完成测试）
	 *
	 * <p>
	 * 1.获取文件对应所有表信息的map
	 * 2.遍历并解析拿到每张表的信息，map里封装的是所有数据源相关的表信息
	 * 3.获取数据源信息并插入数据库
	 * 4.将department_info表数据插入数据库
	 * 5.将source_relation_dep数据插入数据库
	 * 6.将agent_info表数据插入数据库
	 * 7.将Agent_down_info表数据插入数据库
	 * 8.将collect_job_classify表数据插入数据库
	 * 9.将ftp采集设置ftp_collect表数据插入数据库
	 * 10.将ftp已传输表ftp_transfered表数据插入数据库
	 * 11.将ftp目录表ftp_folder表数据插入数据库
	 * 12.将对象采集设置object_collect表数据插入数据库
	 * 13.将对象采集对应信息object_collect_task表数据插入数据库
	 * 14.将对象采集存储设置object_storage表数据插入数据库
	 * 15.将对象采集结构信息object_collect_struct表数据插入数据库
	 * 16.将数据库设置database_set表数据插入数据库
	 * 17.将文件系统设置file_collect_set表数据插入数据库
	 * 18.将文件源设置file_source表数据插入数据库
	 * 19.将信号文件入库信息signal_file表数据插入数据库
	 * 20.将数据库对应的表table_info表数据插入数据库
	 * 21.将列合并信息column_merge表数据插入数据库
	 * 22.将表存储信息table_storage_info表数据插入数据库
	 * 23.将表清洗参数信息table_clean表数据插入数据库
	 * 24.将表对应的字段table_column表数据插入数据库
	 * 25.将列清洗参数信息column_clean表数据插入数据库
	 * 26.将列拆分信息表column_split表数据插入数据库
	 *
	 * @param strTemp       String
	 *                      含义：涉及数据源文件下载相关的所有表进行base64编码后的信息
	 * @param agentIp       String
	 *                      含义：agent地址
	 *                      取值范围：不能为空，服务器ip地址
	 * @param agentPort     String
	 *                      含义：agent端口
	 *                      取值范围：1024-65535
	 * @param userCollectId long
	 *                      含义：数据采集用户ID，指定谁可以查看该用户对应表信息
	 *                      取值范围：不能为空以及空格，长度不超过10
	 * @param userId        long
	 *                      含义：data_source表数据源创建用户ID，代表数据是由谁创建的
	 *                      取值范围：不为空以及不为空格，长度不超过10位
	 */
	private void importDclData(String strTemp, String agentIp, String agentPort, long
			userCollectId, long userId) {
		Type type = new TypeReference<Map<String, Object>>() {
		}.getType();
		// 1.获取文件对应所有表信息的map
		Map<String, Object> map = JsonUtil.toObject(strTemp, type);
		// 2.遍历并解析拿到每张表的信息，map里封装的是所有数据源相关的表信息
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			// 3.获取数据源信息并插入数据库
			Data_source dataSource = getDataSource(userId, entry);
			// 4.将department_info表数据插入数据库
			List<Department_info> departmentInfoList = addDepartmentInfo(entry);
			// 5.将source_relation_dep数据插入数据库
			addSourceRelationDep(entry, dataSource, departmentInfoList);
			// 6.将agent_info表数据插入数据库
			addAgentInfo(agentIp, agentPort, userCollectId, entry);
			// 7.将Agent_down_info表数据插入数据库
			addAgentDownInfo(agentIp, agentPort, userCollectId, entry);
			// 8.将collect_job_classify表数据插入数据库
			addCollectJobClassify(userId, entry);
			// 9.将ftp采集设置ftp_collect表数据插入数据库
			addFtpCollect(entry);
			// 10.将ftp已传输表ftp_transfered表数据插入数据库
			addFtpTransfered(entry);
			// 11.将ftp目录表ftp_folder表数据插入数据库
			addFtpFolder(entry);
			// 12.将对象采集设置object_collect表数据插入数据库
			addObjectCollect(entry);
			// 13.将对象采集对应信息object_collect_task表数据插入数据库
			addObjectCollectTask(entry);
			// 14.将对象采集存储设置object_storage表数据插入数据库
			addObjectStorage(entry);
			// 15.将对象采集结构信息object_collect_struct表数据插入数据库
			addObjectCollectStruct(entry);
			// 16.将数据库设置database_set表数据插入数据库
			addDatabaseSet(entry);
			// 17.将文件系统设置file_collect_set表数据插入数据库
			addFileCollectSet(entry);
			// 18.将文件源设置file_source表数据插入数据库
			addFileSource(entry);
			// 19.将信号文件入库信息signal_file表数据插入数据库
			addSignalFile(entry);
			// 20.将数据库对应的表table_info表数据插入数据库
			addTableInfo(entry);
			// 21.将列合并信息column_merge表数据插入数据库
			addColumnMerge(entry);
			// 22.将表存储信息table_storage_info表数据插入数据库
			addTableStorageInfo(entry);
			// 23.将表清洗参数信息table_clean表数据插入数据库
			addTableClean(entry);
			// 24.将表对应的字段table_column表数据插入数据库
			addTableColumn(entry);
			// 25.将列清洗参数信息column_clean表数据插入数据库
			addColumnClean(entry);
			// 26.将列拆分信息表column_split表数据插入数据库
			addColumnSplit(entry);
		}
	}

	/**
	 * 将table_storage_info表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为table_storage_info对应表数据
	 * 3.获取表存储信息table_storage_info信息
	 * 4.将table_storage_info表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addTableStorageInfo(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String tsi = "tableStorageInfo";
		// 2.判断map中的key值是否为table_storage_info对应表数据
		if (tsi.equals(entry.getKey())) {
			Type tsiType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取表存储信息table_storage_info信息
			List<Table_storage_info> tsiList = JsonUtil.toObject(entry.getValue().toString(), tsiType);
			// 4.将table_storage_info表数据循环入数据库
			for (Table_storage_info tableStorageInfo : tsiList) {
				tableStorageInfo.add(Dbo.db());
			}
		}
	}

	/**
	 * 将column_merge表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为column_merge对应表数据
	 * 3.获取列合并信息column_merge信息
	 * 4.将column_merge表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addColumnMerge(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String cm = "columnMerge";
		// 2.判断map中的key值是否为column_merge对应表数据
		if (cm.equals(entry.getKey())) {
			Type cmType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取列合并信息column_merge信息
			List<Column_merge> columnMergeList = JsonUtil.toObject(entry.getValue().toString(), cmType);
			// 4.将column_merge表数据循环入数据库
			for (Column_merge columnMerge : columnMergeList) {
				columnMerge.add(Dbo.db());
			}
		}
	}

	/**
	 * 将column_split表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为column_split对应表数据
	 * 3.获取列拆分信息表column_split信息
	 * 4.将column_split表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addColumnSplit(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String cs = "columnSplit";
		// 2.判断map中的key值是否为column_split对应表数据
		if (cs.equals(entry.getKey())) {
			Type csType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取列拆分信息表column_split信息
			List<Column_split> columnSplitList = JsonUtil.toObject(entry.getValue().toString(), csType);
			// 4.将column_split表数据循环入数据库
			for (Column_split columnSplit : columnSplitList) {
				columnSplit.add(Dbo.db());
			}
		}
	}

	/**
	 * 将column_clean表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为column_clean对应表数据
	 * 3.获取列清洗参数信息column_clean信息
	 * 4.将column_clean表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addColumnClean(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String cc = "columnClean";
		// 2.判断map中的key值是否为column_clean对应表数据
		if (cc.equals(entry.getKey())) {
			Type ccType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取列清洗参数信息column_clean信息
			List<Column_clean> columnCleanList = JsonUtil.toObject(entry.getValue().toString(), ccType);
			// 4.将column_clean表数据循环入数据库
			for (Column_clean columnClean : columnCleanList) {
				columnClean.add(Dbo.db());
			}
		}
	}

	/**
	 * 将table_column表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为table_column对应表数据
	 * 3.获取表对应的字段table_column信息
	 * 4.将table_column表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addTableColumn(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String tc = "tableColumn";
		// 2.判断map中的key值是否为table_column对应表数据
		if (tc.equals(entry.getKey())) {
			Type tcnType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取表对应的字段table_column信息
			List<Table_column> tableColumnList = JsonUtil.toObject(entry.getValue().toString(), tcnType);
			// 4.将table_column表数据循环入数据库
			for (Table_column tableColumn : tableColumnList) {
				tableColumn.add(Dbo.db());
			}
		}
	}

	/**
	 * 将table_clean表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为table_clean对应表数据
	 * 3.获取表清洗参数信息table_clean信息
	 * 4.将table_clean表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addTableClean(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String tc = "tableClean";
		// 2.判断map中的key值是否为table_clean对应表数据
		if (tc.equals(entry.getKey())) {
			Type tcType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取表清洗参数信息table_clean信息
			List<Table_clean> tableCleanList = JsonUtil.toObject(entry.getValue().toString(), tcType);
			// 4.将table_clean表数据循环入数据库
			for (Table_clean tableClean : tableCleanList) {
				tableClean.add(Dbo.db());
			}
		}
	}

	/**
	 * 将table_info表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为table_info对应表数据
	 * 3.获取数据库对应的表table_info信息
	 * 4.将table_info表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addTableInfo(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String ti = "tableInfo";
		// 2.判断map中的key值是否为table_info对应表数据
		if (ti.equals(entry.getKey())) {
			Type tiType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取数据库对应的表table_info信息
			List<Table_info> tableInfoList = JsonUtil.toObject(entry.getValue().toString(), tiType);
			// 4.将table_info表数据循环入数据库
			for (Table_info tableInfo : tableInfoList) {
				tableInfo.add(Dbo.db());
			}
		}
	}

	/**
	 * 将signal_file表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为signal_file对应表数据
	 * 3.获取信号文件入库信息signal_file信息
	 * 4.将signal_file表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addSignalFile(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String sf = "signalFile";
		// 2.判断map中的key值是否为signal_file对应表数据
		if (sf.equals(entry.getKey())) {
			Type sfType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取信号文件入库信息signal_file信息
			List<Signal_file> signalFileList = JsonUtil.toObject(entry.getValue().toString(), sfType);
			// 4.将signal_file表数据循环入数据库
			for (Signal_file signalFile : signalFileList) {
				signalFile.add(Dbo.db());
			}
		}
	}

	/**
	 * 将file_source表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为file_source对应表数据
	 * 3.获取文件源设置file_source信息
	 * 4.将file_source表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addFileSource(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String fs = "fileSource";
		// 2.判断map中的key值是否为file_source对应表数据
		if (fs.equals(entry.getKey())) {
			Type fsType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取文件源设置file_source信息
			List<File_source> fileSourceList = JsonUtil.toObject(entry.getValue().toString(), fsType);
			// 4.将file_source表数据循环入数据库
			for (File_source fileSource : fileSourceList) {
				fileSource.add(Dbo.db());
			}
		}
	}

	/**
	 * 将file_collect_set表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为file_collect_set对应表数据
	 * 3.获取文件系统设置file_collect_set信息
	 * 4.将file_collect_set表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addFileCollectSet(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String fcs = "fileCollectSet";
		// 2.判断map中的key值是否为file_collect_set对应表数据
		if (fcs.equals(entry.getKey())) {
			Type fcsType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取文件系统设置file_collect_set信息
			List<File_collect_set> fcsList = JsonUtil.toObject(entry.getValue().toString(), fcsType);
			// 4.将file_collect_set表数据循环入数据库
			for (File_collect_set fileCollectSet : fcsList) {
				fileCollectSet.add(Dbo.db());
			}
		}
	}

	/**
	 * 将database_set表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为database_set对应表数据
	 * 3.获取数据库设置database_set信息
	 * 4.将database_set表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addDatabaseSet(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String ds = "databaseSet";
		// 2.判断map中的key值是否为database_set对应表数据
		if (ds.equals(entry.getKey())) {
			Type dsType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取数据库设置database_set信息
			List<Database_set> databaseSetList = JsonUtil.toObject(entry.getValue().toString(), dsType);
			// 4.将database_set表数据循环入数据库
			for (Database_set databaseSet : databaseSetList) {
				databaseSet.add(Dbo.db());
			}
		}
	}

	/**
	 * 将object_collect_struct表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为object_collect_struct对应表数据
	 * 3.获取对象采集结构信息object_collect_struct信息
	 * 4.将object_collect_struct表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addObjectCollectStruct(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String ocs = "objectCollectStruct";
		// 2.判断map中的key值是否为object_collect_struct对应表数据
		if (ocs.equals(entry.getKey())) {
			Type ocsType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取对象采集结构信息object_collect_struct信息
			List<Object_collect_struct> ocsList = JsonUtil.toObject(entry.getValue().toString(), ocsType);
			// 4.将object_collect_struct表数据循环入数据库
			for (Object_collect_struct objectCollectStruct : ocsList) {
				objectCollectStruct.add(Dbo.db());
			}
		}
	}

	/**
	 * 将object_storage表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为object_storage对应表数据
	 * 3.获取tp采集设置object_storage信息
	 * 4.将object_storage表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addObjectStorage(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String os = "objectStorage";
		// 2.判断map中的key值是否为object_storage对应表数据
		if (os.equals(entry.getKey())) {
			Type osType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取对象采集存储设置object_storage信息
			List<Object_storage> objectStorageList = JsonUtil.toObject(entry.getValue().toString(), osType);
			// 4.将object_storage表数据循环入数据库
			for (Object_storage objectStorage : objectStorageList) {
				objectStorage.add(Dbo.db());
			}
		}
	}

	/**
	 * 将object_collect_task表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为object_collect_task对应表数据
	 * 3.获取对象采集对应信息object_collect_task信息
	 * 4.将object_collect_task表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addObjectCollectTask(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String oct = "objectCollectTask";
		// 2.判断map中的key值是否为object_collect_task对应表数据
		if (oct.equals(entry.getKey())) {
			Type octType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取对象采集对应信息object_collect_task信息
			List<Object_collect_task> octList = JsonUtil.toObject(entry.getValue().toString(), octType);
			// 4.将object_collect_task表数据循环入数据库
			for (Object_collect_task objectCollectTask : octList) {
				objectCollectTask.add(Dbo.db());
			}
		}
	}

	/**
	 * 将object_collect表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为object_collect对应表数据
	 * 3.获取对象采集设置object_collect信息
	 * 4.将object_collect表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addObjectCollect(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String oc = "objectCollect";
		// 2.判断map中的key值是否为object_collect对应表数据
		if (oc.equals(entry.getKey())) {
			Type ocType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取对象采集设置object_collect信息
			List<Object_collect> objectCollectList = JsonUtil.toObject(entry.getValue().toString(), ocType);
			// 4 .将object_collect表数据循环入数据库
			for (Object_collect objectCollect : objectCollectList) {
				objectCollect.add(Dbo.db());
			}
		}
	}

	/**
	 * 将ftp_folder表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为ftp_folder对应表数据
	 * 3.获取ftp目录表ftp_folder信息
	 * 4.将ftp_folder表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addFtpFolder(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String ff = "ftpFolder";
		// 2.判断map中的key值是否为ftp_folder对应表数据
		if (ff.equals(entry.getKey())) {
			Type ffType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取ftp目录表ftp_folder信息
			List<Ftp_folder> ftpFolderList = JsonUtil.toObject(entry.getValue().toString(), ffType);
			// 4.将ftp_folder表数据循环入数据库
			for (Ftp_folder ftpFolder : ftpFolderList) {
				ftpFolder.setFtp_folder_id(PrimayKeyGener.getNextId());
				ftpFolder.add(Dbo.db());
			}
		}
	}

	/**
	 * 将ftp_transfered表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为ftp_transfered对应表数据
	 * 3.获取tp采集设置ftp_transfered信息
	 * 4.将ftp_transfered表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addFtpTransfered(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String ft = "ftpTransfered";
		// 2.判断map中的key值是否为ftp_transfered对应表数据
		if (ft.equals(entry.getKey())) {
			Type ftType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取ftp已传输表ftp_transfered信息
			List<Ftp_transfered> transferList = JsonUtil.toObject(entry.getValue().toString(), ftType);
			// 4.将ftp_transfered表数据循环入数据库
			for (Ftp_transfered ftpTransfered : transferList) {
				ftpTransfered.setFtp_id(PrimayKeyGener.getNextId());
				ftpTransfered.add(Dbo.db());
			}
		}
	}

	/**
	 * 将ftp_collect表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为ftp_collect对应表数据
	 * 3.获取tp采集设置ftp_collect信息
	 * 4.将ftp_collect表数据循环入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private void addFtpCollect(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String fc = "ftpCollect";
		// 2.判断map中的key值是否为ftp_collect对应表数据
		if (fc.equals(entry.getKey())) {
			Type fcType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取tp采集设置ftp_collect信息
			List<Ftp_collect> ftpCollectList = JsonUtil.toObject(entry.getValue().toString(), fcType);
			// 4.将ftp_collect表数据循环入数据库
			for (Ftp_collect ftpCollect : ftpCollectList) {
				ftpCollect.setFtp_id(PrimayKeyGener.getNextId());
				ftpCollect.add(Dbo.db());
			}
		}
	}

	/**
	 * 将collect_job_classify表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为collect_job_classify对应表数据
	 * 3.获取采集任务分类表collect_job_classify信息
	 * 4.将collect_job_classify表数据循环入数据库
	 *
	 * @param userCollectId long
	 *                      含义：数据采集用户，代表此数据属于哪个用户
	 *                      取值范围：不为空以及不为空格，长度不超过10位
	 * @param entry         java.util.Map.Entry
	 *                      含义：所有表数据的map的实体
	 *                      取值范围：不为空
	 */
	private void addCollectJobClassify(long userCollectId, Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String cjc = "collectJobClassify";
		// 2.判断map中的key值是否为collect_job_classify对应表数据
		if (cjc.equals(entry.getKey())) {
			Type cjcType = new TypeReference<List<Collect_job_classify>>() {
			}.getType();
			// 3.获取采集任务分类表collect_job_classify信息
			List<Collect_job_classify> cjcList = JsonUtil.toObject(entry.getValue().toString(), cjcType);
			// 4.将collect_job_classify表数据循环入数据库
			for (Collect_job_classify collectJobClassify : cjcList) {
				collectJobClassify.setClassify_id(PrimayKeyGener.getNextId());
				collectJobClassify.setUser_id(userCollectId);
				collectJobClassify.add(Dbo.db());
			}
		}
	}

	/**
	 * 将agent_down_info表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为agent_down_info对应表数据
	 * 3.获取agent_down_info表数据
	 * 4.将agent_down_info表数据循环入数据库
	 *
	 * @param agentIp       String
	 *                      含义：agent地址
	 *                      取值范围：不能为空，服务器ip地址
	 * @param agentPort     String
	 *                      含义：agent端口
	 *                      取值范围：1024-65535
	 * @param userCollectId long
	 *                      含义：数据采集用户，代表此数据属于哪个用户
	 *                      取值范围：不为空以及不为空格，长度不超过10位
	 * @param entry         java.util.Map.Entry
	 *                      含义：所有表数据的map的实体
	 *                      取值范围：不为空
	 */
	private void addAgentDownInfo(String agentIp, String agentPort, long userCollectId,
	                              Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String adi = "agentDownInfo";
		// 2.判断map中的key值是否为agent_down_info对应表数据
		if (adi.equals(entry.getKey())) {
			Type adiType = new TypeReference<Map<String, Object>>() {
			}.getType();
			// 3.获取agent_down_info表数据
			List<Agent_down_info> agentDownInfoList = JsonUtil.toObject(entry.getValue().toString(), adiType);
			// 4.将agent_down_info表数据循环入数据库
			for (Agent_down_info agentDownInfo : agentDownInfoList) {
				agentDownInfo.setDown_id(PrimayKeyGener.getNextId());
				agentDownInfo.setUser_id(userCollectId);
				agentDownInfo.setAgent_ip(agentIp);
				agentDownInfo.setAgent_port(agentPort);
				agentDownInfo.add(Dbo.db());
			}
		}
	}

	/**
	 * 将department_info表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.创建新的department_info表数据集合
	 * 3.判断map中的key值是否为对应department_info表数据
	 * 4.获取部门表department_info表数据
	 * 5.将department_info表数据循环插入数据库
	 *
	 * @param entry java.util.Map.Entry
	 *              含义：所有表数据的map的实体
	 *              取值范围：不为空
	 */
	private List<Department_info> addDepartmentInfo(Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String di = "departmentInfo";
		// 2.创建新的department_info表数据集合
		List<Department_info> departmentInfoListNew = new ArrayList<>();
		// 3.判断map中的key值是否为对应department_info表数据
		if (di.equals(entry.getKey())) {
			Type srdType = new TypeReference<List<Department_info>>() {
			}.getType();
			// 4.获取部门表department_info表数据
			List<Department_info> departmentInfoList = JsonUtil.toObject(entry.getValue().toString(), srdType);
			for (Department_info departmentInfo : departmentInfoList) {
				departmentInfo.setDep_id(PrimayKeyGener.getNextId());
				// 4.将department_info表数据循环插入数据库
				departmentInfo.add(Dbo.db());
			}
		}
		// 5.返回新的department_info表数据集合
		return departmentInfoListNew;
	}

	/**
	 * 将source_relation_dep表数据插入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为对应source_relation_dep表数据
	 * 3.获取数据源和部门关系表source_relation_dep表数据
	 * 4.遍历department_info表数据，目的是获取最新的部门ID，dep_id
	 * 5.将source_relation_dep表数据循环插入数据库
	 *
	 * @param entry              java.util.Map.Entry
	 *                           含义：所有表数据的map的实体
	 *                           取值范围：不为空
	 * @param dataSource         entity
	 *                           含义： source_relation_dep表对应实体
	 *                           取值范围：不为空
	 * @param departmentInfoList java.uti.List
	 *                           含义：存放department_info表数据的集合
	 *                           取值范围：不为空
	 */
	private void addSourceRelationDep(Map.Entry<String, Object> entry, Data_source dataSource,
	                                  List<Department_info> departmentInfoList) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String srd = "sourceRelationDep";
		// 2.判断map中的key值是否为对应source_relation_dep表数据
		if (srd.equals(entry.getKey())) {
			Type srdType = new TypeReference<List<Source_relation_dep>>() {
			}.getType();
			// 3.获取数据源和部门关系表source_relation_dep表数据
			List<Source_relation_dep> srdList = JsonUtil.toObject(entry.getValue().toString(), srdType);
			for (Source_relation_dep sourceRelationDep : srdList) {
				sourceRelationDep.setSource_id(dataSource.getSource_id());
				// 4.遍历department_info表数据，目的是获取最新的部门ID，dep_id
				for (Department_info departmentInfo : departmentInfoList) {
					sourceRelationDep.setDep_id(departmentInfo.getDep_id());
					// 5.将source_relation_dep表数据循环插入数据库
					sourceRelationDep.add(Dbo.db());
				}
			}
		}
	}

	/**
	 * 将data_source表数据入库并返回data_source实体对象
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.创建data_source表实体对象
	 * 3.判断map中的key值是否为对应data_source表数据
	 * 4.获取数据源data_source表信息
	 * 5.判断上传文件的数据源名称和已有的名称是否重复
	 * 6.将data_source表数据插入数据库
	 * 7.返回data_source表数据对应实体对象
	 *
	 * @param userId long
	 *               含义：创建用户ID，代表此数据源由哪个用户创建
	 *               取值范围：不为空，创建用户时自动生成
	 * @param entry  java.util.Map.Entry
	 *               含义：所有表数据的map的实体
	 *               取值范围：不为空
	 * @return entity
	 * 含义：data_source表实体对象
	 * 取值范围：不为空
	 */
	private Data_source getDataSource(long userId, Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// 2.创建data_source表实体对象
		Data_source dataSource = new Data_source();
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String ds = "dataSource";
		// 3.判断map中的key值是否为对应data_source表数据
		if (ds.equals(entry.getKey())) {
			// 4.获取数据源data_source表信息
			dataSource = JsonUtil.toObject(entry.getValue().toString(), Data_source.class);
			// 5.判断上传文件的数据源名称和已有的名称是否重复
			if (Dbo.queryNumber("select count(1) from data_source where datasource_name=?",
					dataSource.getDatasource_name()).orElse(Long.MIN_VALUE) > 0) {
				throw new BusinessException("数据源名称重复,datasource_name=" +
						dataSource.getDatasource_name());
			}
			// 6.将data_source表数据插入数据库
			dataSource.setSource_id(PrimayKeyGener.getNextId());
			dataSource.setCreate_user_id(userId);
			dataSource.add(Dbo.db());
		}
		// 7.返回data_source表数据对应实体对象
		return dataSource;
	}

	/**
	 * 将agent_info表信息入数据库
	 * <p>
	 * 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
	 * 2.判断map中的key值是否为对应agent_info表数据
	 * 3.获取agent_info表数据
	 * 4.循环入库agent_info
	 *
	 * @param agentIp       String
	 *                      含义：agent地址
	 *                      取值范围：不能为空，服务器ip地址
	 * @param agentPort     String
	 *                      含义：agent端口
	 *                      取值范围：1024-65535
	 * @param userCollectId long
	 *                      含义：数据采集用户，代表此数据属于哪个用户
	 *                      取值范围：不为空以及不为空格，长度不超过10位
	 * @param entry         java.util.Map.Entry
	 *                      含义：所有表数据的map的实体
	 *                      取值范围：不为空
	 */
	private void addAgentInfo(String agentIp, String agentPort, long userCollectId,
	                          Map.Entry<String, Object> entry) {
		// 1.数据权限处理方式，此方法是私有方法，不需要做权限验证
		// map中key的值，也是下载数据源时对应表信息封装入map的key值
		String ai = "agentInfo";
		// 2.判断map中的key值是否为对应agent_info表数据
		if (ai.equals(entry.getKey())) {
			// 获取agent信息表信息
			Type aiType = new TypeReference<Map<String, Object>>() {
			}.getType();
			// 3.获取agent_info表数据
			List<Agent_info> agentInfoList = JsonUtil.toObject(entry.getValue().toString(), aiType);
			// 4.循环入库agent_info
			for (Agent_info agentInfo : agentInfoList) {
				agentInfo.setAgent_id(PrimayKeyGener.getNextId());
				agentInfo.setUser_id(userCollectId);
				agentInfo.setAgent_ip(agentIp);
				agentInfo.setAgent_port(agentPort);
				if (agentInfo.add(Dbo.db()) != 1) {
					throw new BusinessException("添加agent_info表数据入库失败");
				}
			}
		}
	}


	/**
	 * 下载文件（数据源下载功能使用，下载数据源给数据源导入提供上传文件）,暂时未完成，修改中
	 *
	 * <p>
	 * 1.数据可访问权限处理方式，这里是下载数据源，所以不需要数据权限验证
	 * 2.创建存放所有数据源下载，所有相关表数据库查询获取数据的map集合
	 * 3.获取data_source表信息集合，将data_source表信息封装入map
	 * 4.获取source_relation_dep表信息集合，将source_relation_dep表数据封装入map
	 * 5.获取department_info表信息集合，将department_info表数据封装入map
	 * 6.获取agent_info表信息集合，将agent_info表信息封装入map
	 * 7.获取Agent_down_info表信息集合封装入map
	 * 8.采集任务分类表collect_job_classify，获取collect_job_classify表信息集合入map
	 * 9.ftp采集设置ftp_collect,获取ftp_collect表信息集合
	 * 10.ftp已传输表ftp_transfered,获取ftp_transfered表信息集合入map
	 * 11.ftp目录表ftp_folder,获取ftp_folder表信息集合入map
	 * 12.对象采集设置object_collect,获取object_collect表信息集合入map
	 * 13.对象采集对应信息object_collect_task,获取object_collect_task表信息集合入map
	 * 14.对象采集存储设置object_storage,获取object_storage表信息集合入map
	 * 15.对象采集结构信息object_collect_struct,获取object_collect_struct表信息集合入map
	 * 16.数据库设置database_set,获取database_set表信息集合入map
	 * 17.文件系统设置file_collect_set,获取file_collect_set表信息集合入map
	 * 18.文件源设置file_source,获取file_source表信息集合入map
	 * 19.信号文件入库信息signal_file,获取signal_file表信息集合入map
	 * 20.数据库对应的表table_info,获取table_info表信息集合入map
	 * 21.列合并信息表column_merge,获取column_merge表信息集合入map
	 * 22.表存储信息table_storage_info,获取table_storage_info表信息集合入map
	 * 23.表清洗参数信息table_clean,获取table_clean表信息集合入map
	 * 24.表对应的字段table_column,获取table_column表信息集合入map
	 * 25.列清洗参数信息 column_clean,获取column_clean表信息集合入map
	 * 26.列拆分信息表column_split,获取column_split表信息集合入map
	 * 27.使用base64编码
	 * 28.判断文件是否存在
	 * 29.清空response，设置响应头，响应编码格式，控制浏览器下载该文件
	 * 30.通过流的方式写入文件
	 *
	 * @param sourceId long
	 *                 含义：data_source表主键
	 *                 取值范围：不为空以及不为空格，长度不超过10
	 */
	public void downloadFile(@RequestParam long sourceId) {
		// 1.数据可访问权限处理方式，这里是下载数据源，所以不需要数据权限验证
		HttpServletResponse response = ResponseUtil.getResponse();
		try (OutputStream out = response.getOutputStream()) {
			// 2.创建存放所有数据源下载，所有相关表数据库查询获取数据的map集合
			Map<String, Object> collectionMap = new HashMap<>();
			// 3.获取data_source表信息集合，将data_source表信息封装入map
			addDataSourceToMap(sourceId, collectionMap);
			// 4.获取source_relation_dep表信息集合，将source_relation_dep表数据封装入map
			List<Source_relation_dep> sourceRelationDepList = addSourceRelationDepToMap(sourceId, collectionMap);
			// 5.获取department_info表信息集合，将department_info表数据封装入map
			addDepartmentInfoToMap(collectionMap, sourceRelationDepList);
			// 6.获取agent_info表信息集合，将agent_info表信息封装入map
			List<Agent_info> agentInfoList = getAgentInfoList(sourceId, collectionMap);
			// 7.获取Agent_down_info表信息集合封装入map
			addAgentDownInfoToMap(collectionMap, agentInfoList);
			// 8.采集任务分类表collect_job_classify，获取collect_job_classify表信息集合入map
			addCollectJobClassifyToMap(collectionMap, agentInfoList);
			// 9.ftp采集设置ftp_collect,获取ftp_collect表信息集合
			Result ftpCollectResult = getFtpCollectResult(collectionMap, agentInfoList);
			// 10.ftp已传输表ftp_transfered,获取ftp_transfered表信息集合入map
			addFtpTransferedToMap(collectionMap, ftpCollectResult);
			// 11.ftp目录表ftp_folder,获取ftp_folder表信息集合入map
			addFtpFolderToMap(collectionMap, ftpCollectResult);
			// 12.对象采集设置object_collect,获取object_collect表信息集合入map
			Result objectCollectResult = getObjectCollectResult(collectionMap, agentInfoList);
			// 13.对象采集对应信息object_collect_task,获取object_collect_task表信息集合入map
			Result objectCollectTaskResult = getObjectCollectTaskResult(collectionMap, objectCollectResult);
			// 14.对象采集存储设置object_storage,获取object_storage表信息集合入map
			addObjectStorageToMap(collectionMap, objectCollectTaskResult);
			// 15.对象采集结构信息object_collect_struct,获取object_collect_struct表信息集合入map
			addObjectCollectStructResultToMap(collectionMap, objectCollectTaskResult);
			// 16.数据库设置database_set,获取database_set表信息集合入map
			Result databaseSetResult = getDatabaseSetResult(collectionMap, agentInfoList);
			// 17.文件系统设置file_collect_set,获取file_collect_set表信息集合入map
			Result fileCollectSetResult = getFileCollectSetResult(collectionMap, agentInfoList);
			// 18.文件源设置file_source,获取file_source表信息集合入map
			addFileSourceToMap(collectionMap, fileCollectSetResult);
			// 19.信号文件入库信息signal_file,获取signal_file表信息集合入map
			addSignalFileToMap(collectionMap, databaseSetResult);
			// 20.数据库对应的表table_info,获取table_info表信息集合入map
			Result tableInfoResult = getTableInfoResult(collectionMap, databaseSetResult);
			// 21.列合并信息表column_merge,获取column_merge表信息集合入map
			addColumnMergeToMap(collectionMap, tableInfoResult);
			// 22.表存储信息table_storage_info,获取table_storage_info表信息集合入map
			addTableStorageInfoToMap(collectionMap, tableInfoResult);
			// 23.表清洗参数信息table_clean,获取table_clean表信息集合入map
			addTableCleanToMap(collectionMap, tableInfoResult);
			// 24.表对应的字段table_column,获取table_column表信息集合入map
			Result tableColumnResult = getTableColumnResult(collectionMap, tableInfoResult);
			// 25.列清洗参数信息 column_clean,获取column_clean表信息集合入map
			addColumnCleanToMap(collectionMap, tableColumnResult);
			// 26.列拆分信息表column_split,获取column_split表信息集合入map
			addColumnSplitToMap(collectionMap, tableColumnResult);
			// 27.使用base64编码
			byte[] bytes = Base64.getEncoder().encode(JsonUtil.toJson(collectionMap).
					getBytes(CodecUtil.UTF8_CHARSET));
			// 28.判断文件是否存在
			if (bytes == null) {
				throw new BusinessException("此文件不存在");
			}
			// 29.清空response，设置响应编码格式,响应头，控制浏览器下载该文件
			response.reset();
			response.setCharacterEncoding(CodecUtil.UTF8_STRING);
			response.setContentType("APPLICATION/OCTET-STREAM");
			// 30.通过流的方式写入文件
			out.write(bytes);
			out.flush();
		} catch (IOException e) {
			throw new AppSystemException(e);
		}
	}

	/**
	 * 将department_info表数据加入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放department_info表数据的集合
	 * 3.循环source_relation_dep表集合获取dep_id，通过dep_id获取department_info表数据
	 * 4.将department_info表信息加入集合
	 * 5.将department_info表数据集合入map
	 *
	 * @param collectionMap         java.util.Map
	 *                              含义：封装数据源下载信息（这里封装的是department_info表数据集合）
	 *                              取值范围：key唯一
	 * @param sourceRelationDepList java.util.List
	 *                              含义：存放source_relation_dep表数据集合
	 *                              取值范围：不为空
	 */
	private void addDepartmentInfoToMap(Map<String, Object> collectionMap,
	                                    List<Source_relation_dep> sourceRelationDepList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放department_info表数据的集合
		List<Optional<Department_info>> departmentInfoList = new ArrayList<>();
		// 3.循环source_relation_dep表集合获取dep_id，通过dep_id获取department_info表数据
		for (Source_relation_dep sourceRelationDep : sourceRelationDepList) {
			Optional<Department_info> departmentInfo = Dbo.queryOneObject(Department_info.class
					, "select * from department_info where dep_id=?",
					sourceRelationDep.getDep_id());
			// 4.将department_info表信息加入集合
			departmentInfoList.add(departmentInfo);
		}
		// 5.将department_info表数据集合入map
		collectionMap.put("departmentInfo", departmentInfoList);
	}

	/**
	 * 获取agent_info表数据集合
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.将agent_info表数据集合存入map
	 * 3.将agent_info表数据集合返回
	 *
	 * @param sourceId      long
	 *                      含义：data_source表主键，source_relation_dep表外键
	 *                      取值范围：不为空及空格，10位数字，新增data_source表时自动生成
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是source_relation_dep表数据集合）
	 *                      取值范围：key唯一
	 * @return java.util.List
	 * 含义：返回source_relation_dep表数据的集合
	 * 取值范围：不为空
	 */
	private List<Agent_info> getAgentInfoList(long sourceId, Map<String, Object> collectionMap) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		List<Agent_info> agentInfoList = Dbo.queryList(Agent_info.class, "select * from " +
				"agent_info  where sourceId = ?", sourceId);
		// 2.将agent_info表数据集合存入map
		collectionMap.put("agentInfo", agentInfoList);
		// 3.将agent_info表数据集合返回
		return agentInfoList;
	}

	/**
	 * 将source_relation_dep表数据入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.查询数据源与部门关系表信息
	 * 3.将source_relation_dep表数据入map
	 * 4.返回source_relation_dep表数据集合
	 *
	 * @param sourceId      long
	 *                      含义：data_source表主键，source_relation_dep表外键
	 *                      取值范围：不为空及空格，10位数字，新增data_source表时自动生成
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是source_relation_dep表数据集合）
	 *                      取值范围：key唯一
	 * @return java.util.List
	 * 含义：返回source_relation_dep表数据的集合
	 * 取值范围：不为空
	 */
	private List<Source_relation_dep> addSourceRelationDepToMap(long sourceId, Map<String, Object> collectionMap) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.查询数据源与部门关系表信息
		List<Source_relation_dep> sourceRelationDepList = Dbo.queryList(Source_relation_dep.class,
				"select * from source_relation_dep where source_id=?", sourceId);
		// 3.将source_relation_dep表数据入map
		collectionMap.put("sourceRelationDep", sourceRelationDepList);
		// 4.返回source_relation_dep表数据集合
		return sourceRelationDepList;
	}

	/**
	 * 将column_split表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放column_split表信息的集合
	 * 3.遍历table_column结果集获取column_id,通过column_id查询column_split表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将column_split表集合信息存入map
	 *
	 * @param collectionMap     java.util.Map
	 *                          含义：封装数据源下载信息（这里封装的是column_split表数据集合）
	 *                          取值范围：key唯一
	 * @param tableColumnResult fd.ng.db.resultset.Result
	 *                          含义：table_column表数据结果集
	 *                          取值范围：不为空
	 */
	private void addColumnSplitToMap(Map<String, Object> collectionMap, Result tableColumnResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放column_split表信息的集合
		Result columnSplitResult = new Result();
		// 3.遍历table_column结果集获取column_id,通过column_id查询column_split表信息
		for (int i = 0; i < tableColumnResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from column_split where column_id = ?",
					tableColumnResult.getLong(i, "column_id"));
			// 4.将查询到的信息封装入集合
			columnSplitResult.add(result);
		}
		// 5.将column_split表集合信息存入map
		collectionMap.put("columnSplit", columnSplitResult);
	}

	/**
	 * 将column_clean表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放column_clean表信息的集合
	 * 3.遍历table_column结果集获取column_id,通过column_id查询column_clean表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将column_clean表集合信息存入map
	 *
	 * @param collectionMap     java.util.Map
	 *                          含义：封装数据源下载信息（这里封装的是column_clean表数据集合）
	 *                          取值范围：key唯一
	 * @param tableColumnResult fd.ng.db.resultset.Result
	 *                          含义：table_column表数据结果集
	 *                          取值范围：不为空
	 */
	private void addColumnCleanToMap(Map<String, Object> collectionMap, Result tableColumnResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放column_clean表信息的集合
		Result columnCleanResult = new Result();
		// 3.遍历table_column结果集获取column_id,通过column_id查询column_clean表信息
		for (int i = 0; i < tableColumnResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from column_clean where column_id=?",
					tableColumnResult.getLong(i, "column_id"));
			// 4.将查询到的信息封装入集合
			columnCleanResult.add(result);
		}
		// 5.将column_clean表集合信息存入map
		collectionMap.put("columnClean", columnCleanResult);
	}

	/**
	 * 将table_column表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放table_column表信息的集合
	 * 3.遍历table_info结果集获取table_id,通过table_id查询table_column表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将table_column表集合信息存入map
	 * 6.将table_column表结果集返回
	 *
	 * @param collectionMap   java.util.Map
	 *                        含义：封装数据源下载信息（这里封装的是table_column表数据集合）
	 *                        取值范围：key唯一
	 * @param tableInfoResult fd.ng.db.resultset.Result
	 *                        含义：table_info表数据结果集
	 *                        取值范围：不为空
	 */
	private Result getTableColumnResult(Map<String, Object> collectionMap, Result tableInfoResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放table_column表信息的集合
		Result tableColumnResult = new Result();
		// 3.遍历table_info结果集获取table_id,通过table_id查询table_column表信息
		for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from table_column where table_id=?",
					tableInfoResult.getLong(i, "table_id"));
			// 4.将查询到的信息封装入结果集
			tableColumnResult.add(result);
		}
		// 5.将table_column表集合信息存入map
		collectionMap.put("tableColumn", tableColumnResult);
		// 6.将table_column表结果集返回
		return tableColumnResult;
	}

	/**
	 * 将table_clean表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放table_clean表信息的集合
	 * 3.遍历table_info结果集获取table_id,通过table_id查询table_clean表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将table_clean表集合信息存入map
	 *
	 * @param collectionMap   java.util.Map
	 *                        含义：封装数据源下载信息（这里封装的是table_clean表数据集合）
	 *                        取值范围：key唯一
	 * @param tableInfoResult fd.ng.db.resultset.Result
	 *                        含义：table_info表数据结果集
	 *                        取值范围：不为空
	 */
	private void addTableCleanToMap(Map<String, Object> collectionMap, Result tableInfoResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放table_clean表信息的集合
		Result tableCleanResult = new Result();
		// 3.遍历table_info结果集获取table_id,通过table_id查询table_clean表信息
		for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from table_clean where table_id = ?",
					tableInfoResult.getLong(i, "table_id"));
			// 4.将查询到的信息封装入集合
			tableCleanResult.add(result);
		}
		// 5.将table_clean表集合信息存入map
		collectionMap.put("tableClean", tableCleanResult);
	}

	/**
	 * 将object_collect_struct表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 1.创建存放object_collect_struct表信息的集合
	 * 2.遍历object_collect_task结果集获取table_id,通过table_id查询object_collect_struct表信息
	 * 3.将查询到的信息封装入集合
	 * 4.将object_collect_struct表集合信息存入map
	 *
	 * @param collectionMap   java.util.Map
	 *                        含义：封装数据源下载信息（这里封装的是object_collect_struct表数据集合）
	 *                        取值范围：key唯一
	 * @param tableInfoResult fd.ng.db.resultset.Result
	 *                        含义：table_info表数据结果集
	 *                        取值范围：不为空
	 */
	private void addTableStorageInfoToMap(Map<String, Object> collectionMap, Result tableInfoResult) {
		Result tableStorageInfoResult = new Result();
		for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from table_storage_info where table_id = ?",
					tableInfoResult.getLong(i, "table_id"));
			tableStorageInfoResult.add(result);
		}
		collectionMap.put("tableStorageInfo", tableStorageInfoResult);
	}

	/**
	 * 将column_merge表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 1.创建存放column_merge表信息的集合
	 * 2.遍历table_info结果集获取table_id,通过table_id查询column_merge表信息
	 * 3.将查询到的信息封装入集合
	 * 4.将object_collect_struct表集合信息存入map
	 *
	 * @param collectionMap   java.util.Map
	 *                        含义：封装数据源下载信息（这里封装的是column_merge表数据集合）
	 *                        取值范围：key唯一
	 * @param tableInfoResult fd.ng.db.resultset.Result
	 *                        含义：table_info表数据结果集
	 *                        取值范围：不为空
	 */
	private void addColumnMergeToMap(Map<String, Object> collectionMap, Result tableInfoResult) {
		Result columnMergeResult = new Result();
		for (int i = 0; i < tableInfoResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from column_merge where table_id = ?",
					tableInfoResult.getLong(i, "table_id"));
			columnMergeResult.add(result);
		}
		collectionMap.put("columnMerge", columnMergeResult);
	}

	/**
	 * 将table_info表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放table_info表信息的集合
	 * 3.遍历database_set结果集获取database_set,通过database_set查询table_info表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将table_info表集合信息存入map
	 *
	 * @param collectionMap     java.util.Map
	 *                          含义：封装数据源下载信息（这里封装的是table_info表数据集合）
	 *                          取值范围：key唯一
	 * @param databaseSetResult fd.ng.db.resultset.Result
	 *                          含义：database_set表数据结果集
	 *                          取值范围：不为空
	 */
	private Result getTableInfoResult(Map<String, Object> collectionMap, Result databaseSetResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放table_info表信息的集合
		Result tableInfoResult = new Result();
		// 3.遍历database_set结果集获取database_set,通过database_set查询table_info表信息
		for (int i = 0; i < databaseSetResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from table_info where database_id = ?",
					databaseSetResult.getLong(i, "database_set"));
			tableInfoResult.add(result);
		}
		// 4.将查询到的信息封装入集合
		collectionMap.put("tableInfo", tableInfoResult);
		// 5.将table_info表集合信息存入map
		return tableInfoResult;
	}

	/**
	 * 将signal_file表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放signal_file表信息的集合
	 * 3.遍历database_set结果集获取database_id,通过ocs_id查询signal_file表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将signal_file表集合信息存入map
	 *
	 * @param collectionMap     java.util.Map
	 *                          含义：封装数据源下载信息（这里封装的是signal_file表数据集合）
	 *                          取值范围：key唯一
	 * @param databaseSetResult fd.ng.db.resultset.Result
	 *                          含义：database_set表数据结果集
	 *                          取值范围：不为空
	 */
	private void addSignalFileToMap(Map<String, Object> collectionMap, Result databaseSetResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放signal_file表信息的集合
		Result signalFileResult = new Result();
		// 3.遍历database_set结果集获取database_id,通过ocs_id查询signal_file表信息
		for (int i = 0; i < databaseSetResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from signal_file where database_id=?",
					databaseSetResult.getLong(i, "database_id"));
			// 4.将查询到的信息封装入集合
			signalFileResult.add(result);
		}
		// 5.将signal_file表集合信息存入map
		collectionMap.put("signalFile", signalFileResult);
	}

	/**
	 * 将file_source表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放file_source表信息的集合
	 * 3.遍历file_collect_set结果集获取fcs_id(文件系统采集ID),通过fcs_id查询file_source表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将file_source表集合信息存入map
	 *
	 * @param collectionMap          java.util.Map
	 *                               含义：封装数据源下载信息（这里封装的是file_source表数据集合）
	 *                               取值范围：key唯一
	 * @param file_collect_setResult fd.ng.db.resultset.Result
	 *                               含义：file_collect_set表数据结果集
	 *                               取值范围：不为空
	 */
	private void addFileSourceToMap(Map<String, Object> collectionMap, Result file_collect_setResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放file_source表信息的集合
		Result fileSourceResult = new Result();
		// 3.遍历file_collect_set结果集获取fcs_id(文件系统采集ID),通过fcs_id查询file_source表信息
		for (int i = 0; i < file_collect_setResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from file_source where fcs_id=?",
					file_collect_setResult.getLong(i, "fcs_id"));
			// 4.将查询到的信息封装入集合
			fileSourceResult.add(result);
		}
		// 5.将file_source表集合信息存入map
		collectionMap.put("fileSource", fileSourceResult);
	}

	/**
	 * 将file_collect_set表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放file_collect_set表信息的集合
	 * 3.遍历agent_info结果集获取agent_id,通过agent_id查询file_collect_set表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将file_collect_set表集合信息存入map
	 * 6.返回file_collect_set数据结果集
	 *
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是file_collect_set表数据集合）
	 *                      取值范围：key唯一
	 * @param agentInfoList java.util.List
	 *                      含义：agent_info表数据结果集合
	 *                      取值范围：不为空
	 */
	private Result getFileCollectSetResult(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放file_collect_set表信息的集合
		Result fileCollectSetResult = new Result();
		// 3.遍历agent_info结果集获取agent_id,通过agent_id查询file_collect_set表信息
		for (int i = 0; i < agentInfoList.size(); i++) {
			Result result = Dbo.queryResult("select * from file_collect_set where agent_id=?",
					agentInfoList.get(i).getAgent_id());
			// 4.将查询到的信息封装入集合
			fileCollectSetResult.add(result);
		}
		// 5.将file_collect_set表集合信息存入map
		collectionMap.put("fileCollectSet", fileCollectSetResult);
		// 6.返回file_collect_set数据结果集
		return fileCollectSetResult;
	}

	/**
	 * 将database_set表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放database_set表信息的集合
	 * 3.遍历agent_info结果集获取agent_id,通过agent_id查询database_set表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将database_set表集合信息存入map
	 * 6.返回database_result表数据结果集
	 *
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是database_set表数据集合）
	 *                      取值范围：key唯一
	 * @param agentInfoList java.util.List
	 *                      含义：agent_info表数据结果集合
	 *                      取值范围：不为空
	 */
	private Result getDatabaseSetResult(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放database_set表信息的集合
		Result databaseSetResult = new Result();
		// 3.遍历agent_info结果集获取agent_id,通过agent_id查询database_set表信息
		for (int i = 0; i < agentInfoList.size(); i++) {
			Result result = Dbo.queryResult("select * from database_set where agent_id = ?",
					agentInfoList.get(i).getAgent_id());
			// 4.将查询到的信息封装入集合
			databaseSetResult.add(result);
		}
		//5.将database_set表集合信息存入map
		collectionMap.put("databaseSet", databaseSetResult);
		// 6.返回database_result表数据结果集
		return databaseSetResult;
	}

	/**
	 * 将ftp_folder表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放ftp_folder表信息的集合
	 * 3.遍历ftp_collect结果集获取ftp_id(ftp采集任务编号),通过ftp_id查询ftp_folder表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将ftp_folder表集合信息存入map
	 *
	 * @param collectionMap    java.util.Map
	 *                         含义：封装数据源下载信息（这里封装的是ftp_folder表数据结果集）
	 *                         取值范围：key唯一
	 * @param ftpCollectResult fd.ng.db.resultset.Result
	 *                         含义：ftp_collect表数据结果集
	 *                         取值范围：不为空
	 */
	private void addFtpFolderToMap(Map<String, Object> collectionMap, Result ftpCollectResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放ftp_folder表信息的集合
		Result ftpFolderResult = new Result();
		// 3.遍历ftp_collect结果集获取ftp_id(ftp采集任务编号),通过ftp_id查询ftp_folder表信息
		for (int i = 0; i < ftpCollectResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from ftp_folder  where ftp_id = ?",
					ftpFolderResult.getLong(i, "ftp_id"));
			// 4.将查询到的信息封装入集合
			ftpFolderResult.add(result);
		}
		// 5.将ftp_folder表集合信息存入map
		collectionMap.put("ftpFolder", ftpFolderResult);
	}

	/**
	 * 将object_collect_struct表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放object_collect_struct表信息的集合
	 * 3.遍历object_collect_task结果集获取ocs_id(对象采集任务id),通过ocs_id查询object_collect_struct表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将object_collect_struct表集合信息存入map
	 *
	 * @param collectionMap           java.util.Map
	 *                                含义：封装数据源下载信息（这里封装的是object_collect_struct表数据集合）
	 *                                取值范围：key唯一
	 * @param objectCollectTaskResult fd.ng.db.resultset.Result
	 *                                含义：object_collect_task表数据结果集
	 *                                取值范围：不为空
	 */
	private void addObjectCollectStructResultToMap(Map<String, Object> collectionMap,
	                                               Result objectCollectTaskResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放object_collect_struct表信息的集合
		Result objectCollectStructResult = new Result();
		// 3.遍历object_collect_task结果集获取ocs_id,通过ocs_id查询object_collect_struct表信息
		for (int i = 0; i < objectCollectTaskResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from object_collect_struct where " +
					" ocs_id =?", objectCollectTaskResult.getLong(i, "ocs_id"));
			// 4.将查询到的信息封装入集合
			objectCollectStructResult.add(result);
		}
		// 5.将object_collect_struct表集合信息存入map
		collectionMap.put("objectCollectStruct", objectCollectStructResult);
	}

	/**
	 * 将object_storage表数据集合存入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放object_storage信息结果集
	 * 3.遍历object_collect_task结果集获取ocs_id（对象采集任务id），通过ocs_id查询object_storage表信息
	 * 4.将查询到的信息封装入集合
	 * 5.将object_storage集合信息存入map
	 *
	 * @param collectionMap           java.util.Map
	 *                                含义：封装数据源下载信息（这里封装的是object_storage表数据集合）
	 *                                取值范围：key唯一
	 * @param objectCollectTaskResult fd.ng.db.resultset.Result
	 *                                含义：object_collect_task表数据结果集
	 *                                取值范围：不为空
	 */
	private void addObjectStorageToMap(Map<String, Object> collectionMap, Result objectCollectTaskResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放object_storage信息的结果集
		Result objectStorageResult = new Result();
		// 3.遍历object_collect_task结果集获取ocs_id（对象采集任务编号），通过ocs_id查询object_storage表信息
		for (int i = 0; i < objectCollectTaskResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from object_storage where ocs_id =?",
					objectCollectTaskResult.getString(i, "ocs_id"));
			// 4.将查询到的信息封装入集合
			objectStorageResult.add(result);
		}
		// 5.将object_storage集合信息存入map
		collectionMap.put("objectStorage", objectStorageResult);
	}

	/**
	 * 封装object_collect_task表信息入map并返回object_collect_task表信息
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建存放object_collect_task表数据的结果集对象
	 * 3.循环遍历object_collect表数据获取odc_id，根据odc_id查询object_collect_task表信息并封装
	 * 4.将object_collect_task表结果集封装入map
	 * 5.返回object_collect_task表结果集
	 *
	 * @param collectionMap        java.util.Map
	 *                             含义：封装数据源下载信息（这里封装的是object_collect_task表数据集合）
	 *                             取值范围：key唯一
	 * @param object_collectResult fd.ng.db.resultset.Result
	 *                             含义：object_collect表数据结果集
	 *                             取值范围：不为空
	 * @return fd.ng.db.resultset.Result
	 * 含义：返回object_collect_task表数据集合信息
	 * 取值范围：不为空
	 */
	private Result getObjectCollectTaskResult(Map<String, Object> collectionMap, Result object_collectResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建存放object_collect_task表数据的结果集对象
		Result objectCollectTaskResult = new Result();
		// 3.循环遍历object_collect表数据获取odc_id，根据odc_id查询object_collect_task表信息并封装
		for (int i = 0; i < object_collectResult.getRowCount(); i++) {
			Result result = Dbo.queryResult("select * from object_collect_task where " +
					" odc_id=?", object_collectResult.getLong(i, "odc_id"));
			objectCollectTaskResult.add(result);
		}
		// 4.将object_collect_task表结果集封装入map
		collectionMap.put("objectCollectTask", objectCollectTaskResult);
		// 5.返回object_collect_task表结果集
		return objectCollectTaskResult;
	}

	/**
	 * 封装object_collect表信息入map并返回object_collect表信息
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建封装object_collect信息的结果集对象
	 * 3.循环遍历agent_info表信息集合获取agent_id（agent_info表主键，object_collect表外键）
	 * 4.根据agent_id查询object_collect表获取结果集并添加到结果集对象中
	 * 5.将object_collect结果集封装入map
	 * 6.返回object_collect结果集
	 *
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是object_collect表数据集合）
	 *                      取值范围：key唯一
	 * @param agentInfoList java.util.List
	 *                      含义：agent_info表数据集合
	 *                      取值范围：不为空
	 * @return fd.ng.db.resultset.Result
	 * 含义：返回object_collect表数据集合信息
	 * 取值范围：不为空
	 */
	private Result getObjectCollectResult(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建封装object_collect信息的结果集对象
		Result objectCollectResult = new Result();
		// 3.循环遍历agent_info表信息集合获取agent_id（agent_info表主键，object_collect表外键）
		for (int i = 0; i < agentInfoList.size(); i++) {
			// 4.根据agent_id查询object_collect表获取结果集并添加到结果集对象中
			Result object_collect = Dbo.queryResult("select * from object_collect where " +
					"agent_id=?", agentInfoList.get(i).getAgent_id());
			objectCollectResult.add(object_collect);
		}
		// 5.将object_collect结果集封装入map
		collectionMap.put("objectCollect", objectCollectResult);
		// 6.返回object_collect结果集
		return objectCollectResult;
	}

	/**
	 * 封装agent_down_info表数据集合到map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建封装agent_down_info表信息集合
	 * 3.遍历agent_info表信息获取agent_id（agent_info表主键，agent_down_info表外键）
	 * 4.通过agent_id查询agent_down_info表信息
	 * 5.将agent_down_info表信息放入list
	 * 6.将agent_down_info表信息入map
	 *
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是agent_down_info表数据集合）
	 *                      取值范围：key唯一
	 * @param agentInfoList java.util.List
	 *                      含义：agent_info表数据集合
	 *                      取值范围：不为空
	 */
	private void addAgentDownInfoToMap(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建封装agent_down_info表信息集合
		List<Optional<Agent_down_info>> agentDownInfoList =
				new ArrayList<>();
		// 3.遍历agent_info表信息获取agent_id（agent_info表主键，agent_down_info表外键）
		for (int i = 0; i < agentInfoList.size(); i++) {
			// 4.通过agent_id查询agent_down_info表信息
			Optional<Agent_down_info> agent_down_info = Dbo.queryOneObject(Agent_down_info.class,
					"select * from  agent_down_info where  agent_id = ?",
					agentInfoList.get(i).getAgent_id());
			// 5.将agent_down_info表信息放入list
			agentDownInfoList.add(agent_down_info);
		}
		// 6.将agent_down_info表信息入map
		collectionMap.put("agentDownInfo", agentDownInfoList);
	}

	/**
	 * 封装data_source表数据集合到map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.根据数据源ID查询数据源data_source集合
	 * 3.判断获取到的集合是否有数据，没有抛异常，有返回数据
	 * 4.将data_source数据入map
	 *
	 * @param sourceId      long
	 *                      含义：data_source主键ID
	 *                      取值范围：不为空以及空格，长度不超过10
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是data_source表数据集合）
	 *                      取值范围：key唯一
	 */
	private void addDataSourceToMap(long sourceId, Map<String, Object> collectionMap) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.根据数据源ID查询数据源data_source集合
		List<Data_source> dataSourceList = Dbo.queryList(Data_source.class, "select * from " +
				"data_source where  sourceId = ?", sourceId);
		// 3.判断获取到的集合是否有数据，没有抛异常，有返回数据
		if (dataSourceList.isEmpty()) {
			throw new BusinessException("此数据源下没有数据，sourceId = ?" + sourceId);
		}
		// 4.将data_source数据入map
		collectionMap.put("dataSource", dataSourceList);
	}

	/**
	 * 封装ftp_transfered表数据集合到map
	 *
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建封装ftp_transfered信息的集合
	 * 3.遍历Ftp_collect信息，获取ftp_id(ftp_transfered主键，ftp_collect外键）
	 * 4. 根据ftp_id查询ftp_transfered信息
	 * 5.将ftp_transfered表数据结果集信息入map
	 *
	 * @param collectionMap    java.util.Map
	 *                         含义：封装数据源下载信息（这里封装的是ftp_transfered表数据集合）
	 *                         取值范围：key唯一
	 * @param ftpCollectResult fd.ng.db.resultset.Result
	 *                         含义：ftp_collect表数据集
	 *                         取值范围：不为空
	 */
	private void addFtpTransferedToMap(Map<String, Object> collectionMap, Result ftpCollectResult) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建封装ftp_transfered信息的集合
		Result ftpTransferedResult = new Result();
		// 3.遍历Ftp_collect信息，获取ftp_id(ftp_transfered主键，ftp_collect外键）
		for (int i = 0; i < ftpCollectResult.getRowCount(); i++) {
			// 4. 根据ftp_id查询ftp_transfered信息
			Result result = Dbo.queryResult("select * from ftp_transfered where ftp_id=?",
					ftpCollectResult.getLong(i, "ftp_id"));
			ftpTransferedResult.add(result);
		}
		// 5.将ftp_transfered表数据结果集信息入map
		collectionMap.put("ftpTransfered", ftpTransferedResult);
	}

	/**
	 * 获取ftp_collect表信息
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建封装ftp_collect信息的集合
	 * 3.遍历agent_info信息，获取agent_id(agent_info主键，ftp_collect外键）
	 * 4. 根据agent_id查询ftp_collect信息
	 * 5.将ftp_collect表信息入map
	 * 6.返回ftp_collect表结果集信息
	 *
	 * @param agentInfoList java.util.List
	 *                      含义：agent_info表数据集合
	 *                      取值范围：不为空
	 * @return fd.ng.db.resultset.Result
	 * 含义：返回ftp_collect集合信息
	 * 取值范围：不为空
	 */
	private Result getFtpCollectResult(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建封装ftp_collect信息的集合
		Result ftpCollectResult = new Result();
		// 3.遍历agent_info信息，获取agent_id(agent_info主键，ftp_collect外键）
		for (int i = 0; i < agentInfoList.size(); i++) {
			// 4. 根据agent_id查询ftp_collect信息
			Result result = Dbo.queryResult("select * from  ftp_collect  where agent_id = ?",
					agentInfoList.get(i).getAgent_id());
			ftpCollectResult.add(result);
		}
		// 5.将ftp_collect表信息入map
		collectionMap.put("ftpCollect", ftpCollectResult);
		// 6.返回ftp_collect表结果集信息
		return ftpCollectResult;
	}

	/**
	 * 封装collect_job_classify表信息入map
	 * <p>
	 * 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
	 * 2.创建封装Collect_job_classify信息的集合
	 * 3.遍历agent_info信息，获取agent_id(agent_info主键，Collect_job_classify外键）
	 * 4. 根据agent_id查询Collect_job_classify信息
	 * 5.将collect_job_classify表数据结果集入map
	 *
	 * @param collectionMap java.util.Map
	 *                      含义：封装数据源下载信息（这里封装的是collect_job_classify表数据集合）
	 *                      取值范围：key唯一
	 * @param agentInfoList java.util.list
	 *                      含义：agent_info表信息集合
	 *                      取值范围：不为空
	 */
	private void addCollectJobClassifyToMap(Map<String, Object> collectionMap, List<Agent_info> agentInfoList) {
		// 1.数据可访问权限处理方式,这是私有方法，不会被单独调用，所以不需要权限验证
		// 2.创建封装collect_job_classify信息的集合
		Result collectJobClassifyResult = new Result();
		// 3.遍历agent_info信息，获取agent_id(agent_info主键，collect_job_classify外键）
		for (int i = 0; i < agentInfoList.size(); i++) {
			// 4.根据agent_id查询Collect_job_classify信息
			Result result = Dbo.queryResult("select * from collect_job_classify where " +
					" agent_id=?", agentInfoList.get(i).getAgent_id());
			collectJobClassifyResult.add(result);
		}
		// 5.将collect_job_classify表数据结果集入map
		collectionMap.put("collectJobClassify", collectJobClassifyResult);
	}

}
