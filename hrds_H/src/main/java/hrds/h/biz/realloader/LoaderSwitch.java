package hrds.h.biz.realloader;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.codes.Store_type;
import hrds.commons.collection.ProcessingData;
import hrds.commons.collection.bean.LayerTypeBean;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.DruidParseQuerySql;
import hrds.h.biz.config.MarketConf;

import java.util.List;

/**
 * loader 实现选择器
 * @Author: Mick Yuan
 * @Since jdk1.8
 */
public class LoaderSwitch {

    /**
     * 根据集市配置实体来选择应该执行哪种loader实现
     * @param conf 集市配置类实体
     * @return 需要执行的loader实现
     */
    public static Loader switchLoader(MarketConf conf) {

        String storeType = conf.getDataStoreLayer().getStore_type();

        if (Store_type.DATABASE.getCode().equals(storeType) && isSameJdbc(conf)) {
            return new SameDatabaseLoader(conf);
        }

        if (Store_type.DATABASE.getCode().equals(storeType)) {
            return new DatabaseLoader(conf);
        }
        throw new AppSystemException("无法识别存储码： " + storeType);
    }

    /**
     * 判断是否是同配置的关系型数据库
     * @param conf 集市配置类实体
     * @return 是否是同配置的关系型数据库
     */
    private static boolean isSameJdbc(MarketConf conf) {

        String sql = conf.getCompleteSql();
        //通过sql解析到所有的来源表名
        List<String> listTable = DruidParseQuerySql.parseSqlTableToList(sql);
        //添加输出表名
        listTable.add(conf.getTableName());
        //我要把输入输出表名都放一块来看看是不是都是一个jdbc配置的
        try (DatabaseWrapper db = new DatabaseWrapper()){
            LayerTypeBean allTableIsLayer = ProcessingData.getAllTableIsLayer(listTable, db);
            return allTableIsLayer.getConnType().equals(LayerTypeBean.ConnType.oneJdbc);
        }
    }

}
