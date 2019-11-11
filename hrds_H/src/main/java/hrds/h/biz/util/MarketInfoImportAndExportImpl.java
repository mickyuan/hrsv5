package hrds.h.biz.util;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.CodecUtil;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import fd.ng.web.util.FileUploadUtil;
import fd.ng.web.util.ResponseUtil;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DataTableUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.h.biz.market.MarketInfoAction;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;

@DocClass(desc = "集市导入导出类", author = "BY-HLL", createdate = "2019/11/1 0001 下午 02:48")
public class MarketInfoImportAndExportImpl implements ImportAndExport {

	@Method(desc = "集市导出",
			logicStep = "1.初始化待返回的结果集" +
					"2.根据集市id获取集市信息" +
					"3.根据集市id获取集市数据表信息" +
					"4.根据数据表id获取数据表所属信息" +
					"4-1.根据集市数据表id获取数据表字段信息" +
					"4-2.根据集市数据表id获取数据操作信息" +
					"4-3.根据集市数据表id获取数据表已选数据源信息" +
					"4-4.根据集市数据表id获取集市数据表外部存储信息" +
					"4-5.根据集市数据表id获取集市数据表结果映射信息表" +
					"4-6.根据集市数据表id获取数据集市文件导出信息" +
					"4-7.根据集市数据表id获取CarbonData预聚合信息" +
					"4-8.设置集市数据表相关表数据" +
					"5.使用Base64编码" +
					"6.清空response，设置响应编码格式,响应头，控制浏览器下载该文件" +
					"7.通过流写入文件")
	@Param(name = "data_mart_id", desc = "集市id", range = "long类型,长度最长限制19,该id唯一")
	@Return(desc = "集市导出的信息集合", range = "无限制")
	@Override
	public void dataExport(long data_mart_id) {
		HttpServletResponse response = ResponseUtil.getResponse();
		try (OutputStream out = response.getOutputStream()) {
			//数据可访问权限处理方式，此方法不需要权限验证，不涉及用户权限
			//1.初始化待返回的结果集
			Map<String, Object> dataExportMap = new HashMap<>();
			//2.根据集市id获取集市信息
			dataExportMap.put("dataMartInfo", getDataMartInfo(data_mart_id));
			//3.根据集市id获取集市数据表信息
			Result datatableInfoRs = (Result) getDatatableInfo(data_mart_id);
			dataExportMap.put("datatableInfo", datatableInfoRs.toList());
			//4.根据数据表id获取数据表所属信息
			List<Map<String, Object>> dtiList = new ArrayList<>();
			Map<String, Object> dtiMap;
			for (int i = 0; i < datatableInfoRs.getRowCount(); i++) {
				dtiMap = new HashMap<>();
				//4-1.根据集市数据表id获取数据表字段信息
				dtiMap.put("datatableFieldInfo", getDatatableFieldInfo(datatableInfoRs.getLong(i, "datatable_id")));
				//4-2.根据集市数据表id获取数据操作信息
				dtiMap.put("sourceOperationInfo", getSourceOperationInfo(datatableInfoRs.getLong(i, "datatable_id")));
				//4-3.根据集市数据表id获取数据表已选数据源信息
				dtiMap.put("datatableOwnSourceInfo", getDatatableOwnSourceInfo(datatableInfoRs.getLong(i, "datatable_id")));
				//4-4.根据集市数据表id获取集市数据表外部存储信息
				dtiMap.put("datatableInfoOther", getDatatableInfoOther(datatableInfoRs.getLong(i, "datatable_id")));
				//4-5.根据集市数据表id获取集市数据表结果映射信息表
				dtiMap.put("etlMapInfo", getEtlMapInfo(datatableInfoRs.getLong(i, "datatable_id")));
				//4-6.根据集市数据表id获取数据集市文件导出信息
				dtiMap.put("dataMartExport", getDataMartExport(datatableInfoRs.getLong(i, "datatable_id")));
				//4-7.根据集市数据表id获取CarbonData预聚合信息
				dtiMap.put("cbPreAggregate", getCbPreAggregate(datatableInfoRs.getLong(i, "datatable_id")));
				//4-8.设置集市数据表相关表数据
				dtiList.add(i, dtiMap);
			}
			dataExportMap.put("datatableRelatedDataInformation", dtiList);
			//5.使用base64编码
			byte[] bytes = Base64.getEncoder().encode(JsonUtil.toJson(dataExportMap).
					getBytes(CodecUtil.UTF8_CHARSET));
			//6.清空response，设置响应编码格式,响应头，控制浏览器下载该文件
			response.reset();
			response.setCharacterEncoding(CodecUtil.UTF8_STRING);
			response.setContentType("APPLICATION/OCTET-STREAM");
			//7.通过流写入文件
			out.write(bytes);
			out.flush();
		} catch (IOException e) {
			throw new BusinessException("文件导出失败!");
		}
	}

