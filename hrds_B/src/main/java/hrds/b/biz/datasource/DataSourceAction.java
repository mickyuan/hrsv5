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
import javax.ws.rs.GET;
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
		if (dataSource.add(Dbo.db()) != 1) {
			throw new BusinessException("新增保存数据源data_source表数据失败,datasource_number=" +
					dataSource.getDatasource_number());
		}
		// 5.保存source_relation_dep表信息
		saveSourceRelationDep(dataSource.getSource_id(), depIds);
	}

	/**
	 * 更新数据源信息
	 * <p>
	 * 1.数据可访问权限处理方式，通过source_id与user_id关联检查
	 * 2.验证source_id是否合法
	 * 3.字段合法性检查
	 * 4.将data_source实体数据封装
	 * 5.更新数据源信息
	 * 6.先删除数据源与部门关系信息
	 * 7.保存source_relation_dep表信息
	 *
	 * @param source_id         long
	 *                          含义：data_source表主键，source_relation_dep表外键
	 *                          取值范围：不为空，10位数字
	 * @param source_remark     String
	 *                          含义：备注
	 *                          取值范围：没有限制
	 * @param datasource_name   String
	 *                          含义：数据源名称
	 *                          取值范围：不为空且不为空格
	 * @param datasource_number String
	 *                          含义：数据源编号
	 *                          取值范围：不为空且不为空格，长度不超过四位
	 * @param depIds            Long[]
	 *                          含义：存储source_relation_dep表主键ID的数组，定义为Long是为了
	 *                          判断是否为null
	 *                          取值范围：不为空以及不为空格
	 */
	public void updateDataSource(Long source_id, String source_remark, String datasource_name,
	                             String datasource_number, Long[] depIds) {
		// 1.数据可访问权限处理方式，通过source_id与user_id关联检查
		if (Dbo.queryNumber("select count(1) from data_source where source_id=? and " +
				" create_user_id=?", source_id, getUserId()).orElse(Long.MIN_VALUE) == 0) {
			throw new BusinessException("数据权限校验失败，数据不可访问！");
		}
		//source_id长度
		// 2.验证source_id是否合法
		if (source_id == null) {
			throw new BusinessException("source_id不为空以及不为空格，新增时自动生成");
		}
		// 3.字段合法性检查
		fieldLegalityValidation(datasource_name, datasource_number, depIds);
		// 4.将data_source实体数据封装
		Data_source dataSource = new Data_source();
		dataSource.setSource_id(source_id);
		dataSource.setDatasource_name(datasource_name);
		dataSource.setDatasource_number(datasource_number);
		dataSource.setSource_remark(source_remark);
		// 5.更新数据源信息
		if (dataSource.update(Dbo.db()) != 1) {
			throw new BusinessException("更新保存data_source表数据失败,source_id=" +
					source_id + ",datasource_name=" + datasource_name);
		}
		// 6.先删除数据源与部门关系信息
		int num = Dbo.execute("delete from source_relation_dep where source_id=?",
				dataSource.getSource_id());
		if (num < 1) {
			throw new BusinessException("编辑时会先删除原数据源与部门关系信息，删除错旧关系时错误，" +
					"source_id=" + dataSource.getSource_id());
		}
		// 7.保存source_relation_dep表信息
		saveSourceRelationDep(source_id, depIds);
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
	 * @param datasource_name   String
	 *                          含义：数据源名称
	 *                          取值范围：不为空及空格
	 * @param datasource_number String
	 *                          含义：数据源编号
	 *                          取值范围：不为空以及不为空格，长度不超过4
	 * @param depIds            Long[]
	 *                          含义：存储source_relation_dep主键ID(dep_id)的数组,定义为Long是为了
	 *                          判断是否为null
	 *                          取值范围：10为数字
	 */
	private void fieldLegalityValidation(String datasource_name, String datasource_number,
	                                     Long[] depIds) {
		// 1.数据可访问权限处理方式，通过create_user_id检查
		// 2.循环遍历获取source_relation_dep主键ID，验证dep_id合法性
		for (Long dep_id : depIds) {
			if (dep_id == null) {
				throw new BusinessException("部门不能为空或者空格，新增部门时通过主键生成，" +
						"dep_id=" + dep_id);
			}
		}
		// 3.验证datasource_name是否合法
		if (StringUtil.isBlank(datasource_name)) {
			throw new BusinessException("数据源名称不能为空以及不能为空格，datasource_name="
					+ datasource_name);
		}
		// 数据源编号长度
		int len = 4;
		// 4.datasource_number是否合法
		if (StringUtil.isBlank(datasource_number)
				|| datasource_number.length() > len) {
			throw new BusinessException("数据源编号不能为空以及不能为空格或数据源编号长度不能超过四位，" +
					"datasource_number=" + datasource_number);
		}
		// 5.更新前查询数据源编号是否已存在
		if (Dbo.queryNumber("select count(1) from " + Data_source.TableName + "  where " +
						"datasource_number=? and create_user_id=?", datasource_number
				, getUserId()).orElse(Long.MIN_VALUE) > 0) {
			// 判断数据源编号是否重复
			throw new BusinessException("数据源编号重复,datasource_number=" +
					datasource_number);
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
	 * @param source_id long
	 *                  含义：source_relation_dep表外键ID
	 *                  取值范围，不能为空以及不能为空格
	 * @param depIds    Long[]
	 *                  含义：存放source_relation_dep表主键ID的数组
	 *                  取值范围：不为空
	 */
	private void saveSourceRelationDep(long source_id, Long[] depIds) {
		// 1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以这里不需要做权限验证
		// 2.创建source_relation_dep对象
		Source_relation_dep srd = new Source_relation_dep();
		// 2.1设置source_relation_dep表外键ID
		srd.setSource_id(source_id);
		// 3.分隔部门depIds获取到封装所有部门ID的数组
		// 4.循环遍历存储部门ID的数组
		for (long dep_id : depIds) {
			// 4.1设置source_relation_dep表主键ID
			srd.setDep_id(dep_id);
			// 5.循环保存source_relation_dep表信息
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
	 * 1.数据可访问权限处理方式，以下SQL关联source_id与user_id检查
	 * 2.关联查询data_source表与source_relation_dep表信息
	 *
	 * @param source_id long
	 *                  含义：data_source表主键，source_relation_dep表外键
	 *                  取值范围：不能为空或空格
	 * @return 返回关联查询data_source表与source_relation_dep表信息结果
	 */
	public List<Map<String, Object>> searchDataSource(long source_id) {
		// 1.数据可访问权限处理方式，以下SQL关联source_id与user_id检查
		// 2.关联查询data_source表与source_relation_dep表信息
		return Dbo.queryList("select ds.*,srd.dep_id from data_source ds " +
				" join source_relation_dep srd on ds.source_id=srd.source_id " +
				"  where ds.source_id = ? and ds.create_user_id=?", source_id, getUserId());
	}

	/**
	 * 删除数据源信息
	 * <p>
	 * 1.数据可访问权限处理方式，以下SQL关联source_id与user_id检查
	 * 2.先查询该datasource下是否还有agent,有不能删除，没有，可以删除
	 * 3.删除data_source表信息，删除失败就抛异常，否则正常删除
	 * 4.判断删除的数据是否不存在，不存在就抛异常
	 * 5.删除source_relation_dep信息
	 *
	 * @param source_id long
	 *                  含义：data_source表主键，agent_info表外键
	 *                  取值范围：不可为空以及不可为空格
	 */
	public void deleteDataSource(long source_id) {

		// 1.数据可访问权限处理方式，以下SQL关联source_id与user_id检查
		// 2.先查询该datasource下是否还有agent
		// 获取登录用户ID
		if (Dbo.queryNumber("SELECT count(1) FROM agent_info  WHERE source_id=?" +
				" and user_id=?", source_id, getUserId()).orElse(Long.MIN_VALUE) > 0) {
			throw new BusinessException("此数据源下还有agent，不能删除,source_id=" + source_id);
		}
		// 3.删除data_source表信息
		int num = Dbo.execute("delete from data_source where source_id=? " +
				" and create_user_id=?", source_id, getUserId());
		if (num != 1) {
			// 4.判断库里是否没有这条数据
			if (num == 0) {
				throw new BusinessException("删除数据源信息表data_source失败，数据库里没有此条数据，" +
						"source_id=" + source_id);
			}
			throw new BusinessException("删除数据源信息表data_source失败，source_id=" + source_id);
		}
		// 5.删除source_relation_dep信息
		int srdNum = Dbo.execute("delete from source_relation_dep where source_id=?", source_id);
		if (srdNum < 1) {
			if (srdNum == 0) {
				throw new BusinessException("删除source_relation_dep失败，数据库里没有此条数据，"
						+ "source_id=" + source_id);
			}
			throw new BusinessException("删除该数据源下source_relation_dep表数据错误，source_id="
					+ source_id);
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
	 *                   含义：数据采集用户ID,用户类型
	 *                   取值范围：参考UserType代码项
	 * @param file       String
	 *                   含义：上传文件名称（全路径）
	 *                   取值范围：不能为空以及空格
	 */
	@UploadFile
	public void uploadFile(String agent_ip, String agent_port, long user_id, String file) {
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
			// FIXME user_id与ActionUtil.getUser().getUserId()什么区别，为什么要两个不同的用户
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
	 *                        含义：数据采集用户ID,用户类型
	 *                        取值范围：参考UserType代码项
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
		// 2.遍历并解析拿到每张表的信息，map里封装的是所有数据源相关的表信息
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			// 数据源信息
			if ("data_source".equals(entry.getKey())) {
				// 获取数据源信息
				Data_source data_source = JsonUtil.toObject(entry.getValue().toString(),
						Data_source.class);
				// 判断上传文件的数据源名称和已有的名称是否重复
				if (Dbo.queryNumber("select count(1) from data_source where datasource_name = ?",
						data_source.getDatasource_name()).orElse(Long.MIN_VALUE) > 0) {
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
			Map<String, Object> collection_map = new HashMap<>();
			// 2.获取data_source表信息集合，将data_source表信息封装入map
			addDataSourceToMap(source_id, collection_map);
			// 3.获取agent_info表信息集合，将agent_info表信息封装入map
			List<Agent_info> agentInfoList = Dbo.queryList(Agent_info.class, "select * from " +
					"agent_info  where source_id = ?", source_id);
			collection_map.put("agent_info", agentInfoList);
			// 4.获取Agent_down_info表信息集合封装入map
			addAgentDownInfoToMap(collection_map, agentInfoList);
			// 5.采集任务分类表collect_job_classify，获取collect_job_classify表信息集合入map
			addCollectJobClassifyToMap(collection_map, agentInfoList);
			// 6.ftp采集设置ftp_collect,获取ftp_collect表信息集合
			Result ftpCollectResult = getFtpCollectResult(collection_map, agentInfoList);
			// 7.ftp已传输表ftp_transfered,获取ftp_transfered表信息集合入map
			addFtpTransferedToMap(collection_map, ftpCollectResult);
			//ftp目录表ftp_folder
			Result ftp_folderResult = new Result();
			for (int i = 0; i < ftpCollectResult.getRowCount(); i++) {
				Result result = Dbo.queryResult("select * from " +
								"ftp_folder  where ftp_id = ?",
						ftp_folderResult.getLong(i, "ftp_id"));
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

	private void addAgentDownInfoToMap(Map<String, Object> collection_map, List<Agent_info> agentInfoList) {
		// 1.创建封装agent_down_info表信息集合
		List<Optional<Agent_down_info>> agentDownInfoList =
				new ArrayList<>();
		// 2.遍历agent_info表信息获取agent_id（agent_info表主键，agent_down_info表外键）
		for (int i = 0; i < agentInfoList.size(); i++) {
			// 3.通过agent_id查询agent_down_info表信息
			Optional<Agent_down_info> agent_down_info = Dbo.queryOneObject(Agent_down_info.class,
					"select * from  agent_down_info where  agent_id = ?",
					agentInfoList.get(i).getAgent_id());
			// 4.将agent_down_info表信息放入list
			agentDownInfoList.add(agent_down_info);
		}
		// 5.将agent_down_info表信息入map
		collection_map.put("agent_down_info", agentDownInfoList);
	}

	/**
	 * 封装Data_source表数据集合到map
	 * <p>
	 * 1.根据数据源ID查询数据源data_source集合
	 * 2.判断获取到的集合是否有数据，没有抛异常，有返回数据
	 * 3.将data_source数据入map
	 *
	 * @param source_id      long
	 *                       含义：data_source主键ID
	 *                       取值范围：不为空以及空格，长度不超过10
	 * @param collection_map Map
	 *                       含义：封装数据源下载信息（这里封装的是ftp_transfered表数据集合）
	 *                       取值范围：key唯一
	 */
	private void addDataSourceToMap(long source_id, Map<String, Object> collection_map) {
		// 1.根据数据源ID查询数据源data_source集合
		List<Data_source> dataSourceList = Dbo.queryList(Data_source.class, "select * from " +
				"data_source where  source_id = ?", source_id);
		// 2.判断获取到的集合是否有数据，没有抛异常，有返回数据
		if (dataSourceList.isEmpty()) {
			throw new BusinessException("此数据源下没有数据，source_id = ?" + source_id);
		}
		// 3.将data_source数据入map
		collection_map.put("data_source", dataSourceList);
	}

	/**
	 * 封装ftp_transfered表数据集合到map
	 *
	 * <p>
	 * 1.创建封装ftp_transfered信息的集合
	 * 2.遍历Ftp_collect信息，获取ftp_id(ftp_transfered主键，ftp_collect外键）
	 * 3. 根据ftp_id查询ftp_transfered信息
	 * 4. 返回ftp_transfered集合信息
	 *
	 * @param collection_map   Map
	 *                         含义：封装数据源下载信息（这里封装的是ftp_transfered表数据集合）
	 *                         取值范围：key唯一
	 * @param ftpCollectResult Result
	 *                         含义：ftp_collect表数据集
	 *                         取值范围：不为空
	 */
	private void addFtpTransferedToMap(Map<String, Object> collection_map,
	                                   Result ftpCollectResult) {
		// 1.创建封装ftp_transfered信息的集合
		List<List<Ftp_transfered>> ftpTransferList = new ArrayList<>();
		for (int i = 0; i < ftpCollectResult.getRowCount(); i++) {
			List<Ftp_transfered> list = Dbo.queryList(Ftp_transfered.class, "select * from "
							+ "ftp_transfered where ftp_id=?",
					ftpCollectResult.getLong(i, "ftp_id"));
			ftpTransferList.add(list);
		}
		//
		collection_map.put("ftp_transfered", ftpTransferList);
	}

	/**
	 * 获取ftp_collect表信息
	 * <p>
	 * 1.创建封装ftp_collect信息的集合
	 * 2.遍历agent_info信息，获取agent_id(agent_info主键，ftp_collect外键）
	 * 3. 根据agent_id查询ftp_collect信息
	 * 4. 返回ftp_collect集合信息
	 *
	 * @param agentInfoList List
	 *                      含义：agent_info表数据集合
	 *                      取值范围：不为空
	 * @return 返回ftp_collect集合信息
	 */
	private Result getFtpCollectResult(Map<String, Object> collection_map,
	                                   List<Agent_info> agentInfoList) {
		// 1.创建封装ftp_collect信息的集合
		Result ftpCollectResult = new Result();
		// 2.遍历agent_info信息，获取agent_id(agent_info主键，ftp_collect外键）
		for (int i = 0; i < agentInfoList.size(); i++) {
			// 3. 根据agent_id查询ftp_collect信息
			Result result = Dbo.queryResult("select * from  ftp_collect  where agent_id = ?",
					agentInfoList.get(i).getAgent_id());
			ftpCollectResult.add(result);
		}
		// 4. 返回ftp_collect集合信息
		return ftpCollectResult;
	}

	/**
	 * 封装collect_job_classify表信息入map
	 * <p>
	 * 1.创建封装Collect_job_classify信息的集合
	 * 2.遍历agent_info信息，获取agent_id(agent_info主键，Collect_job_classify外键）
	 * 3. 根据agent_id查询Collect_job_classify信息
	 * 4. 返回Collect_job_classify集合信息
	 *
	 * @param collection_map Map
	 *                       含义：封装数据源下载信息（这里封装的是collect_job_classify表数据集合）
	 *                       取值范围：key唯一
	 * @param agentInfoList  list
	 *                       含义：agent_info表信息集合
	 *                       取值范围：不为空
	 */
	private void addCollectJobClassifyToMap(Map<String, Object> collection_map,
	                                        List<Agent_info> agentInfoList) {
		// 1.创建封装collect_job_classify信息的集合
		List<List<Collect_job_classify>> cjcList = new ArrayList<>();
		// 2.遍历agent_info信息，获取agent_id(agent_info主键，collect_job_classify外键）
		for (int i = 0; i < agentInfoList.size(); i++) {
			// 3. 根据agent_id查询Collect_job_classify信息
			List<Collect_job_classify> jobClassifyList = Dbo.queryList(Collect_job_classify.class,
					"select * from  collect_job_classify where  agent_id = ?",
					agentInfoList.get(i).getAgent_id());
			cjcList.add(jobClassifyList);
		}
		// 4. 将collect_job_classify集合信息入map
		collection_map.put("collect_job_classify", cjcList);
	}

}
