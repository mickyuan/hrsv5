package hrds.b.biz.agent.ftpcollect;

import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Ftp_collect;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;

/**
 * Ftp采集前端接口类，处理ftp采集的增改查
 * date: 2019/9/16 17:55
 * author: zxz
 */
public class FtpCollectAction extends BaseAction {

	/**
	 * 根据ftp_id查询ftp采集设置表
	 * <p>
	 * 1.获取ftp采集表id
	 * 2.查询ftp采集表返回到前端
	 *
	 * @param ftp_id Long
	 *               含义：ftp采集表id
	 *               取值范围：不可为空
	 * @return hrds.commons.entity.Ftp_collect
	 * 含义：ftp采集设置表的值，新增状态下为空
	 * 取值范围：不会为空
	 */
	public Ftp_collect searchFtp_collect(long ftp_id) {
		//文件系统采集ID不为空则表示当前操作为编辑，获取文件系统设置表信息
		return Dbo.queryOneObject(Ftp_collect.class,
				//数据可访问权限处理方式：该表没有对应的用户访问权限限制
				"SELECT * FROM " + Ftp_collect.TableName + " WHERE ftp_id = ?", ftp_id).orElseThrow(()
				-> new BusinessException("根据ftp_id:" + ftp_id + "查询不到ftp_collect表信息"));
	}

	/**
	 * 保存ftp采集表对象
	 * <p>
	 * 1.获取ftp采集表对象
	 * 2.判断ftp采集任务名称是否重复
	 * 3.保存ftp采集表对象
	 *
	 * @param ftp_collect Ftp_collect
	 *                    含义：ftp采集表对象对象不可为空的变量必须有值
	 *                    取值范围：不能为空
	 * @return void
	 */
	public void addFtp_collect(@RequestBean Ftp_collect ftp_collect) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//TODO 使用公共方法校验数据的正确性
		//为空则新增
		//判断ftp采集任务名称是否重复
		long count = Dbo.queryNumber("SELECT count(1) count FROM " + Ftp_collect.TableName
				+ " WHERE ftp_name = ?", ftp_collect.getFtp_name())
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if (count > 0) {
			throw new BusinessException("ftp采集任务名称重复");
		} else {
			ftp_collect.setFtp_id(PrimayKeyGener.getNextId());
			ftp_collect.setIs_sendok(IsFlag.Shi.getCode());
			if (ftp_collect.add(Dbo.db()) != 1)
				throw new BusinessException("新增数据失败！data=" + ftp_collect);
		}
	}

	/**
	 * 更新ftp采集表对象
	 * <p>
	 * 1.获取ftp采集表对象判断主键是否为空
	 * 2.判断ftp采集任务名称是否重复
	 * 3.更新ftp采集表对象
	 *
	 * @param ftp_collect Ftp_collect
	 *                    含义：ftp采集表对象对象不可为空的变量必须有值
	 *                    取值范围：不能为空
	 * @return void
	 */
	public void updateFtp_collect(@RequestBean Ftp_collect ftp_collect) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//TODO 使用公共方法校验数据的正确性
		if (ftp_collect.getFtp_id() == null) {
			throw new BusinessException("更新ftp_collect时ftp_id不能为空");
		}
		//根据ftp_name查询ftp采集任务名称是否与其他采集任务名称重复
		long count = Dbo.queryNumber("SELECT count(1) count FROM " + Ftp_collect.TableName
				+ " WHERE ftp_name = ? AND ftp_id != ?", ftp_collect.getFtp_name(), ftp_collect.getFtp_id())
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if (count > 0) {
			throw new BusinessException("更新后的ftp采集任务名称重复");
		} else {
			ftp_collect.setIs_sendok(IsFlag.Shi.getCode());
			if (ftp_collect.update(Dbo.db()) != 1)
				throw new BusinessException("更新数据失败！data=" + ftp_collect);
		}
	}
}
