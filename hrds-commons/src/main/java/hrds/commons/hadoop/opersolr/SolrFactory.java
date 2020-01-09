package hrds.commons.hadoop.opersolr;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.PropertyParaValue;

import java.lang.reflect.Constructor;

@DocClass(desc = "获取solr类实例的工厂类", author = "博彦科技", createdate = "2020/1/9 0009 上午 10:20")
public class SolrFactory {

	//初始化solr具体实现类全名
	private static final String CLASS_NAME = PropertyParaValue.getString("solrclassname",
			"hrds.commons.hadoop.opersolr.OperSolrImpl5_3_1");

	@Method(desc = "获取solr具体实现类实例",
			logicStep = "获取solr具体实现类实例")
	@Param(name = "className", desc = "实现类全名", range = "取值范围说明")
	@Return(desc = "solr具体实现类实例", range = "solr具体实现类实例")
	public static OperSolr getInstance(String className) {
		OperSolr solr;
		try {
			solr = (OperSolr) Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new BusinessException("初始化Solr实例实现类失败...！");
		}
		return solr;
	}

	@Method(desc = "获取solr具体实现类实例",
			logicStep = "获取solr具体实现类实例")
	@Param(name = "className", desc = "实现类全名", range = "实现类全名")
	@Param(name = "solrParam", desc = "连接远程solr库的url", range = "url")
	@Return(desc = "solr具体实现类实例", range = "solr具体实现类实例")
	public static OperSolr getInstance(String ClassName, SolrParam solrParam) {
		OperSolr solr;
		try {
			Class<?> cl = Class.forName(ClassName);
			Constructor<?> cc = cl.getConstructor(SolrParam.class);
			solr = (OperSolr) cc.newInstance(solrParam);
		} catch (Exception e) {
			throw new BusinessException("初始化Solr实例实现类失败...远程!");
		}
		return solr;
	}

	@Method(desc = "获取solr具体实现类实例", logicStep = "获取solr具体实现类实例,远程solr库")
	@Param(name = "solrParam", desc = "连接远程solr库的url", range = "取值范围说明")
	@Return(desc = "solr具体实现类实例", range = "solr具体实现类实例")
	public static OperSolr getInstance(SolrParam solrParam) {
		return getInstance(CLASS_NAME, solrParam);
	}

	@Method(desc = "获取solr具体实现类实例", logicStep = "获取solr具体实现类实例")
	@Return(desc = "solr具体实现类实例", range = "solr具体实现类实例")
	public static OperSolr getInstance() {
		return getInstance(CLASS_NAME);
	}
}
