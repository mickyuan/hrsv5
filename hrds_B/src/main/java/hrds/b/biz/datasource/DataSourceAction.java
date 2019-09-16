package hrds.b.biz.datasource;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.utils.CodecUtil;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.annotation.UploadFile;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.FileUploadUtil;
import fd.ng.web.util.ResponseUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.exception.ExceptionEnum;
import hrds.commons.utils.ActionUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 数据源增删改，导入、下载类
 *
 * @author mine
 * @date 2019-09-03 16:44:25
 */
public class DataSourceAction extends BaseAction {
	private static final Logger logger = LogManager.getLogger();

	/**
	 * 新增/编辑数据源
	 * <p>
	 * 1.字段合法性检查
	 * 2.判断数据源编号是否为空，为空则为新增，不为空则为编辑
	 * 3.新增前查询数据源编号是否已存在，存在则抛异常，不存在就新增
	 * 4.保存或更新数据源信息
	 * 5.如果是编辑先删除数据源与部门关系
	 * 6.保存或更新数据源与部门关系信息
	 *
	 * @param dataSource data_source表
	 *                   含义：data_source表实体类
	 *                   取值范围：与数据字段定义规则相同
	 * @param depIds     String
	 *                   含义：source_relation_dep表主键ID
	 *                   取值范围：可能是一个部门ID字符串， 也可能是通过分隔符拼接成的部门ID的字符串
	 */
	public void saveDataSource(@RequestBean Data_source dataSource, String depIds) {
		// 1.字段做合法性检查
		// 验证data_source_remark数据源名称合法性
		if (StringUtil.isBlank(dataSource.getDatasource_name())) {
			throw new BusinessException("数据源名称不能为空以及不能为空格，datasource_name=" + dataSource
					.getDatasource_name());
		}
		// 数据源编号长度
		int len = 4;
		// 验证数据源编号datasource_number合法性
		if (StringUtil.isBlank(dataSource.getDatasource_number())
				|| dataSource.getDatasource_number().length() > len) {
			throw new BusinessException("数据源编号不能为空以及不能为空格或数据源编号长度不能超过四位，" +
					"datasource_number=" + dataSource.getDatasource_number());
		}
		// 验证部门depIds合法性
		if (StringUtil.isBlank(depIds)) {
			throw new BusinessException("部门不能为空格，depIds=" + depIds);
		}
		// 2.判断数据源id(数据源data_source表主键ID)是否为空
		if (dataSource.getSource_id() == null) {
			// 新增,初始化一些非页面传值
			// 数据源主键ID
			dataSource.setSource_id(PrimayKeyGener.getNextId());
			// 数据源创建用户ID
			dataSource.setCreate_user_id(getUserId());
			// 数据源创建日期
			dataSource.setCreate_date(DateUtil.getSysDate());
			// 数据源创建时间
			dataSource.setCreate_time(DateUtil.getSysTime());
			// 3.新增前查询数据源编号是否已存在
			Result result = Dbo.queryResult("select datasource_number from " +
							Data_source.TableName + "  where datasource_number=?",
					dataSource.getDatasource_number());
			if (!result.isEmpty()) {
				// 数据源编号重复
				throw new BusinessException("数据源编号重复,datasource_number=" +
						dataSource.getDatasource_number());
			}
			// 4.保存数据源信息
			if (dataSource.add(Dbo.db()) != 1) {
				// 新增保存失败
				throw new BusinessException("新增保存数据源data_source表数据失败,datasource_number=" +
						dataSource.getDatasource_number());
			}
		} else {
			// 编辑
			// 字段合法性检查
			if (String.valueOf(dataSource.getSource_id()).length() > 10) {
				throw new BusinessException("source_id长度不能超过10，source_id="
						+ dataSource.getSource_id());
			}
			// 4.更新数据源信息
			if (dataSource.update(Dbo.db()) != 1) {
				// 编辑保存失败
				throw new BusinessException("编辑保存数据源data_source表数据失败,datasource_number=" +
						dataSource.getDatasource_number());
			}
			// 5.先删除数据源与部门关系信息
			int num = Dbo.execute("delete from " + Source_relation_dep.TableName +
					" where source_id=?", dataSource.getSource_id());
			if (num < 1) {
				throw new BusinessException("编辑时会先删除原数据源与部门关系信息，删除错旧关系时错误，" +
						"source_id=" + dataSource.getSource_id());
			}
		}
		// 6.保存或更新数据源与部门关系信息
		saveSourceRelationDep(dataSource.getSource_id(), depIds);
	}