	@Method(desc = "集市导入",
			logicStep = "1.获取上传文件" +
					"2.解码文件" +
					"3.导入文件对应的表信息" +
					"3-1.导入集市信息" +
					"3-2.导入集市数据表信息" +
					"")
	@Param(name = "filePath", desc = "导入文件的全路径", range = "导入文件的全路径",
			example = "windows: C:/tmp/集市名.hrds linux: /tmp/集市名.hrds")
	@Param(name = "userId", desc = "登录用户id", range = "long类型,长度最长限制19,该id唯一")
	@Return(desc = "void", range = "无返回值")
	@Override
	public void dataImport(String filePath, long userId) {
		//数据可访问权限处理方式，此方法不需要权限验证，不涉及用户权限
		//1.获取上传文件
		File uploadedFile = FileUploadUtil.getUploadedFile(filePath);
		//2.解码文件
		Map<String, Object> dataImport;
		try {
			String strTemp = new String(Base64.getDecoder().decode(Files.readAllBytes(uploadedFile.toPath())));
			Type type = new TypeReference<Map<String, Object>>() {
			}.getType();
			dataImport = JsonUtil.toObject(strTemp, type);
		} catch (IOException e) {
			throw new BusinessException("获取上传文件内容失败!");
		}
		//3.导入文件对应的表信息
		//获取集市信息
		Data_mart_info dataMartInfo = (Data_mart_info) dataImport.get("dataMartInfo");
		//获取集市数据表信息
		List<Datatable_info> datatableInfos = (List<Datatable_info>)
				dataImport.get("datatableInfo");
		//获取集市数据表的关联表信息
		List<Map<String, Object>> datatableRelatedDataInformationList = (List<Map<String, Object>>)
				dataImport.get("datatableRelatedDataInformation");
		//3-1.导入集市信息
		setDataMartInfo(dataMartInfo, userId);
		//3-2.导入集市数据表和相关
		setDataTableInfoAndRelatedTableDataInfo(datatableInfos, datatableRelatedDataInformationList);
		//3-3.导入集市数据表相关表数据
	}

	@Method(desc = "根据集市id获取集市信息",
			logicStep = "1.根据集市id获取集市信息")
	@Param(name = "data_mart_id", desc = "集市id", range = "long类型,长度最长限制19,该id唯一")
	@Return(desc = "集市信息", range = "无")
	private List getDataMartInfo(long data_mart_id) {
		//1.根据集市id获取集市信息
		return Dbo.queryList("SELECT * FROM " + Data_mart_info.TableName + " WHERE data_mart_id = ?", data_mart_id);
	}

	@Method(desc = "根据集市id获取集市数据表信息",
			logicStep = "1.根据集市id获取集市数据表信息")
	@Param(name = "data_mart_id", desc = "集市id", range = "long类型,长度最长限制19,该id唯一")
	@Return(desc = "集市数据表信息", range = "无")
	private List getDatatableInfo(long data_mart_id) {
		//1.根据集市id获取集市信息
		return Dbo.queryList("SELECT * FROM " + Datatable_info.TableName + " WHERE data_mart_id = ?", data_mart_id);
	}

	@Method(desc = "根据集市数据表id获取数据表字段信息",
			logicStep = "1.根据集市数据表id获取数据表字段信息")
	@Param(name = "datatable_id", desc = "集市数据表id", range = "long类型,长度最长限制19,该id唯一")
	@Return(desc = "数据表字段信息", range = "无")
	private List getDatatableFieldInfo(long datatable_id) {
		//1.根据集市数据表id获取数据表字段信息
		return Dbo.queryList("SELECT * FROM " + Datatable_field_info.TableName + " WHERE datatable_id = ?", datatable_id);
	}

	@Method(desc = "根据集市数据表id获取数据操作信息",
			logicStep = "1.根据集市数据表id获取数据操作信息")
	@Param(name = "datatable_id", desc = "集市数据表id", range = "long类型,长度最长限制19,该id唯一")
	@Return(desc = "数据操作信息", range = "无")
	private List getSourceOperationInfo(long datatable_id) {
		//1.根据集市数据表id获取数据表字段信息
		return Dbo.queryList("SELECT * FROM " + Source_operation_info.TableName + " WHERE datatable_id = ?", datatable_id);
	}

