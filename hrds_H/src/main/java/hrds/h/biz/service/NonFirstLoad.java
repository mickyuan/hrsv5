package hrds.h.biz.service;


/**
 * 
 * Description: 第二次执行进数
 *
 * Date:2018年5月13日下午10:30:44 
 * Copyright (c) 2018, yuanqi@beyondsoft.com All Rights Reserved.
 * 
 * @author yuanqi 
 * @version  
 * @since JDK 1.7
 */
public interface NonFirstLoad {
	
	/**
	 * 追加
	 *  
	 * @return  
	 * @author yuanqi
	 * Date:2018年5月13日下午10:29:22 
	 * @throws Exception 
	 * @since JDK 1.7
	 */
	void appendData();
	
	/**
	 * 替换
	 *  
	 * @return  
	 * @author yuanqi
	 * Date:2018年5月13日下午10:29:30 
	 * @throws Exception 
	 * @since JDK 1.7
	 */
	void replaceData() ;
	
	/**
	 * 增量
	 *  
	 * @return  
	 * @author yuanqi
	 * Date:2018年5月13日下午10:29:37 
	 * @since JDK 1.7
	 */
	void IncrementData();

	/**
	 * 重跑替换
	 *  
	 * @return  
	 * @author xxx
	 * Date:2018年10月31日10:25:36 
	 * @since JDK 1.7
	 */
	void reAppendData();
	
}
