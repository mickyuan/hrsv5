package hrds.h.biz;

import fd.ng.core.utils.JsonUtil;
import hrds.commons.entity.Dm_relation_datatable;
import hrds.h.biz.config.MarketConfUtils;
import hrds.h.biz.realloader.*;
import hrds.h.biz.config.MarketConf;

import java.io.IOException;

/**
 * 集市任务启动主类入口
 * 脚本调用的主类
 *
 * @Author: Mick Yuan
 * @Date: 20-4-1 上午10:40
 * @Since jdk1.8
 */
public class MainClass {

    public static void run(String datatableId, String etlDate, String sqlParams) throws IOException {
        //获取本次作业的运行配置
        MarketConf conf = MarketConf.getConf(datatableId, etlDate, sqlParams);
        MarketConfUtils.serialize(conf);

        //初始化作业状态实体
        JobState jobState = new JobStateImpl(conf);

        //选择导入数据 Loader 实现
        Loader loader = LoaderSwitch.switchLoader(conf);

        try (LoaderExecutor loaderExecutor = new LoaderExecutor()) {
            //开始执行导数作业
            loaderExecutor
                    .setJobState(jobState)
                    .register(loader)
                    .execute();
        }
    }
}
