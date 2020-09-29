package hrds.agent.job.biz.core.objectstage.service;

import java.util.Map;

/**
 * TableProcessInterface
 * date: 2020/4/26 17:24
 * author: zxz
 */
public interface ObjectProcessInterface {

	void parserFileToTable(String readFile);

	void dealData(Map<String, Map<String, Object>> valueList);

	void excute();

	void close();
}