	@Method(desc = "根据集市数据表id获取数据表已选数据源信息",
			logicStep = "1.根据集市数据表id获取数据操作信息")
	@Param(name = "datatable_id", desc = "集市数据表id", range = "long类型,长度最长限制19,该id唯一")
	@Return(desc = "数据表已选数据源信息", range = "无")
	private List getDatatableOwnSourceInfo(long datatable_id) {
		//1.根据集市数据表id获取数据表已选数据源信息
		return Dbo.queryList("SELECT * FROM " + Datatable_own_source_info.TableName + " WHERE datatable_id = ?",
				datatable_id);
	}

	@Method(desc = "根据集市数据表id获取集市数据表外部存储信息",
			logicStep = "1.根据集市数据表id获取集市数据表外部存储信息")
	@Param(name = "datatable_id", desc = "集市数据表id", range = "long类型,长度最长限制19,该id唯一")
	@Return(desc = "集市数据表外部存储信息", range = "无")
	private List getDatatableInfoOther(long datatable_id) {
		//1.根据集市数据表id获取集市数据表外部存储信息
		return Dbo.queryList("SELECT * FROM " + Datatable_info_other.TableName + " WHERE datatable_id = ?",
				datatable_id);
	}

	@Method(desc = "根据集市数据表id获取集市数据表结果映射信息",
			logicStep = "1.根据集市数据表id获取集市数据表结果映射信息")
	@Param(name = "datatable_id", desc = "集市数据表id", range = "long类型,长度最长限制19,该id唯一")
	@Return(desc = "集市数据表结果映射信息", range = "无")
	private List getEtlMapInfo(long datatable_id) {
		//1.根据集市数据表id获取集市数据表结果映射信息
		return Dbo.queryList("SELECT * FROM " + Etlmap_info.TableName + " WHERE datatable_id = ?",
				datatable_id);
	}

	@Method(desc = "根据集市数据表id获取数据集市文件导出信息",
			logicStep = "1.根据集市数据表id获取数据集市文件导出信息")
	@Param(name = "datatable_id", desc = "集市数据表id", range = "long类型,长度最长限制19,该id唯一")
	@Return(desc = "数据集市文件导出信息", range = "无")
	private List getDataMartExport(long datatable_id) {
		//1.根据集市数据表id获取数据集市文件导出信息
		return Dbo.queryList("SELECT * FROM " + Datamart_export.TableName + " WHERE datatable_id = ?",
				datatable_id);
	}

	@Method(desc = "根据集市数据表id获取CarbonData预聚合信息",
			logicStep = "1.根据集市数据表id获取CarbonData预聚合信息")
	@Param(name = "datatable_id", desc = "集市数据表id", range = "long类型,长度最长限制19,该id唯一")
	@Return(desc = "CarbonData预聚合信息", range = "无")
	private List getCbPreAggregate(long datatable_id) {
		//1.根据集市数据表id获取CarbonData预聚合信息
		return Dbo.queryList("SELECT * FROM " + Cb_preaggregate.TableName + " WHERE datatable_id = ?",
				datatable_id);
	}

	@Method(desc = "导入集市信息表",
			logicStep = "1.校验数据合法性" +
					"2.设置导入属性" +
					"3.导入集市信息表")
	@Param(name = "dataMartInfo", desc = "Data_mart_info 实体对象", range = "实体对象", isBean = true)
	@Param(name = "userId", desc = "操作用户id", range = "long类型,长度最长限制19,该id唯一")
	private void setDataMartInfo(Data_mart_info dataMartInfo, long userId) {
		//数据可访问权限处理方式，根据登录用户id
		//1.校验数据合法性
		if (StringUtil.isBlank(dataMartInfo.getMart_number())) {
			throw new BusinessException("待导入的集市编号为空! mart_number=" + dataMartInfo.getMart_number());
		}
		if (MarketInfoAction.checkMarketNameIsRepeat(dataMartInfo.getMart_name())) {
			throw new BusinessException("集市名称已经存在! mart_name=" + dataMartInfo.getMart_name());
		}
		//2.设置导入属性
		dataMartInfo.setData_mart_id(PrimayKeyGener.getNextId());
		dataMartInfo.setCreate_id(userId);
		//3.导入集市信息表
		dataMartInfo.add(Dbo.db());
	}

