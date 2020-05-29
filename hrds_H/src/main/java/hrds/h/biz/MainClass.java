package hrds.h.biz;

import hrds.h.biz.config.MarketConf;
import hrds.h.biz.config.MarketConfUtils;
import hrds.h.biz.realloader.*;
import org.apache.logging.log4j.LogManager;

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

    /**
     * 集市作业发起方法
     * @param datatableId 集市配置表主键
     * @param etlDate 跑批日期
     * @param sqlParams sql动态参数
     * @throws IOException 呵呵
     */
    public static void run(String datatableId, String etlDate, String sqlParams) throws IOException {
        //获取本次作业的运行配置
        MarketConf conf = MarketConf.getConf(datatableId, etlDate, sqlParams);
        MarketConfUtils.serialize(conf);

        //初始化作业上下文实体
        JobContext jobContext = new JobContextImpl(conf);

        //选择导入数据 Loader 实现
        try (Loader loader = LoaderSwitch.switchLoader(conf)) {
            //开始执行导数作业
            new LoaderExecutor()
                    .setJobContext(jobContext)
                    .register(loader)
                    .execute();
        }

    }

    public static void main(String[] args) throws IOException {
        if (args.length == 2) {
            run(args[0], args[1], "");
        } else if (args.length == 3) {
            run(args[0], args[1], args[2]);
        } else {
            LogManager.getLogger().error("参数应为 datatableId etlDate [sqlParams]");
            System.exit(-1);
        }
    }
}
