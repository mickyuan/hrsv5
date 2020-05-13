package hrds.agent.job.biz.core.filecollectstage.methods;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.FileCollectParamBean;
import hrds.agent.job.biz.utils.CommunicationUtil;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.ExecuteState;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Collect_case;

import java.util.UUID;

@DocClass(desc = "检测文件采集是否完成", author = "zxz", createdate = "2019/11/4 17:20")
public class CollectionWatcher {
	private FileCollectParamBean fileCollectParamBean;
	private Collect_case collectCase = new Collect_case();

	private String job_rs_id;
	private String excuteLength;
	private Long collect_total;

	public void setCollect_total(Long collect_total) {
		this.collect_total = collect_total;
	}

	public Collect_case getCollectCase() {
		return collectCase;
	}

	public void setExcuteLength(String excuteLength) {

		this.excuteLength = excuteLength;
	}

	public CollectionWatcher(FileCollectParamBean fileCollectParamBean) {
		this.fileCollectParamBean = fileCollectParamBean;
		initJobInfo();
	}

	public String getJob_rs_id() {

		return job_rs_id;
	}

	private void initJobInfo() {
		collectCase.setJob_rs_id(UUID.randomUUID().toString());
		job_rs_id = collectCase.getJob_rs_id();
		collectCase.setAgent_id(fileCollectParamBean.getAgent_id());
		collectCase.setSource_id(fileCollectParamBean.getFcs_id());//数据源
		//XXX 这里我改成了源文件设置表主键，即每个文件夹保存一个
		collectCase.setCollect_set_id(fileCollectParamBean.getFile_source_id());
		collectCase.setEtl_date(fileCollectParamBean.getSysDate());
		collectCase.setTask_classify(fileCollectParamBean.getFile_source_path());//文件夹名称
		collectCase.setCc_remark("");
		collectCase.setJob_group("");
		collectCase.setCollect_type(AgentType.WenJianXiTong.getCode());
		collectCase.setJob_type(AgentType.WenJianXiTong.getCode());
		collectCase.setCollet_database_size("");
		collectCase.setIs_again(IsFlag.Fou.getCode());
		startJob();//此时作为任务开始
	}

	private void startJob() {
		collectCase.setCollect_s_date(DateUtil.getSysDate());//开始采集日期
		collectCase.setCollect_s_time(DateUtil.getSysTime());//开始采集时间
		collectCase.setCollect_e_date("-");
		collectCase.setCollect_e_time("-");
		collectCase.setExecute_state(ExecuteState.KaiShiYunXing.getCode());
		collectCase.setExecute_length("-");
		collectCase.setCollect_total(collect_total);
		//TODO 这里需要调用主服务的接口，去添加入库信息，或者使用mapDB或者PalDB去存每个文件的运行状态，开始，运行中，结束
	}

	public void endJob(String loadMessage) {
		collectCase.setCollect_e_date(DateUtil.getSysDate());
		collectCase.setCollect_e_time(DateUtil.getSysTime());
		collectCase.setExecute_length(excuteLength);//运行总时长
		collectCase.setCollect_total(collect_total);
		//TODO 下面的运行情况的记录，查询，需要调用接口，这里不允许有查询的接口，所有的地方有异常全部抛异常抛出去
		//捕捉到了异常会入库，如果查出此任务有异常被捕捉到，则认为此任务失败，反之成功
		if (!StringUtil.isBlank(loadMessage)) {
			collectCase.setExecute_state(ExecuteState.TongZhiChengGong.getCode());
		} else {
			collectCase.setExecute_state(ExecuteState.TongZhiShiBai.getCode());
		}
		CommunicationUtil.saveCollectCase(collectCase,loadMessage);
	}

}
