package hrds.biz.source;

import fd.ng.web.action.AbstractWebappBaseAction;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.annotation.RequestParam;
import fd.ng.web.util.Dbo;
import hrds.entity.DataSource;
import hrds.exception.BusinessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>标 题:海云数服 V3.5</p>
 * <p>描 述:</p>
 * <p>版 权:Copyright(c)2019</p>
 * <p>公 司:博彦科技(上海)有限公司</p>
 * <p>@author :Mr.Lee</p>
 * <p>创建时间:2019-08-22 16:59</p>
 * <p>version:JDK1.8</p>
 */

public class DataSourceAction extends AbstractWebappBaseAction {

	private static final Logger logger = LogManager.getLogger();

	/**
	 * <p>方法描述: </p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-08-22</p>
	 * <p>参   数:  user_id : 用户ID</p>
	 * <p>return:  </p>
	 */
	public void querySource(String user_id) {

		final Object[] objects = Dbo.queryArray("SELECT * FROM " + DataSource.TableName + "WHERE USER_ID = ?", user_id);
	}

	/**
	 * <p>方法描述: </p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-08-22</p>
	 * <p>参   数:  DataSource : 数据源信息表实体类</p>
	 * <p>return:  </p>
	 */
	public void add(@RequestBean DataSource dataSource) {

		if( dataSource.add(Dbo.db()) != 1 ) {
			throw new BusinessException("数据源信息添加失败!" + dataSource);
		}
	}
}