	/**
	 * 保存数据源与部门关系表信息
	 * <p>
	 * 1.创建source_relation_dep对象,并设置数据源编号ID值
	 * 2.分隔部门depIds获取到存放所有部门ID的数组
	 * 3.循环遍历存放部门ID的数组，并向source_relation_dep对象设置部门ID值
	 * 4.循环保存source_relation_dep表信息
	 *
	 * @param source_id long
	 *                  含义：source_relation_dep表外键ID
	 *                  取值范围，不能为空以及不能为空格
	 * @param depIds    String
	 *                  含义：source_relation_dep表主键ID
	 *                  取值范围：可能是一个部门ID字符串， 也可能是通过分隔符拼接成的部门ID的字符串
	 */
	private void saveSourceRelationDep(long source_id, String depIds) {
		// 1.创建source_relation_dep对象,并初始化值
		Source_relation_dep srd = new Source_relation_dep();
		// 设置数据源与部门关系表外键ID
		srd.setSource_id(source_id);
		// 2.分隔部门depIds获取到存放所有部门ID的数组
		String[] split = depIds.split(",");
		// 3.循环遍历存放部门ID的数组
		for (String dep_id : split) {
			// 设置数据源与部门关系表主键ID
			srd.setDep_id(dep_id);
			// 4.循环保存source_relation_dep表信息
			if (srd.add(Dbo.db()) != 1) {
				throw new BusinessException("新增保存数据源与部门关系Source_relation_dep表信息失败，" +
						"dep_id=" + dep_id);
			}
		}
	}

	/**
	 * 根据数据源编号查询数据源及数据源与部门关系信息
	 *
	 * <p>
	 * 1.关联查询data_source表与source_relation_dep表信息
	 *
	 * @param source_id long
	 *                  含义：data_source表主键，source_relation_dep表外键
	 *                  取值范围：不能为空或空格
	 * @return 返回关联查询data_source表与source_relation_dep表信息结果
	 */
	public List<Map<String, Object>> searchDataSource(long source_id) {
		// 1.关联查询data_source表与source_relation_dep表信息
		return Dbo.queryList("select ds.*,srd.dep_id from data_source ds " +
				" join source_relation_dep srd on ds.source_id=srd.source_id " +
				"  where ds.source_id = ?", source_id);
	}

	/**
	 * 删除数据源信息
	 * <p>
	 * 1.先查询该datasource下是否还有agent,有不能删除，没有，可以删除
	 * 2.删除data_source表信息，删除失败就抛异常，否则正常删除
	 * 3.判断删除的数据是否不存在，不存在就抛异常
	 * 4.删除source_relation_dep信息
	 *
	 * @param source_id long
	 *                  含义：data_source表主键，agent_info表外键
	 *                  取值范围：不可为空以及不可为空格
	 */
	public void deleteDataSource(long source_id) {

		// 1.先查询该datasource下是否还有agent
		if (Dbo.queryNumber("SELECT * FROM agent_info  WHERE source_id=?", source_id)
				.orElse(-1) > 0) {
			throw new BusinessException("此数据源下还有agent，不能删除,source_id=" + source_id);
		}

		// 2.删除data_source表信息
		int num = Dbo.execute("delete from " + Data_source.TableName +
				" where source_id=?", source_id);
		if (num != 1) {
			// 3.判断库里是否没有这条数据
			if (num == 0) {
				throw new BusinessException("删除数据源信息表data_source失败，数据库里没有此条数据，" +
						"source_id=" + source_id);
			}
			throw new BusinessException("删除数据源信息表data_source失败，source_id=" + source_id);
		}
		// 4.删除source_relation_dep信息
		int srdNum = Dbo.execute("delete from " + Source_relation_dep.TableName +
				" where source_id=?", source_id);
		if (srdNum < 1) {
			throw new BusinessException("编辑时会先删除原数据源与部门关系信息，删除错旧关系时错误，" +
					"source_id=" + source_id);
		}
	}

