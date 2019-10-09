package hrds.agent.job.biz.core;

import hrds.agent.job.biz.bean.MetaInfoBean;

import java.util.List;

/**
 * ClassName: MetaInfoInterface <br/>
 * Function: meta信息接口类，任务/作业提供meta信息，实现该接口. <br/>
 * Date: 2019/8/6 14:17 <br/>
 * <p>
 * Author 13616
 * Version 1.0
 * Since JDK 1.8
 **/
public interface MetaInfoInterface {

	/**
	 * 获得meta信息，提供多于1个meta
	 *
	 * @Param: 无
	 *
	 * @return: List<MetaInfoBean>
	 *          含义：存放Meta信息的Bean对象的List集合
	 *          取值范围：不会为null
	 *
	 * */
	List<MetaInfoBean> getMetaInfoGroup();

	/**
	 * 获得meta信息，提供1个meta
	 *
	 * @Param: 无
	 *
	 * @return: MetaInfoBean
	 *          含义：存放Meta信息的Bean对象
	 *          取值范围：不会为null
	 *
	 * */
	MetaInfoBean getMetaInfo();
}
