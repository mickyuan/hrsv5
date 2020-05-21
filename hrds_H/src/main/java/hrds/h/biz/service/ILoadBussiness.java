package hrds.h.biz.service;

import java.io.Closeable;

/**
 * 
 * Description: 业务接口
 *
 * Date:2018年5月13日下午11:40:49 
 * Copyright (c) 2018, yuanqi@beyondsoft.com All Rights Reserved.
 * 
 * @author yuanqi 
 * @version  
 * @since JDK 1.7
 */
public interface ILoadBussiness {

	/**
	 * 是否需要执行进数的业务处理
	 *
	 * @author yuanqi
	 * Date:2018年5月13日下午11:20:54
	 * @throws Exception
	 * @since JDK 1.7
	 */
	void eventLoad();

}