	/**
	 * 上传文件(数据源下载文件提供的文件中涉及到的所有表的数据导入数据库中对应的表中）
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
	 * @param agent_ip   String
	 *                   含义：agent地址
	 *                   取值范围：不能为空，服务器ip地址
	 * @param agent_port String
	 *                   含义：agent端口
	 *                   取值范围：1024-65535
	 * @param user_id    long
	 *                   含义：数据采集用户
	 *                   取值范围：不为空以及空格，长度不超过10位
	 * @param file       String
	 *                   含义：上传文件名称（全路径）
	 *                   取值范围：不能为空以及空格
	 */
	@UploadFile
	public void uploadFile(String agent_ip, String agent_port, long user_id, String file) {
		try {
			// 1.通过文件名称获取文件
			File uploadedFile = FileUploadUtil.getUploadedFile(file);
			// 2.获取文件名
			String fileName = FileUploadUtil.getOriginalFileName(file);
			/*注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，
			如： c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt*/
			// 3.处理获取到的上传文件的文件名的路径部分，只保留文件名部分
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
			while ((len = bis.read(bytes)) != -1) {
				// 7.循环读取写入，将读取的字节转为字符串对象
				sb.append((new String(bytes, 0, len, CodecUtil.UTF8_CHARSET)));
			}
			// 8.关闭输入流
			bis.close();
			// 9.使用base64编码
			String strTemp = new String(Base64.getDecoder().decode(sb.toString()),
					CodecUtil.UTF8_CHARSET);
			// 10.导入数据源数据，将涉及到的所有表的数据导入数据库中对应的表中
			importDclData(strTemp, agent_ip, agent_port, user_id,
					ActionUtil.getUser().getUserId());
		} catch (Exception e) {
			throw new AppSystemException(e);
		}
	}