	@Method(desc = "导入集市数据表和相关所属信息",
			logicStep = "1.判断所有存储层中是否存在该表,返回结果(报错提示重复位置 或者 false:不存在)" +
					"2.导入数据表所属信息")
	@Param(name = "datatableInfos", desc = "数据表对象的List", range = "List集合")
	@Param(name = "datatableRelatedDataInformationList", desc = "相关所属信息的List", range = "List集合")
	private void setDataTableInfoAndRelatedTableDataInfo(List<Datatable_info> datatableInfos,
	                                                     List<Map<String, Object>> datatableRelatedDataInformationList) {
		for (int i = 0; i < datatableInfos.size(); i++) {
			//1.判断所有存储层中是否存在该表,返回结果(报错提示重复位置 或者 false:不存在)
			if (!DataTableUtil.tableIsRepeat(datatableInfos.get(i).getDatatable_en_name())) {

				String datatableNewId = PrimayKeyGener.getNextId();
				datatableInfos.get(i).setDatatable_id(datatableNewId);
				datatableInfos.get(i).setEtl_date("00000000");
				datatableInfos.get(i).setDatatable_create_date(DateUtil.getSysDate());
				datatableInfos.get(i).setDatatable_create_time(DateUtil.getSysTime());
				datatableInfos.get(i).setDdlc_date(DateUtil.getSysDate());
				datatableInfos.get(i).setDdlc_time(DateUtil.getSysTime());
				datatableInfos.get(i).setSoruce_size("0");
				datatableInfos.get(i).add(Dbo.db());
				//2.导入数据表所属信息
				//2-1.导入数据表字段信息
				List<Datatable_field_info> datatableFieldInfos = (List<Datatable_field_info>)
						datatableRelatedDataInformationList.get(i).get("datatableFieldInfo");
				setDatatableFieldInfo(datatableFieldInfos, datatableNewId);
				//2-2.导入数据操作信息
				List<Source_operation_info> sourceOperationInfos = (List<Source_operation_info>)
						datatableRelatedDataInformationList.get(i).get("sourceOperationInfo");
				setSourceOperationInfo(sourceOperationInfos, datatableNewId);
				//2-3.导入数据表已选数据源信息同时导入结果映射信息表信息
				List<Datatable_own_source_info> datatableOwnSourceInfos = (List<Datatable_own_source_info>)
						datatableRelatedDataInformationList.get(i).get("datatableOwnSourceInfo");
				List<Etlmap_info> etlMapInfos = (List<Etlmap_info>)
						datatableRelatedDataInformationList.get(i).get("etlMapInfo");
				setDatatableOwnSourceInfo(datatableOwnSourceInfos, etlMapInfos, datatableNewId);
				//2-4.导入集市数据表外部存储信息
				List<Datatable_info_other> datatableInfoOthers = (List<Datatable_info_other>)
						datatableRelatedDataInformationList.get(i).get("datatableInfoOther");
				setDatatableInfoOther(datatableInfoOthers, datatableNewId);
				//2-5.导入数据集市文件导出信息
				List<Datamart_export> dataMartExports = (List<Datamart_export>)
						datatableRelatedDataInformationList.get(i).get("dataMartExport");
				setDataMartExport(dataMartExports, datatableNewId);
				//2-6.导入CarbonData预聚合信息
				List<Cb_preaggregate> cbPreAggregates = (List<Cb_preaggregate>)
						datatableRelatedDataInformationList.get(i).get("cbPreAggregate");
				setCbPreAggregate(cbPreAggregates, datatableNewId);
			}
		}
	}

	@Method(desc = "导入数据表字段信息",
			logicStep = "1.导入数据表字段信息")
	@Param(name = "datatableFieldInfos", desc = "数据表字段对象的List", range = "List集合,不为null")
	@Param(name = "datatableId", desc = "数据表id", range = "String类型,长度最长限制19,该id唯一")
	private void setDatatableFieldInfo(List<Datatable_field_info> datatableFieldInfos, String datatableId) {
		//1.导入数据表字段信息
		for (Datatable_field_info datatableFieldInfo : datatableFieldInfos) {
			datatableFieldInfo.setDatatable_field_id(PrimayKeyGener.getNextId());
			datatableFieldInfo.setDatatable_id(datatableId);
			datatableFieldInfo.add(Dbo.db());
		}
	}

	@Method(desc = "导入数据操作信息",
			logicStep = "1.导入数据操作信息")
	@Param(name = "sourceOperationInfos", desc = "数据操作信息对象的List", range = "List集合,不为null")
	@Param(name = "datatableId", desc = "数据表id", range = "String类型,长度最长限制19,该id唯一")
	private void setSourceOperationInfo(List<Source_operation_info> sourceOperationInfos, String datatableId) {
		//1.导入数据操作信息
		for (Source_operation_info sourceOperationInfo : sourceOperationInfos) {
			sourceOperationInfo.setId(PrimayKeyGener.getNextId());
			sourceOperationInfo.setDatatable_id(datatableId);
			sourceOperationInfo.add(Dbo.db());
		}
	}

