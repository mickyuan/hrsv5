package hrds.b.biz.agent.dbagentconf.cleanconf;

import hrds.commons.entity.Table_clean;
import hrds.commons.entity.Table_info;

import java.util.List;

/**
 * @Description: 定义清洗规则Action测试类
 * @Author: wangz
 * @CreateTime: 2019-10-12-17:10
 * @BelongsProject: hrsv5
 * @BelongsPackage: hrds.b.biz.agent.dbagentconf.cleanconf
 **/
public class CleanConfStepActionTest {

	/*
	* 字符补齐保存按钮
	* */
	public void saveTableCleanCompletion(Table_clean table_clean){

	}

	/*
	 * 字符替换保存按钮
	 * */
	public void saveTableCleanReplace(List<Table_clean> replaceList){

	}

	/*
	 * 全表清洗优先级保存按钮
	 * */
	public void saveTableCleanOrder(List<Table_info> tableInfos){
		//先在table_info表中保存
	}

}