	/**
	 * 导入数据源数据，将涉及到的所有表的数据导入数据库中对应的表中(修改中,未完成）
	 *
	 * <p>
	 * 1.解析文件获取文件所有信息
	 * 2.遍历并解析拿到每张表的信息
	 * 3.将对应表信息插入库（数据源信息还需要判断数据源名称是否重复，重复抛异常，否则正常入库）
	 *
	 * @param strTemp         String
	 *                        含义：涉及数据源文件下载相关的所有表进行base64编码后的信息
	 * @param agent_ip        String
	 *                        含义：agent地址
	 *                        取值范围：不能为空，服务器ip地址
	 * @param agent_port      String
	 *                        含义：agent端口
	 *                        取值范围：1024-65535
	 * @param user_id         long
	 *                        含义：数据采集用户ID
	 *                        取值范围：不为空以及空格，长度不超过10位
	 * @param user_collect_id long
	 *                        含义：创建用户id
	 *                        取值范围：不为空以及不为空格，长度不超过10位
	 */
	private void importDclData(String strTemp, String agent_ip, String agent_port, long
			user_id, long user_collect_id) {
		// 1.获取文件所有信息
		Type type = new TypeReference<Map<String, Object>>() {
		}.getType();
		Map<String, Object> map = JsonUtil.toObject(strTemp, type);
		// 2.遍历并解析拿到每张表的信息，map里存放的是所有数据源相关的表信息
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			// 数据源信息
			if ("data_source".equals(entry.getKey())) {
				// 获取数据源信息
				Data_source data_source = JsonUtil.toObject(entry.getValue().toString(),
						Data_source.class);
				// 判断上传文件的数据源名称和已有的名称是否重复
				if (Dbo.queryNumber("select * from data_source where datasource_name = ?",
						data_source.getDatasource_name()).orElse(-1) > 0) {
					throw new BusinessException("数据源名称重复,datasource_name=" +
							data_source.getDatasource_name());
				}
				//数据源data_source
				data_source.setSource_id(PrimayKeyGener.getNextId());
				data_source.setCreate_user_id(user_collect_id);
				// 3.入库
				if (data_source.add(Dbo.db()) != 1) {
					throw new BusinessException("保存data_source表信息失败");
				}
				//数据源和部门关系表source_relation_dep
				Result diResult = Dbo.queryResult("select dep_id from department_info where dep_name" +
						" = '第一部门'");
				//FIXME 为什么写死了？
				//FIXME 为什么不做查询结果存在性判断
				//FIXME 需要对数据所属部门进行判断，并正确插入
				String dep_id = diResult.getString(0, "dep_id");
				Source_relation_dep source_relation_dep = new Source_relation_dep();
				source_relation_dep.setDep_id(dep_id);
				source_relation_dep.setSource_id(data_source.getSource_id());
				// 3.入库
				if (1 != source_relation_dep.add(Dbo.db())) {
					throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
				}
			}
			//Agent信息表agent_info
			if ("agent_info".equals(entry.getKey())) {
				// 获取agent信息表信息
				//FIXME idea的提示要解决
				List<Agent_info> agent_info = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				// 3.循环入库agent_info
				for (Agent_info agent : agent_info) {
					agent.setUser_id(user_id);
					agent.setAgent_ip(agent_ip);
					agent.setAgent_port(agent_port);
					if (1 != agent.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//Agent下载信息表Agent_down_info
			if ("agent_down_info".equals(entry.getKey())) {
				// 获取Agent下载信息表信息
				List<Agent_down_info> agent_down_info = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				// 3.循环入库agent_down_info
				for (Agent_down_info down_info : agent_down_info) {
					down_info.setUser_id(user_id);
					down_info.setAgent_ip(agent_ip);
					down_info.setAgent_port(agent_port);
					if (down_info.add(Dbo.db()) != 1) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			if ("collect_job_classify".equals(entry.getKey())) {
				// 获取采集任务分类表collect_job_classify信息
				List<Collect_job_classify> collect_job_classify = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库collect_job_classify
				for (Collect_job_classify classify : collect_job_classify) {
					classify.setUser_id(user_id);
					if (classify.add(Dbo.db()) != 1) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//ftp采集设置ftp_collect
			if ("ftp_collect".equals(entry.getKey())) {
				// 获取tp采集设置ftp_collect信息
				List<Ftp_collect> ftp_collect = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库ftp_collect
				for (Ftp_collect collect : ftp_collect) {
					if (collect.add(Dbo.db()) != 1) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//ftp已传输表ftp_transfered
			if ("ftp_transfered".equals(entry.getKey())) {
				// 获取ftp已传输表ftp_transfered信息
				List<Ftp_transfered> ftp_transfered = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库ftp_transfered
				for (Ftp_transfered transfered : ftp_transfered) {
					if (transfered.add(Dbo.db()) != 1) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//ftp目录表ftp_folder
			if ("ftp_folder".equals(entry.getKey())) {
				// 获取ftp目录表ftp_folder信息
				List<Ftp_folder> ftp_folder = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库ftp_folder
				for (Ftp_folder folder : ftp_folder) {
					if (folder.add(Dbo.db()) != 1) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//对象采集设置object_collect
			if ("object_collect".equals(entry.getKey())) {
				// 获取对象采集设置object_collect信息
				List<Object_collect> object_collect = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库object_collect
				for (Object_collect collect : object_collect) {
					if (1 != collect.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//对象采集对应信息object_collect_task
			if ("object_collect_task".equals(entry.getKey())) {
				// 获取对象采集对应信息object_collect_task信息
				List<Object_collect_task> object_collect_task = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库object_collect_task
				for (Object_collect_task collect_task : object_collect_task) {
					if (1 != collect_task.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//对象采集存储设置object_storage
			if ("object_storage".equals(entry.getKey())) {
				//获取对象采集存储设置object_storage信息
				List<Object_storage> object_storage = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库object_storage
				for (Object_storage storage : object_storage) {
					if (1 != storage.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//对象采集结构信息object_collect_struct
			if ("object_collect_struct".equals(entry.getKey())) {
				//获取对象采集结构信息object_collect_struct信息
				List<Object_collect_struct> object_collect_struct = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库object_collect_struct
				for (Object_collect_struct collect_struct : object_collect_struct) {
					if (1 != collect_struct.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//数据库设置database_set
			if ("database_set".equals(entry.getKey())) {
				//获取数据库设置database_set信息
				List<Database_set> database_set = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库database_set
				for (Database_set set : database_set) {
					if (1 != set.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//文件系统设置file_collect_set
			if ("file_collect_set".equals(entry.getKey())) {
				//获取文件系统设置file_collect_set信息
				List<File_collect_set> file_collect_set = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库file_collect_set
				for (File_collect_set collect_set : file_collect_set) {
					if (1 != collect_set.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//文件源设置file_source
			if ("file_source".equals(entry.getKey())) {
				// 获取文件源设置file_source信息
				List<File_source> file_source = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库file_source
				for (File_source source : file_source) {
					if (1 != source.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			// 信号文件入库信息signal_file
			if ("signal_file".equals(entry.getKey())) {
				//获取信号文件入库信息signal_file信息
				List<Signal_file> signal_file = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库signal_file
				for (Signal_file file : signal_file) {
					if (1 != file.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//数据库对应的表table_info
			if ("table_info".equals(entry.getKey())) {
				//获取数据库对应的表table_info信息
				List<Table_info> table_info = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库table_info
				for (Table_info info : table_info) {
					if (1 != info.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//列合并信息column_merge
			if ("column_merge".equals(entry.getKey())) {
				//获取列合并信息column_merge信息
				List<Column_merge> column_merge = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库column_merge
				for (Column_merge merge : column_merge) {
					if (1 != merge.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//表存储信息table_storage_info
			if ("table_storage_info".equals(entry.getKey())) {
				//获取表存储信息table_storage_info信息
				List<Table_storage_info> table_storage_info = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库table_storage_info
				for (Table_storage_info storage_info : table_storage_info) {
					if (1 != storage_info.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//表清洗参数信息table_clean
			if ("table_clean".equals(entry.getKey())) {
				// 获取表清洗参数信息table_clean信息
				List<Table_clean> table_clean = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库table_clean
				for (Table_clean clean : table_clean) {
					if (1 != clean.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			//表对应的字段table_column
			if ("table_column".equals(entry.getKey())) {
				// 获取表对应的字段table_column信息
				List<Table_column> table_column = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				//3.循环入库table_column
				for (Table_column column : table_column) {
					if (1 != column.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			// 列清洗参数信息column_clean
			if ("column_clean".equals(entry.getKey())) {
				// 获取列清洗参数信息column_clean信息
				List<Column_clean> column_clean = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				// 3.循环入库column_clean
				for (Column_clean clean : column_clean) {
					if (1 != clean.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
			// 列拆分信息表column_split
			if ("column_split".equals(entry.getKey())) {
				// 获取列拆分信息表column_split信息
				List<Column_split> column_split = JsonUtil.toObject(entry.getValue().toString(),
						List.class);
				// 3.循环入库column_split
				for (Column_split split : column_split) {
					if (1 != split.add(Dbo.db())) {
						throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
					}
				}
			}
		}
	}

	/**
	 * 下载文件（数据源下载功能使用，下载数据源给数据源导入提供上传文件）,暂时未完成，修改中
	 *
	 * <p>
	 * 1.创建封装数据库查询数据的map集合
	 * 2.获取data_source表信息集合，将data_source表信息封装入map
	 * 3.获取agent_info表信息集合，将agent_info表信息封装入map
	 * 4.获取Agent_down_info表信息集合，将agent_down_info表信息封装入map
	 * 3.通过base64将map转string进行编码
	 * 4.通过流的方式写文件
	 *
	 * @param source_id long
	 *                  含义：data_source表主键
	 *                  取值范围：不为空以及不为空格，长度不超过10
	 */
	public void downloadFile(long source_id) {
		try {
			// 1.创建封装数据库查询数据的map集合
			Map<String, Object> collection_map = new HashMap<String, Object>();
			// 2.获取data_source表信息集合，将data_source表信息封装入map
			List<Data_source> dataSourceList = getData_sources(source_id);
			collection_map.put("data_source", dataSourceList);

			// 3.获取agent_info表信息集合，将agent_info表信息封装入map
			List<Agent_info> agentInfoList = Dbo.queryList(Agent_info.class, "select * from " +
					"agent_info  where source_id = ?", source_id);
			collection_map.put("agent_info", agentInfoList);

			// 4.获取Agent_down_info表信息集合，将agent_down_info表信息封装入map
			List<Optional<Agent_down_info>> agentDownInfoList = getAgentDownInfoList(agentInfoList);
			collection_map.put("agent_down_info", agentDownInfoList);

			// 5.采集任务分类表collect_job_classify
			List<List<Collect_job_classify>> cjcList = getCollectJobClassifyList(agentInfoList);
			collection_map.put("collect_job_classify", cjcList);

			//6.ftp采集设置ftp_collect
			Result ftp_collectResult = new Result();
			for (int i = 0; i < agentInfoList.size(); i++) {
				Result result = Dbo.queryResult("select * from ftp_collect where agent_id = ?",
						agentInfoList.get(i).getAgent_id());
				ftp_collectResult.add(result);
			}
			collection_map.put("ftp_collect", ftp_collectResult);
			//ftp已传输表ftp_transfered
			Result ftp_transferedResult = new Result();
			for (int i = 0; i < ftp_collectResult.getRowCount(); i++) {
				Result result = Dbo.queryResult("select * from ftp_transfered where ftp_id = ?",
						ftp_collectResult.getLong(i, "ftp_id"));
				ftp_transferedResult.add(result);
			}
			collection_map.put("ftp_transfered", ftp_transferedResult);
			//ftp目录表ftp_folder
			Result ftp_folderResult = new Result();
			for (int i = 0; i < ftp_collectResult.getRowCount(); i++) {
				Result result = Dbo.queryResult("select * from ftp_folder where ftp_id = ?",
						ftp_collectResult.getLong(i, "ftp_id"));
				ftp_folderResult.add(result);
			}
			collection_map.put("ftp_folder", ftp_folderResult);
			//对象采集设置object_collect
			Result object_collectResult = new Result();
			for (int i = 0; i < agentInfoList.size(); i++) {
				Result result = Dbo.queryResult("select * from object_collect where agent_id = ?",
						agentInfoList.get(i).getAgent_id());
				object_collectResult.add(result);
			}
			collection_map.put("object_collect", object_collectResult);
			//对象采集对应信息object_collect_task
			Result object_collect_taskResult = new Result();
			for (int i = 0; i < object_collectResult.getRowCount(); i++) {
				Result result = Dbo.queryResult("select * from object_collect_task where odc_id = ?",
						object_collectResult.getLong(i, "odc_id"));
				object_collect_taskResult.add(result);
			}
			collection_map.put("object_collect_task", object_collect_taskResult);
			//对象采集存储设置object_storage
			Result object_storageResult = new Result();
			for (int i = 0; i < object_collect_taskResult.getRowCount(); i++) {
				Result result = Dbo.queryResult("select * from object_storage where ocs_id = ?",
						object_collect_taskResult.getString(i, "ocs_id"));
				object_storageResult.add(result);
			}
			collection_map.put("object_storage", object_storageResult);
			//对象采集结构信息object_collect_struct
			Result object_collect_structResult = new Result();
			for (int i = 0; i < object_collect_taskResult.getRowCount(); i++) {
				Result result = Dbo.queryResult("select * from object_collect_struct where ocs_id = ?",
						object_collect_taskResult.getLong(i, "ocs_id"));
				object_collect_structResult.add(result);
			}
			collection_map.put("object_collect_struct", object_collect_structResult);
			//数据库设置database_set
			Result database_setResult = new Result();
			for (int i = 0; i < agentInfoList.size(); i++) {
				Result result = Dbo.queryResult("select * from database_set where agent_id = ?",
						agentInfoList.get(i).getAgent_id());
				database_setResult.add(result);
			}
			collection_map.put("database_set", database_setResult);
			//文件系统设置file_collect_set
			Result file_collect_setResult = new Result();
			for (int i = 0; i < agentInfoList.size(); i++) {
				Result result = Dbo.queryResult("select * from file_collect_set where agent_id = ?",
						agentInfoList.get(i).getAgent_id());
				file_collect_setResult.add(result);
			}
			collection_map.put("file_collect_set", file_collect_setResult);
			//文件源设置file_source
			Result file_sourceResult = new Result();
			for (int i = 0; i < file_collect_setResult.getRowCount(); i++) {
				Result result = Dbo.queryResult("select * from file_source where fcs_id = ?",
						file_collect_setResult.getLong(i, "fcs_id"));
				file_sourceResult.add(result);
			}
			collection_map.put("file_source", file_sourceResult);

			//信号文件入库信息signal_file
			Result signal_fileResult = new Result();
			for (int i = 0; i < database_setResult.getRowCount(); i++) {
				Result result = Dbo.queryResult("select * from signal_file where database_id = ?",
						database_setResult.getLong(i, "database_id"));
				signal_fileResult.add(result);
			}
			collection_map.put("signal_file", signal_fileResult);

			//数据库对应的表table_info
			Result table_infoResult = new Result();
			for (int i = 0; i < database_setResult.getRowCount(); i++) {
				Result result = Dbo.queryResult("select * from table_info where database_id = ?",
						database_setResult.getLong(i, "database_id"));
				table_infoResult.add(result);
			}
			collection_map.put("table_info", table_infoResult);

			//列合并信息表column_merge
			Result column_mergeResult = new Result();
			for (int i = 0; i < table_infoResult.getRowCount(); i++) {
				Result result = Dbo.queryResult("select * from column_merge where table_id = ?",
						table_infoResult.getLong(i, "table_id"));
				column_mergeResult.add(result);
			}
			collection_map.put("column_merge", column_mergeResult);

			//表存储信息table_storage_info
			Result table_storage_infoResult = new Result();
			for (int i = 0; i < table_infoResult.getRowCount(); i++) {
				Result result = Dbo.queryResult("select * from table_storage_info where table_id = ?",
						table_infoResult.getLong(i, "table_id"));
				table_storage_infoResult.add(result);
			}
			collection_map.put("table_storage_info", table_storage_infoResult);

			//表清洗参数信息table_clean
			Result table_cleanResult = new Result();
			for (int i = 0; i < table_infoResult.getRowCount(); i++) {
				Result result = Dbo.queryResult("select * from table_clean where table_id = ?",
						table_infoResult.getLong(i, "table_id"));
				table_cleanResult.add(result);
			}
			collection_map.put("table_clean", table_cleanResult);

			//表对应的字段table_column
			Result table_columnResult = new Result();
			for (int i = 0; i < table_infoResult.getRowCount(); i++) {
				Result result = Dbo.queryResult("select * from table_column where table_id = ?",
						table_infoResult.getLong(i, "table_id"));
				table_columnResult.add(result);
			}
			collection_map.put("table_column", table_columnResult);

			//列清洗参数信息 column_clean
			Result column_cleanResult = new Result();
			for (int i = 0; i < table_columnResult.getRowCount(); i++) {
				Result result = Dbo.queryResult("select * from column_clean where column_id = ?",
						table_columnResult.getLong(i, "column_id"));
				column_cleanResult.add(result);
			}
			collection_map.put("column_clean", column_cleanResult);

			//列拆分信息表column_split
			Result column_splitResult = new Result();
			for (int i = 0; i < table_columnResult.getRowCount(); i++) {
				Result result = Dbo.queryResult("select * from column_split where column_id = ?",
						table_columnResult.getLong(i, "column_id"));
				column_splitResult.add(result);
			}
			collection_map.put("column_split", column_splitResult);

			// 2.使用base64编码
			byte[] bytes = Base64.getEncoder().encode(JsonUtil.toJson(collection_map).getBytes(CodecUtil.UTF8_CHARSET));
			// 判断文件是否存在
			if (bytes == null) {
				throw new BusinessException("此文件不存在");
			}
			// 通过流的方式写入文件
			HttpServletResponse response = ResponseUtil.getResponse();
			// 3.清空response
			response.reset();

			// 设置响应编码格式
			response.setCharacterEncoding(CodecUtil.UTF8_STRING);

			// 设置响应头，控制浏览器下载该文件
			response.setContentType("APPLICATION/OCTET-STREAM");

			// 创建输出流
			OutputStream out = response.getOutputStream();
			out.write(bytes);
			out.flush();
			out.close();
		} catch (IOException e) {
			throw new AppSystemException(e);
		}
	}

	/**
	 * 获取agent_down_info表信息
	 * <p>
	 * 1.创建封装agent_down_info信息的集合
	 * 2.遍历agent_info信息，获取agent_id(agent_info主键，agent_down_info外键）
	 * 3. 根据agent_id查询agent_down_info信息
	 * 4. 返回agent_down_info集合信息
	 *
	 * @param agentInfoList list
	 *                      含义：agent_info表信息集合
	 *                      取值范围：不为空
	 * @return 返回agent_down_info集合信息
	 */
	private List<List<Collect_job_classify>> getCollectJobClassifyList(List<Agent_info> agentInfoList) {
		// 1.创建封装agent_down_info信息的集合
		List<List<Collect_job_classify>> cjcList = new ArrayList<>();
		// 2.遍历agent_info信息，获取agent_id(agent_info主键，agent_down_info外键）
		for (int i = 0; i < agentInfoList.size(); i++) {
			// 3. 根据agent_id查询agent_down_info信息
			List<Collect_job_classify> jobClassifyList = Dbo.queryList(Collect_job_classify.class,
					"select * from  collect_job_classify where  agent_id = ?",
					agentInfoList.get(i).getAgent_id());
			cjcList.add(jobClassifyList);
		}
		// 4. 返回agent_down_info集合信息
		return cjcList;
	}

	/**
	 * 根据agent_info表信息集合获取agent_down_info表信息集合
	 * <p>
	 * 1.创建存放agent_down_info表信息集合
	 * 2.遍历agent_info表信息获取agent_id（agent_info表主键，agent_down_info表外键）
	 * 3.通过agent_id查询agent_down_info表信息
	 * 4.将agent_down_info表信息放入list
	 * 5.agent_down_info表信息集合
	 *
	 * @param agentInfoList List<Agent_info>
	 *                      含义：agent_info表信息集合
	 *                      取值范围：不为空
	 * @return 返回agent_down_info表信息集合
	 */
	private List<Optional<Agent_down_info>> getAgentDownInfoList(List<Agent_info> agentInfoList) {
		// 1.创建存放agent_down_info表信息集合
		List<Optional<Agent_down_info>> agentDownInfoList =
				new ArrayList<Optional<Agent_down_info>>();
		// 2.遍历agent_info表信息获取agent_id（agent_info表主键，agent_down_info表外键）
		for (int i = 0; i < agentInfoList.size(); i++) {
			// 3.通过agent_id查询agent_down_info表信息
			Optional<Agent_down_info> agent_down_info = Dbo.queryOneObject(Agent_down_info.class,
					"select * from  agent_down_info where  agent_id = ?",
					agentInfoList.get(i).getAgent_id());
			// 4.将agent_down_info表信息放入list
			agentDownInfoList.add(agent_down_info);
		}
		// 5.agent_down_info表信息集合
		return agentDownInfoList;
	}

	/**
	 * 根据数据源ID获取数据源集合
	 * <p>
	 * 1.根据数据源ID查询数据源data_source集合
	 * 2.判断获取到的集合是否有数据，没有抛异常，有返回数据
	 *
	 * @param source_id long
	 *                  含义：data_source表主键
	 *                  取值范围：不能为空以及空格，长度不超过10
	 * @return 返回根据数据源ID查询数据源data_source集合结果信息
	 */
	private List<Data_source> getData_sources(long source_id) {
		// 1.根据数据源ID查询数据源data_source集合
		List<Data_source> dataSourceList = Dbo.queryList(Data_source.class, "select * from " +
				"data_source where  source_id = ?", source_id);
		// 2.判断获取到的集合是否有数据，没有抛异常，有返回数据
		if (dataSourceList.size() == 0) {
			throw new BusinessException("此数据源下没有数据，source_id = ?" + source_id);
		}
		return dataSourceList;
	}

}