	@Method(desc = "导入数据表已选数据源信息同时导入结果映射信息表信息",
			logicStep = "1.导入数据表已选数据源信息" +
					"1-1.导入结果映射信息表信息")
	@Param(name = "datatableOwnSourceInfos", desc = "数据表已选数据源信息对象的List", range = "List集合,不为null")
	@Param(name = "etlMapInfos", desc = "结果映射信息对象的List", range = "List集合,不为null")
	@Param(name = "datatableId", desc = "数据表id", range = "String类型,长度最长限制19,该id唯一")
	private void setDatatableOwnSourceInfo(List<Datatable_own_source_info> datatableOwnSourceInfos,
	                                       List<Etlmap_info> etlMapInfos, String datatableId) {
		//1.导入数据表已选数据源信息
		for (Datatable_own_source_info datatableOwnSourceInfo : datatableOwnSourceInfos) {
			datatableOwnSourceInfo.setOwn_dource_table_id(PrimayKeyGener.getNextId());
			datatableOwnSourceInfo.setDatatable_id(datatableId);
			datatableOwnSourceInfo.add(Dbo.db());
			//1-1.导入结果映射信息表信息
			setEtlMapInfo(etlMapInfos, datatableId, datatableOwnSourceInfo);
		}
	}

	@Method(desc = "导入集市数据表外部存储信息",
			logicStep = "1.导入集市数据表外部存储信息")
	@Param(name = "datatableOwnSourceInfos", desc = "数据表外部存储信息对象的List", range = "List集合,不为null")
	@Param(name = "datatableId", desc = "数据表id", range = "String类型,长度最长限制19,该id唯一")
	private void setDatatableInfoOther(List<Datatable_info_other> datatableInfoOthers, String datatableId) {
		for (Datatable_info_other datatableInfoOther : datatableInfoOthers) {
			datatableInfoOther.setOther_id(PrimayKeyGener.getNextId());
			datatableInfoOther.setDatatable_id(datatableId);
			datatableInfoOther.add(Dbo.db());
		}
	}

	@Method(desc = "导入结果映射信息表信息",
			logicStep = "1.导入结果映射信息表信息")
	@Param(name = "etlMapInfos", desc = "结果映射信息表信息对象的List", range = "List集合,不为null")
	@Param(name = "datatableId", desc = "数据表id", range = "String类型,长度最长限制19,该id唯一")
	@Param(name = "datatableOwnSourceInfo", desc = "数据表已选数据源信息对象", range = "不为null")
	private void setEtlMapInfo(List<Etlmap_info> etlMapInfos, String datatableId,
	                           Datatable_own_source_info datatableOwnSourceInfo) {
		//1.导入结果映射信息表信息
		for (Etlmap_info etlMapInfo : etlMapInfos) {
			etlMapInfo.setEtl_id(PrimayKeyGener.getNextId());
			etlMapInfo.setDatatable_id(datatableId);
			etlMapInfo.setOwn_dource_table_id(datatableOwnSourceInfo.getOwn_dource_table_id());
			etlMapInfo.add(Dbo.db());
		}
	}

	@Method(desc = "导入数据集市文件导出信息",
			logicStep = "1.导入数据集市文件导出信息")
	@Param(name = "dataMartExports", desc = "数据集市文件导出信息对象的List", range = "List集合,不为null")
	@Param(name = "datatableId", desc = "数据表id", range = "String类型,长度最长限制19,该id唯一")
	private void setDataMartExport(List<Datamart_export> dataMartExports, String datatableId) {
		//1.导入数据集市文件导出信息
		for (Datamart_export dataMartExport : dataMartExports) {
			dataMartExport.setExport_id(PrimayKeyGener.getNextId());
			dataMartExport.setDatatable_id(datatableId);
		}
	}

	@Method(desc = "导入CarbonData预聚合信息",
			logicStep = "1.导入CarbonData预聚合信息")
	@Param(name = "cbPreAggregates", desc = "CarbonData预聚合信息对象的List", range = "List集合,不为null")
	@Param(name = "datatableId", desc = "数据表id", range = "String类型,长度最长限制19,该id唯一")
	private void setCbPreAggregate(List<Cb_preaggregate> cbPreAggregates, String datatableId) {
		//1.导入CarbonData预聚合信息
		for (Cb_preaggregate cbPreAggregate : cbPreAggregates) {
			cbPreAggregate.setAgg_id(PrimayKeyGener.getNextId());
			cbPreAggregate.setDatatable_id(datatableId);
			cbPreAggregate.add(Dbo.db());
		}
	}
}
