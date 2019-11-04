package hrds.b.biz.fulltextsearch.tools;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import hrds.commons.base.BaseAction;
import hrds.commons.exception.BusinessException;
import org.apache.commons.lang.math.NumberUtils;

@DocClass(desc = "文章相似查询类", author = "BY-HLL", createdate = "2019/10/10 0010 上午 10:03")
public class EssaySimilar {

	@Method(desc = "从solr中获取相似的文章", logicStep = "从solr中获取相似的文章")
	@Param(name = "filePath", desc = "文件在HDFS上的路径", range = "HDFS文件全路径")
	@Param(name = "similarityRate", desc = "文章相似率", range = "String类型的字符串0-1", valueIfNull = "1")
	@Param(name = "flag", desc = "是否返回检索到文章的文本内容", range = "boolean类型值，默认为false",
			valueIfNull = "false")
	@Return(desc = "solr获取到的相似文章的结果集", range = "无限制")
	public Result getDocumentSimilarFromSolr(String filePath, String similarityRate, boolean flag) {
		//记录文件在hdfs上的地址
		double dblRate = NumberUtils.toDouble(similarityRate);
		if (NumberUtils.compare(dblRate, 1) == 1) {
			dblRate = 1;
		}
		if (NumberUtils.compare(dblRate, 0) == -1) {
			dblRate = 0;
		}
		if (!StringUtil.isBlank(filePath)) {
			throw new BusinessException("从solr中获取相似的文章的方法未实现！");
		}
		return null;
	}

	@Method(desc = "从solr中获取相似的文章", logicStep = "从solr中获取相似的文章")
	@Param(name = "filePath", desc = "文件avro文件存储路径", range = "HDFS文件全路径，不为空", nullable = false)
	@Param(name = "blockId", desc = "avro文件存储的文件blockId", range = "numeric 长度15 不为空", nullable = false)
	@Param(name = "fileId", desc = "文件唯一id", range = "字符串类型 String 长度40 不为空", nullable = false)
	@Return(desc = "solr获取到的相似文章的结果集", range = "无限制")
	public String getFileSummaryFromAvro(String filePath, String blockId, String fileId) {
		String summary = "";
		if (!StringUtil.isBlank(filePath) && !StringUtil.isBlank(blockId) && !StringUtil.isBlank(fileId)) {
			throw new BusinessException("从avro中获取文章摘要的方法未实现！");
		}
		return summary;
	}
}
