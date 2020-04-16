package hrds.g.biz.serviceuser.query;

import java.util.Map;

public interface Query {

	//查询
	public Query query();

	//返回数据
	public Map<String, Object> feedback();
}
