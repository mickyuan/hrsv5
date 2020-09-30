package hrds.agent.job.biz.core.objectstage.service.impl;

import fd.ng.core.utils.FileNameUtils;
import fd.ng.core.utils.FileUtil;
import fd.ng.core.utils.MD5Util;
import hrds.agent.job.biz.bean.ObjectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.core.objectstage.service.ObjectProcessAbstract;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * MppTableProcessImpl
 * date: 2020/4/26 17:26
 * author: zxz
 */
public class HiveTableProcessImpl extends ObjectProcessAbstract {
	//写文件的流
	private BufferedWriter writer;

	public HiveTableProcessImpl(TableBean tableBean, ObjectTableBean objectTableBean) {
		super(tableBean, objectTableBean);
		if (isZipperKeyMap.isEmpty()) {
			for (String column : selectColumnList) {
				isZipperKeyMap.put(column, true);
			}
		}
		//转存落地的文件路径
		String unloadFileAbsolutePath = FileNameUtils.normalize(Constant.DBFILEUNLOADFOLDER +
				objectTableBean.getOdc_id() + File.separator + objectTableBean.getEn_name() +
				File.separator + objectTableBean.getEtlDate() + File.separator +
				objectTableBean.getEn_name() + ".dat", true);
		try {
			FileUtil.forceMkdir(new File(FileNameUtils.getFullPath(unloadFileAbsolutePath)));
			this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(unloadFileAbsolutePath),
					DataBaseCode.ofValueByCode(tableBean.getDbFileArchivedCode())));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void parserFileToTable(String readFile) {
		String lineValue;
		String code = DataBaseCode.ofValueByCode(objectTableBean.getDatabase_code());
		// 存储全量插入信息的list
		long num = 0;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(readFile)), code))) {
			StringBuilder md5_sb = new StringBuilder();
			StringBuilder value_sb = new StringBuilder();
			while ((lineValue = br.readLine()) != null) {
				//获取定长文件，解析每行数据进行处理
				List<Map<String, Object>> listTiledAttributes = getListTiledAttributes(lineValue, num);
				//hive这里上传默认给文件转成固定分隔符的文件，根据选择的拉链字段算MD5,如果没选择，则使用全字段算md5
				for (Map<String, Object> map : listTiledAttributes) {
					//不要delete逻辑的数据
					if (!"delete".equals(map.get(operate_column))) {
						num++;
						for (String key : selectColumnList) {
							//拼接md
							if (isZipperKeyMap.get(key)) {
								md5_sb.append(map.get(key));
							}
							value_sb.append(map.get(key)).append(Constant.DATADELIMITER);
						}
						//取值完毕，算md5
						value_sb.append(etlDate).append(Constant.DATADELIMITER);
						value_sb.append(Constant.MAXDATE).append(Constant.DATADELIMITER);
						value_sb.append(MD5Util.md5String(md5_sb.toString()));
						writer.write(value_sb.toString());
						writer.write(Constant.DEFAULTLINESEPARATOR);
						value_sb.delete(0, value_sb.length());
						md5_sb.delete(0, value_sb.length());
					}
				}
				if (num > JobConstant.BUFFER_ROW) {
					writer.flush();
				}
			}
			writer.flush();
		} catch (Exception e) {
			throw new AppSystemException("解析半结构化对象文件报错", e);
		}
	}

	@Override
	public void close() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
