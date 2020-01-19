package hrds.commons.hadoop.solr;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.CommonVariables;

import java.lang.reflect.Constructor;

@DocClass(desc = "获取solr类实例的工厂类", author = "BY-HLL", createdate = "2020/1/9 0009 上午 10:20")
public class SolrFactory {


    @Method(desc = "获取solr具体实现类实例", logicStep = "获取solr具体实现类实例")
    @Return(desc = "solr具体实现类实例", range = "solr具体实现类实例")
    public static ISolrOperator getInstance() {
        return getInstance(CommonVariables.SOLR_IMPL_CLASS_NAME);
    }

    @Method(desc = "获取solr具体实现类实例", logicStep = "获取solr具体实现类实例,远程solr库")
    @Param(name = "solrParam", desc = "连接远程solr库的url", range = "取值范围说明")
    @Return(desc = "solr具体实现类实例", range = "solr具体实现类实例")
    public static ISolrOperator getInstance(SolrParam solrParam) {
        return getInstance(CommonVariables.SOLR_IMPL_CLASS_NAME, solrParam);
    }

    @Method(desc = "获取solr具体实现类实例",
            logicStep = "获取solr具体实现类实例")
    @Param(name = "className", desc = "实现类全名", range = "取值范围说明")
    @Return(desc = "solr具体实现类实例", range = "solr具体实现类实例")
    public static ISolrOperator getInstance(String className) {
        ISolrOperator solr;
        try {
            solr = (ISolrOperator) Class.forName(className).newInstance();
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
    public static ISolrOperator getInstance(String ClassName, SolrParam solrParam) {
        ISolrOperator solr;
        try {
            Class<?> cl = Class.forName(ClassName);
            Constructor<?> cc = cl.getConstructor(SolrParam.class);
            solr = (ISolrOperator) cc.newInstance(solrParam);
        } catch (Exception e) {
            throw new BusinessException("初始化Solr实例实现类失败...远程!");
        }
        return solr;
    }
}
