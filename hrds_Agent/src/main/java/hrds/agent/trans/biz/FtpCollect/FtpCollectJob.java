package hrds.agent.trans.biz.FtpCollect;

import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import hrds.agent.job.biz.bean.JobParamBean;
import hrds.agent.job.biz.core.JobFactory;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Ftp_collect;

/**
 * 接收页面定义的参数执行ftp采集
 * date: 2019/10/10 16:29
 * author: zxz
 */
public class FtpCollectJob extends BaseAction {

	@Method(desc = "ftp采集和前端交互的接口",
			logicStep = "1.获取参数，校验对象的值是否正确" +
					"2.使用JobFactory工厂类调用后台方法")
	@Param(name = "ftp_collect", desc = "Ftp采集设置表对象",
			isBean = true, range = "所有这张表不能为空的字段的值必须有，为空则会抛异常")
	public void execute(Ftp_collect ftp_collect) {
		//1.获取参数，校验对象的值是否正确
		//TODO 使用公共方法校验ftp_collect对象的值得合法性
		//TODO Agent这个参数该怎么接，是统一封装成工厂需要的参数吗？
		//2.使用JobFactory工厂类调用后台方法
		JobFactory.newInstance(null, null, new JobParamBean(),
				"", null).runJob();
	}
}
