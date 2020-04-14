package hrds.h.biz;

import fd.ng.core.utils.JsonUtil;
import hrds.commons.entity.Dm_relation_datatable;
import hrds.h.biz.config.MarketConfUtils;
import hrds.h.biz.realloader.*;
import hrds.h.biz.config.MarketConf;

/**
 * 集市任务启动主类入口
 * 脚本调用的主类
 *@Author: Mick Yuan
 *@Date: 20-4-1 上午10:40
 *@Since jdk1.8
 */
public class MainClass {

    public static void run(String datatableId, String etlDate, String sqlParams){
        //获取本次作业的运行配置
        MarketConf conf = MarketConf.getConf(datatableId, etlDate, sqlParams);
        String s = JsonUtil.toJson(conf);
        System.out.println(s);

        MarketConf conf1 = JsonUtil.toObjectSafety(s,MarketConf.class).get();
        //初始化作业状态实体
        Dm_relation_datatable dmRelationDatatable = conf.getDmRelationDatatable();
        JobState jobState = new JobStateImpl(dmRelationDatatable);

        //选择导入数据 Loader 实现
        Loader loader = LoaderSwitch.switchLoader(conf1);

        //开始执行导数作业
        new LoaderExecutor()
                .setJobState(jobState)
                .register(loader)
                .execute();
    }
}
