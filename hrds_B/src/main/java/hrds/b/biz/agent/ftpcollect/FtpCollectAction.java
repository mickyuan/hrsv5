package hrds.b.biz.agent.ftpcollect;

import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Ftp_collect;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.Optional;

/**
 * description: FtpCollectAction <br>
 * date: 2019/9/16 17:55 <br>
 * author: zxz <br>
 * version: 5.0 <br>
 */
public class FtpCollectAction extends BaseAction {

	/**
	 * description: 根据ftp_id查询ftp采集设置表 <br>
	 * date: 2019/9/16 18:12 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 * 步骤：
	 * 1.获取ftp采集表id
	 * 2.ftp采集表id不为空，查询ftp采集表返回到前端
	 *
	 * @param ftp_id Long
	 *               含义：ftp采集表id
	 *               取值范围：可为空
	 * @return hrds.commons.entity.Ftp_collect
	 * 含义：ftp采集设置表的值，新增状态下为空
	 * 取值范围：可为空
	 */
	public Ftp_collect searchFtp_collect(Long ftp_id) {
		//文件系统采集ID不为空则表示当前操作为编辑，获取文件系统设置表信息
		if (ftp_id != null) {
			Optional<Ftp_collect> query_ftp_collect_info = Dbo.queryOneObject(Ftp_collect.class,
					"SELECT * FROM ftp_collect WHERE ftp_id = ?", ftp_id);
			Ftp_collect ftp_collect_info = query_ftp_collect_info.orElseThrow(() -> new BusinessException("根据ftp_id" +
					ftp_id + "查询不到ftp_collect表信息"));
			return ftp_collect_info;
		}
		return null;
	}

	/**
	 * description: 保存ftp采集表对象 <br>
	 * date: 2019/9/16 18:18 <br>
	 * author: zxz <br>
	 * version: 5.0 <br>
	 * 步骤：
	 * 1.获取ftp采集表对象
	 * 2.根据ftp_id判断是否为新增
	 * 3.新增则保存ftp采集表对象，编辑则更新ftp采集表对象
	 *
	 * @param ftp_collect Ftp_collect
	 *                    含义：ftp采集表对象
	 *                    取值范围：不会为空
	 * @return void
	 */
	public void saveFtp_collect(@RequestBean Ftp_collect ftp_collect) {
		if (ftp_collect.getFtp_id() == null) {
			//新增
			ftp_collect.setFtp_id(PrimayKeyGener.getNextId());
			if (ftp_collect.add(Dbo.db()) != 1)
				throw new BusinessException("新增数据失败！data=" + ftp_collect);
		} else {
			//更新
			if (ftp_collect.update(Dbo.db()) != 1)
				throw new BusinessException("更新数据失败！data=" + ftp_collect);
		}
	}
}
