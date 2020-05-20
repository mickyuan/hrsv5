package hrds.agent.job.biz.core.dbstage.service;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.dbstage.writer.FileWriterFactory;
import hrds.commons.entity.Data_extraction_def;

import java.sql.ResultSet;

@DocClass(desc = "每个采集线程分别调用，用于解析当前线程采集到的ResultSet,并根据卸数的数据文件类型，" +
		"调用相应的方法写数据文件", author = "WangZhengcheng")
public class ResultSetParser {
	@Method(desc = "解析ResultSet", logicStep = "" +
			"1、获得本次采集的数据库META信息" +
			"2、对后续需要使用的META信息(列名，列类型，列长度)，使用分隔符进行组装" +
			"3、在jobInfo中拿到数据清洗规则(字段清洗，表清洗)，并调用工具类(ColCleanRuleParser，TbCleanRuleParser)中的方法进行解析" +
			"4、如果在表清洗中进行了列合并，调用工具类ColumnTool对组装好的META信息进行更新" +
			"5、如果在列清洗中进行了列拆分，调用工具类ColumnTool对组装好的META信息进行更新" +
			"6、落地文件需要追加开始时间和结束时间(9999-12-31)列，如果需要，还要追加MD5列" +
			"7、构造metaDataMap，根据落地数据文件类型，初始化FileWriterInterface实现类，由实现类去写文件" +
			"8、写文件结束，返回本线程生成数据文件的路径")
	@Param(name = "rs", desc = "当前线程执行分页SQL采集到的数据集", range = "不为空")
	@Param(name = "jobInfo", desc = "保存有当前作业信息的实体类对象", range = "不为空，JobInfo实体类对象")
	@Param(name = "pageNum", desc = "当前采集的页码，用于在写文件时计算行计数器，防止多个数据文件中的Avro行号冲突"
			, range = "不为空")
	@Param(name = "pageRow", desc = "当前码的数据量，用于在写文件时计算行计数器，防止多个数据文件中的Avro行号冲突"
			, range = "不为空")
	@Return(desc = "当前线程生成数据文件的路径", range = "不会为null")
	//TODO pageNum和pageRow一起，在写文件的时候，用于判断文件是否过大，如果文件过大，可以对单个数据文件进行拆分
	public String parseResultSet(ResultSet rs, CollectTableBean collectTableBean, int pageNum,
	                             TableBean tableBean, Data_extraction_def data_extraction_def) {
		//XXX 文件后缀现在页面没有，默认给dat
		if (StringUtil.isEmpty(data_extraction_def.getFile_suffix())) {
			data_extraction_def.setFile_suffix("dat");
		}
		//当前线程生成的数据文件的路径，用于返回
		//8、写文件结束，返回本线程生成数据文件的路径和一个写出数据量
		return FileWriterFactory.getFileWriterImpl(rs,
				collectTableBean, pageNum, tableBean, data_extraction_def).writeFiles();
	}
}
