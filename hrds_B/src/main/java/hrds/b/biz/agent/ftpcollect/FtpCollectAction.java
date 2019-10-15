package hrds.b.biz.agent.ftpcollect;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Ftp_collect;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;

@DocClass(desc = "Ftp采集前端接口类，处理ftp采集的增改查", author = "zxz", createdate = "2019/9/16 17:55")
public class FtpCollectAction extends BaseAction {
	@Override
	protected Long getUserId() {
		return 1001L;
	}

	@Method(desc = "根据ftp_id查询ftp采集设置表",
			logicStep = "1.根据ftp采集表id查询ftp采集表返回到前端")
	@Param(name = "ftp_id", desc = "ftp采集表id", range = "不可为空")
	@Return(desc = "ftp采集设置表的值，新增状态下为空", range = "不会为空")
	public Ftp_collect searchFtp_collect(long ftp_id) {
		//1.根据ftp采集表id查询ftp采集表返回到前端
		return Dbo.queryOneObject(Ftp_collect.class,
				//数据可访问权限处理方式：该表没有对应的用户访问权限限制
				"SELECT * FROM " + Ftp_collect.TableName + " WHERE ftp_id = ?", ftp_id).orElseThrow(()
				-> new BusinessException("根据ftp_id:" + ftp_id + "查询不到ftp_collect表信息"));
	}

	@Method(desc = "保存ftp采集表对象",
			logicStep = "1.判断ftp采集任务名称是否重复" +
					"2.保存ftp采集表对象")
	@Param(name = "ftp_collect", desc = "ftp采集表对象对象不可为空的变量必须有值", range = "不能为空", isBean = true)
	public void addFtp_collect(Ftp_collect ftp_collect) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//TODO 使用公共方法校验数据的正确性
		//为空则新增
		//1.判断ftp采集任务名称是否重复
		long count = Dbo.queryNumber("SELECT count(1) count FROM " + Ftp_collect.TableName
				+ " WHERE ftp_name = ?", ftp_collect.getFtp_name())
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if (count > 0) {
			throw new BusinessException("ftp采集任务名称重复");
		} else {
			ftp_collect.setFtp_id(PrimayKeyGener.getNextId());
			ftp_collect.setIs_sendok(IsFlag.Shi.getCode());
			//2.保存ftp采集表对象
			ftp_collect.add(Dbo.db());
		}
	}

	@Method(desc = "更新ftp采集表对象",
			logicStep = "1.获取ftp采集表对象判断主键是否为空" +
					"2.根据ftp_name查询ftp采集任务名称是否与其他采集任务名称重复" +
					"3.更新ftp采集表对象")
	@Param(name = "ftp_collect", desc = "ftp采集表对象对象不可为空的变量必须有值", range = "不能为空", isBean = true)
	public void updateFtp_collect(Ftp_collect ftp_collect) {
		//数据可访问权限处理方式：该表没有对应的用户访问权限限制
		//TODO 使用公共方法校验数据的正确性
		//1.获取ftp采集表对象判断主键是否为空
		if (ftp_collect.getFtp_id() == null) {
			throw new BusinessException("更新ftp_collect时ftp_id不能为空");
		}
		//2.根据ftp_name查询ftp采集任务名称是否与其他采集任务名称重复
		long count = Dbo.queryNumber("SELECT count(1) count FROM " + Ftp_collect.TableName
				+ " WHERE ftp_name = ? AND ftp_id != ?", ftp_collect.getFtp_name(), ftp_collect.getFtp_id())
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if (count > 0) {
			throw new BusinessException("更新后的ftp采集任务名称重复");
		} else {
			ftp_collect.setIs_sendok(IsFlag.Shi.getCode());
			//3.更新ftp采集表对象
			ftp_collect.update(Dbo.db());
		}
	}
}
