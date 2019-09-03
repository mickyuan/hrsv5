package hrds.a.biz.zsys;

import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DefaultPageImpl;
import fd.ng.db.jdbc.Page;
import fd.ng.db.resultset.Result;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.annotation.RequestParam;
import fd.ng.web.util.Dbo;
import hrds.a.biz.zbase.WebappBaseAction;
import hrds.entity.Sys_para;
import hrds.exception.BusinessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class SysParaAction extends WebappBaseAction {
	private static final Logger logger = LogManager.getLogger();

	public void add(@RequestBean Sys_para sysPara) {
		if(sysPara.add(Dbo.db())!=1)
			throw new BusinessException("添加数据失败！data="+sysPara);
	}

	public void update(String name, String value) {
		Sys_para sysPara = new Sys_para();
		sysPara.setPara_name(name);
		sysPara.setPara_value(value);
		if(sysPara.update(Dbo.db())!=1)
			throw new BusinessException( String.format("更新数据失败！name=%s, value=%s", name, value) );
	}

	public void delete(String name) {
		int nums = Dbo.execute("delete from " + Sys_para.TableName + " where para_name=?", name);
		if(nums!=1) {
			if (nums == 0) throw new BusinessException(String.format("没有数据被删除！name=%s", name));
			else throw new BusinessException(String.format("删除数据异常！name=%s", name));
		}
	}

	public Map<String, Object> getPagedDataList(int currPage, int pageSize, @RequestParam(nullable = true) String value) {
		Page page = new DefaultPageImpl(currPage, pageSize);
		Result rs;
		if(StringUtil.isNotEmpty(value))
			rs = Dbo.queryPagedResult(page,
					"select * from " + Sys_para.TableName + " where para_value like ?", "%" + value + "%");
		else
			rs = Dbo.queryPagedResult(page, "select * from " + Sys_para.TableName);

		Map<String, Object> result = new HashMap<>();
		result.put("count", page.getTotalSize());
		result.put("sysparaList", rs.toList());
		return result;
	}
}
