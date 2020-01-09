package hrds.agent.job.biz.core;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.DateUtil;
import hrds.agent.job.biz.bean.JobStatusInfo;
import hrds.agent.job.biz.bean.StageParamInfo;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.dbstage.failed.DBDataLoadingStageImplFailed;
import hrds.agent.job.biz.core.dbstage.failed.DBUnloadDataStageImplFailed;
import hrds.agent.job.biz.core.dbstage.succeed.*;
import hrds.agent.job.biz.utils.FileUtil;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.DataBaseCode;
import hrds.commons.codes.IsFlag;
import hrds.commons.utils.Constant;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class JobStageControllerTest {

	private static final String DATABASE_ID = "1001";
	private static final String TABLE_ID = "100101";
	private static final String STATUS_FILE_PATH = Constant.JOBINFOPATH + DATABASE_ID + File.separator + TABLE_ID + File.separator + Constant.JOBFILENAME;

	/**
	 * 1、第一次执行正常顺序的责任链，即卸数文件不存在
	 * 2、第一次执行完毕后，再次执行，可以模拟正常执行完采集任务后重跑该采集任务
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void runJobTestOne(){
		try {
			buildChainForCollection();

			JobStatusInfo afterJobStatus = JSONObject.parseObject(FileUtil.readFile2String(new File(STATUS_FILE_PATH)), JobStatusInfo.class);
			assertThat("卸数阶段成功", afterJobStatus.getUnloadDataStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("卸数阶段成功，重跑次数为0", afterJobStatus.getUnloadDataStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("卸数阶段成功，重跑次数为0", afterJobStatus.getUnloadDataStatus().getAgainNum(), is(0));

			assertThat("上传阶段成功", afterJobStatus.getUploadStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("上传阶段成功，重跑次数为0", afterJobStatus.getUploadStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("上传阶段成功，重跑次数为0", afterJobStatus.getUploadStatus().getAgainNum(), is(0));

			assertThat("加载阶段成功", afterJobStatus.getDataLodingStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("加载阶段成功，重跑次数为0", afterJobStatus.getDataLodingStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("加载阶段成功，重跑次数为0", afterJobStatus.getDataLodingStatus().getAgainNum(), is(0));

			assertThat("计算增量阶段成功", afterJobStatus.getCalIncrementStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("计算增量阶段成功，重跑次数为0", afterJobStatus.getCalIncrementStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("计算增量阶段成功，重跑次数为0", afterJobStatus.getCalIncrementStatus().getAgainNum(), is(0));

			assertThat("数据登记阶段成功", afterJobStatus.getDataRegistrationStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("数据登记阶段成功，重跑次数为0", afterJobStatus.getDataRegistrationStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("数据登记阶段成功，重跑次数为0", afterJobStatus.getDataRegistrationStatus().getAgainNum(), is(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 构建JobStatusInfo对象，模拟卸数阶段失败，重跑卸数阶段和运行后面的阶段
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void runJobTestTwo(){
		//构建数据，模拟采集的卸数阶段错误，将信息写入文件
		File file = new File(STATUS_FILE_PATH);
		JobStatusInfo jobStatusInfo = new JobStatusInfo();
		jobStatusInfo.setJobId(TABLE_ID);
		jobStatusInfo.setStartDate(DateUtil.getSysDate());
		jobStatusInfo.setEndDate(DateUtil.getSysDate());
		jobStatusInfo.setStartTime(DateUtil.getSysTime());
		jobStatusInfo.setEndTime(DateUtil.getSysTime());

		StageStatusInfo unloadDataStatus = new StageStatusInfo();
		unloadDataStatus.setStatusCode(RunStatusConstant.FAILED.getCode());
		unloadDataStatus.setJobId(TABLE_ID);
		unloadDataStatus.setStartDate(DateUtil.getSysDate());
		unloadDataStatus.setEndDate(DateUtil.getSysDate());
		unloadDataStatus.setStartTime(DateUtil.getSysTime());
		unloadDataStatus.setEndTime(DateUtil.getSysTime());
		unloadDataStatus.setStageNameCode(StageConstant.UNLOADDATA.getCode());
		unloadDataStatus.setMessage("执行失败");

		jobStatusInfo.setUnloadDataStatus(unloadDataStatus);

		StageParamInfo stageParamInfo = new StageParamInfo();
		stageParamInfo.setStatusInfo(unloadDataStatus);

		jobStatusInfo.setStageParamInfo(stageParamInfo);

		try {
			FileUtils.write(file, JSON.toJSONString(jobStatusInfo), DataBaseCode.UTF_8.getValue());
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertThat("模拟文件构建完毕", file.exists(), is(true));

		//重新读取文件，构建采集责任链，重跑卸数阶段和剩下的阶段
		try {
			buildChainForCollection();
			JobStatusInfo afterJobStatus = JSONObject.parseObject(FileUtil.readFile2String(new File(STATUS_FILE_PATH)), JobStatusInfo.class);
			assertThat("卸数阶段成功", afterJobStatus.getUnloadDataStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("卸数阶段成功，重跑次数为1", afterJobStatus.getUnloadDataStatus().getIsAgain(), is(IsFlag.Shi.getCode()));
			assertThat("卸数阶段成功，重跑次数为1", afterJobStatus.getUnloadDataStatus().getAgainNum(), is(1));

			assertThat("上传阶段成功", afterJobStatus.getUploadStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("上传阶段成功，重跑次数为0", afterJobStatus.getUploadStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("上传阶段成功，重跑次数为0", afterJobStatus.getUploadStatus().getAgainNum(), is(0));

			assertThat("加载阶段成功", afterJobStatus.getDataLodingStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("加载阶段成功，重跑次数为0", afterJobStatus.getDataLodingStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("加载阶段成功，重跑次数为0", afterJobStatus.getDataLodingStatus().getAgainNum(), is(0));

			assertThat("计算增量阶段成功", afterJobStatus.getCalIncrementStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("计算增量阶段成功，重跑次数为0", afterJobStatus.getCalIncrementStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("计算增量阶段成功，重跑次数为0", afterJobStatus.getCalIncrementStatus().getAgainNum(), is(0));

			assertThat("数据登记阶段成功", afterJobStatus.getDataRegistrationStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("数据登记阶段成功，重跑次数为0", afterJobStatus.getDataRegistrationStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("数据登记阶段成功，重跑次数为0", afterJobStatus.getDataRegistrationStatus().getAgainNum(), is(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 构建JobStatusInfo对象，模拟卸数阶段成功，上传阶段失败，重跑上传阶段及其后面的内容
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void runJobTestThree(){
		//构建数据，模拟采集的状态文件，将信息写入文件
		File file = new File(STATUS_FILE_PATH);
		JobStatusInfo jobStatusInfo = new JobStatusInfo();
		jobStatusInfo.setJobId(TABLE_ID);
		jobStatusInfo.setStartDate(DateUtil.getSysDate());
		jobStatusInfo.setEndDate(DateUtil.getSysDate());
		jobStatusInfo.setStartTime(DateUtil.getSysTime());
		jobStatusInfo.setEndTime(DateUtil.getSysTime());

		//卸数阶段状态信息，卸数成功
		StageStatusInfo unloadDataStatus = buildSucceedUnloadStatus();

		//数据上传阶段信息，上传失败
		StageStatusInfo uploadStatus = new StageStatusInfo();
		uploadStatus.setStatusCode(RunStatusConstant.FAILED.getCode());
		uploadStatus.setJobId(TABLE_ID);
		uploadStatus.setStartDate(DateUtil.getSysDate());
		uploadStatus.setEndDate(DateUtil.getSysDate());
		uploadStatus.setStartTime(DateUtil.getSysTime());
		uploadStatus.setEndTime(DateUtil.getSysTime());
		uploadStatus.setStageNameCode(StageConstant.UPLOAD.getCode());
		uploadStatus.setMessage("上传执行失败");

		jobStatusInfo.setUnloadDataStatus(unloadDataStatus);
		jobStatusInfo.setUploadStatus(uploadStatus);

		StageParamInfo stageParamInfo = new StageParamInfo();
		stageParamInfo.setStatusInfo(uploadStatus);

		jobStatusInfo.setStageParamInfo(stageParamInfo);

		//将作业状态信息写入文件
		try {
			FileUtils.write(file, JSON.toJSONString(jobStatusInfo), DataBaseCode.UTF_8.getValue());
		} catch (IOException e) {
			e.printStackTrace();
		}

		//重新读取文件，构建采集责任链，跳过卸数阶段，重跑上传阶段和剩下的阶段
		try {
			buildChainForCollection();

			JobStatusInfo afterJobStatus = JSONObject.parseObject(FileUtil.readFile2String(new File(STATUS_FILE_PATH)), JobStatusInfo.class);
			assertThat("卸数阶段成功", afterJobStatus.getUnloadDataStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("卸数阶段成功，重跑次数为0", afterJobStatus.getUnloadDataStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("卸数阶段成功，重跑次数为0", afterJobStatus.getUnloadDataStatus().getAgainNum(), is(0));

			assertThat("上传阶段成功", afterJobStatus.getUploadStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("上传阶段成功，重跑次数为1", afterJobStatus.getUploadStatus().getIsAgain(), is(IsFlag.Shi.getCode()));
			assertThat("上传阶段成功，重跑次数为1", afterJobStatus.getUploadStatus().getAgainNum(), is(1));

			assertThat("加载阶段成功", afterJobStatus.getDataLodingStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("加载阶段成功，重跑次数为0", afterJobStatus.getDataLodingStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("加载阶段成功，重跑次数为0", afterJobStatus.getDataLodingStatus().getAgainNum(), is(0));

			assertThat("计算增量阶段成功", afterJobStatus.getCalIncrementStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("计算增量阶段成功，重跑次数为0", afterJobStatus.getCalIncrementStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("计算增量阶段成功，重跑次数为0", afterJobStatus.getCalIncrementStatus().getAgainNum(), is(0));

			assertThat("数据登记阶段成功", afterJobStatus.getDataRegistrationStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("数据登记阶段成功，重跑次数为0", afterJobStatus.getDataRegistrationStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("数据登记阶段成功，重跑次数为0", afterJobStatus.getDataRegistrationStatus().getAgainNum(), is(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 构建JobStatusInfo对象，模拟卸数、上传、加载、增量阶段成功，登记阶段失败，重跑登记阶段
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void runJobTestFour(){
		//构建数据，模拟采集的状态文件，将信息写入文件
		File file = new File(STATUS_FILE_PATH);
		JobStatusInfo jobStatusInfo = new JobStatusInfo();
		jobStatusInfo.setJobId(TABLE_ID);
		jobStatusInfo.setStartDate(DateUtil.getSysDate());
		jobStatusInfo.setEndDate(DateUtil.getSysDate());
		jobStatusInfo.setStartTime(DateUtil.getSysTime());
		jobStatusInfo.setEndTime(DateUtil.getSysTime());

		//卸数阶段状态信息，卸数成功
		StageStatusInfo unloadDataStatus = buildSucceedUnloadStatus();

		//数据上传阶段信息，上传成功
		StageStatusInfo uploadStatus = new StageStatusInfo();
		uploadStatus.setStatusCode(RunStatusConstant.SUCCEED.getCode());
		uploadStatus.setJobId(TABLE_ID);
		uploadStatus.setStartDate(DateUtil.getSysDate());
		uploadStatus.setEndDate(DateUtil.getSysDate());
		uploadStatus.setStartTime(DateUtil.getSysTime());
		uploadStatus.setEndTime(DateUtil.getSysTime());
		uploadStatus.setStageNameCode(StageConstant.UPLOAD.getCode());
		uploadStatus.setMessage("上传执行成功");

		//数据加载阶段信息，加载成功
		StageStatusInfo loadingStatus = new StageStatusInfo();
		loadingStatus.setStatusCode(RunStatusConstant.SUCCEED.getCode());
		loadingStatus.setJobId(TABLE_ID);
		loadingStatus.setStartDate(DateUtil.getSysDate());
		loadingStatus.setEndDate(DateUtil.getSysDate());
		loadingStatus.setStartTime(DateUtil.getSysTime());
		loadingStatus.setEndTime(DateUtil.getSysTime());
		loadingStatus.setStageNameCode(StageConstant.DATALOADING.getCode());
		loadingStatus.setMessage("加载执行成功");

		//数据计算增量阶段信息，计算增量成功
		StageStatusInfo incrementStatus = new StageStatusInfo();
		incrementStatus.setStatusCode(RunStatusConstant.SUCCEED.getCode());
		incrementStatus.setJobId(TABLE_ID);
		incrementStatus.setStartDate(DateUtil.getSysDate());
		incrementStatus.setEndDate(DateUtil.getSysDate());
		incrementStatus.setStartTime(DateUtil.getSysTime());
		incrementStatus.setEndTime(DateUtil.getSysTime());
		incrementStatus.setStageNameCode(StageConstant.CALINCREMENT.getCode());
		incrementStatus.setMessage("计算增量执行成功");

		//数据登记阶段信息，登记失败
		StageStatusInfo registerStatus = new StageStatusInfo();
		registerStatus.setStatusCode(RunStatusConstant.FAILED.getCode());
		registerStatus.setJobId(TABLE_ID);
		registerStatus.setStartDate(DateUtil.getSysDate());
		registerStatus.setEndDate(DateUtil.getSysDate());
		registerStatus.setStartTime(DateUtil.getSysTime());
		registerStatus.setEndTime(DateUtil.getSysTime());
		registerStatus.setStageNameCode(StageConstant.DATAREGISTRATION.getCode());
		registerStatus.setMessage("计算增量执行失败");

		jobStatusInfo.setUnloadDataStatus(unloadDataStatus);
		jobStatusInfo.setUploadStatus(uploadStatus);
		jobStatusInfo.setDataLodingStatus(loadingStatus);
		jobStatusInfo.setCalIncrementStatus(incrementStatus);
		jobStatusInfo.setDataRegistrationStatus(registerStatus);

		StageParamInfo stageParamInfo = new StageParamInfo();
		stageParamInfo.setStatusInfo(registerStatus);

		jobStatusInfo.setStageParamInfo(stageParamInfo);

		//将作业状态信息写入文件
		try {
			FileUtils.write(file, JSON.toJSONString(jobStatusInfo), DataBaseCode.UTF_8.getValue());
		} catch (IOException e) {
			e.printStackTrace();
		}

		//重新读取文件，构建采集责任链，跳过执行成功的阶段，重跑数据登记阶段
		try {
			buildChainForCollection();

			JobStatusInfo afterJobStatus = JSONObject.parseObject(FileUtil.readFile2String(new File(STATUS_FILE_PATH)), JobStatusInfo.class);
			assertThat("卸数阶段成功", afterJobStatus.getUnloadDataStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("卸数阶段成功，重跑次数为0", afterJobStatus.getUnloadDataStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("卸数阶段成功，重跑次数为0", afterJobStatus.getUnloadDataStatus().getAgainNum(), is(0));

			assertThat("上传阶段成功", afterJobStatus.getUploadStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("上传阶段成功，重跑次数为0", afterJobStatus.getUploadStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("上传阶段成功，重跑次数为0", afterJobStatus.getUploadStatus().getAgainNum(), is(0));

			assertThat("加载阶段成功", afterJobStatus.getDataLodingStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("加载阶段成功，重跑次数为0", afterJobStatus.getDataLodingStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("加载阶段成功，重跑次数为0", afterJobStatus.getDataLodingStatus().getAgainNum(), is(0));

			assertThat("计算增量阶段成功", afterJobStatus.getCalIncrementStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("计算增量阶段成功，重跑次数为0", afterJobStatus.getCalIncrementStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("计算增量阶段成功，重跑次数为0", afterJobStatus.getCalIncrementStatus().getAgainNum(), is(0));

			assertThat("数据登记阶段成功", afterJobStatus.getDataRegistrationStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("数据登记阶段成功，重跑次数为1", afterJobStatus.getDataRegistrationStatus().getIsAgain(), is(IsFlag.Shi.getCode()));
			assertThat("数据登记阶段成功，重跑次数为1", afterJobStatus.getDataRegistrationStatus().getAgainNum(), is(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除状态文件
	 * 构建JobStatusInfo对象，模拟执行卸数阶段失败，应该直接返回，并且从状态文件中断言文件内容是否正确
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void runJobTestFive(){
		JobStatusInfo jobStatusInfo = JobStatusInfoUtil.getStartJobStatusInfo(STATUS_FILE_PATH, TABLE_ID);
		//卸数失败
		JobStageInterface unloadDataFailed = new DBUnloadDataStageImplFailed();
		//上传成功
		JobStageInterface upload = new DBUploadStageImplSucceed();
		//加载成功
		JobStageInterface dataLoading = new DBDataLoadingStageImplSucceed();
		//增量成功
		JobStageInterface calIncrement = new DBCalIncrementStageImplSucceed();
		//登记成功
		JobStageInterface dataRegistration = new DBDataRegistrationStageImplSucceed();
		//利用JobStageController构建本次数据库直连采集作业流程
		JobStageController controller = new JobStageController();
		//构建责任链
		controller.registerJobStage(unloadDataFailed, upload, dataLoading, calIncrement, dataRegistration);
		//按顺序执行责任链
		try {
			controller.handleStageByOrder(STATUS_FILE_PATH, jobStatusInfo);
			//执行完毕后读取文件
			JobStatusInfo afterJobStatus = JSONObject.parseObject(FileUtil.readFile2String(new File(STATUS_FILE_PATH)), JobStatusInfo.class);
			assertThat("卸数阶段失败", afterJobStatus.getUnloadDataStatus().getStatusCode(), is(RunStatusConstant.FAILED.getCode()));
			assertThat("由于卸数阶段失败，直接返回，所以上传阶段没有执行", afterJobStatus.getUploadStatus() == null, is(true));
			assertThat("由于卸数阶段失败，直接返回，所以加载阶段没有执行", afterJobStatus.getDataLodingStatus() == null, is(true));
			assertThat("由于卸数阶段失败，直接返回，所以计算增量阶段没有执行", afterJobStatus.getCalIncrementStatus() == null, is(true));
			assertThat("由于卸数阶段失败，直接返回，所以数据登记阶段没有执行", afterJobStatus.getDataRegistrationStatus() == null, is(true));
		} catch (Exception e) {
			e.printStackTrace();
		}

		//失败后，重新构建责任链，读取已生成的状态文件，从卸数阶段开始重跑采集任务
		try {
			buildChainForCollection();

			JobStatusInfo afterJobStatus = JSONObject.parseObject(FileUtil.readFile2String(new File(STATUS_FILE_PATH)), JobStatusInfo.class);
			assertThat("卸数阶段成功", afterJobStatus.getUnloadDataStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("卸数阶段成功，重跑次数为1", afterJobStatus.getUnloadDataStatus().getIsAgain(), is(IsFlag.Shi.getCode()));
			assertThat("卸数阶段成功，重跑次数为1", afterJobStatus.getUnloadDataStatus().getAgainNum(), is(1));

			assertThat("上传阶段成功", afterJobStatus.getUploadStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("上传阶段成功，重跑次数为0", afterJobStatus.getUploadStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("上传阶段成功，重跑次数为0", afterJobStatus.getUploadStatus().getAgainNum(), is(0));

			assertThat("加载阶段成功", afterJobStatus.getDataLodingStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("加载阶段成功，重跑次数为0", afterJobStatus.getDataLodingStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("加载阶段成功，重跑次数为0", afterJobStatus.getDataLodingStatus().getAgainNum(), is(0));

			assertThat("计算增量阶段成功", afterJobStatus.getCalIncrementStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("计算增量阶段成功，重跑次数为0", afterJobStatus.getCalIncrementStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("计算增量阶段成功，重跑次数为0", afterJobStatus.getCalIncrementStatus().getAgainNum(), is(0));

			assertThat("数据登记阶段成功", afterJobStatus.getDataRegistrationStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("数据登记阶段成功，重跑次数为0", afterJobStatus.getDataRegistrationStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("数据登记阶段成功，重跑次数为0", afterJobStatus.getDataRegistrationStatus().getAgainNum(), is(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除状态文件
	 * 构建JobStatusInfo对象，模拟执行加载阶段失败，应该直接返回，并且从状态文件中断言文件内容是否正确
	 *
	 * @Param: 无
	 * @return: 无
	 *
	 * */
	@Test
	public void runJobTestSix(){
		JobStatusInfo jobStatusInfo = JobStatusInfoUtil.getStartJobStatusInfo(STATUS_FILE_PATH, TABLE_ID);
		//卸数成功
		JobStageInterface unloadData = new DBUnloadDataStageImplSucceed();
		//上传成功
		JobStageInterface upload = new DBUploadStageImplSucceed();
		//加载失败
		JobStageInterface dataLoadingFailed = new DBDataLoadingStageImplFailed();
		//增量成功
		JobStageInterface calIncrement = new DBCalIncrementStageImplSucceed();
		//登记成功
		JobStageInterface dataRegistration = new DBDataRegistrationStageImplSucceed();
		//利用JobStageController构建本次数据库直连采集作业流程
		JobStageController controller = new JobStageController();
		//构建责任链
		controller.registerJobStage(unloadData, upload, dataLoadingFailed, calIncrement, dataRegistration);
		//按顺序执行责任链
		try {
			controller.handleStageByOrder(STATUS_FILE_PATH, jobStatusInfo);
			//执行完毕后读取文件
			JobStatusInfo afterJobStatus = JSONObject.parseObject(FileUtil.readFile2String(new File(STATUS_FILE_PATH)), JobStatusInfo.class);
			assertThat("卸数阶段成功", afterJobStatus.getUnloadDataStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("上传阶段成功", afterJobStatus.getUploadStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("加载阶段失败", afterJobStatus.getDataLodingStatus().getStatusCode(), is(RunStatusConstant.FAILED.getCode()));
			assertThat("由于加载阶段失败，直接返回，所以计算增量阶段没有执行", afterJobStatus.getCalIncrementStatus() == null, is(true));
			assertThat("由于加载阶段失败，直接返回，所以数据登记阶段没有执行", afterJobStatus.getDataRegistrationStatus() == null, is(true));
		} catch (Exception e) {
			e.printStackTrace();
		}

		//失败后，重新构建责任链，读取已生成的状态文件，从加载阶段开始重跑采集任务
		try {
			buildChainForCollection();

			JobStatusInfo afterJobStatus = JSONObject.parseObject(FileUtil.readFile2String(new File(STATUS_FILE_PATH)), JobStatusInfo.class);
			assertThat("卸数阶段成功", afterJobStatus.getUnloadDataStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("卸数阶段成功，重跑次数为0", afterJobStatus.getUnloadDataStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("卸数阶段成功，重跑次数为0", afterJobStatus.getUnloadDataStatus().getAgainNum(), is(0));

			assertThat("上传阶段成功", afterJobStatus.getUploadStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("上传阶段成功，重跑次数为0", afterJobStatus.getUploadStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("上传阶段成功，重跑次数为0", afterJobStatus.getUploadStatus().getAgainNum(), is(0));

			assertThat("加载阶段成功", afterJobStatus.getDataLodingStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("加载阶段成功，重跑次数为1", afterJobStatus.getDataLodingStatus().getIsAgain(), is(IsFlag.Shi.getCode()));
			assertThat("加载阶段成功，重跑次数为1", afterJobStatus.getDataLodingStatus().getAgainNum(), is(1));

			assertThat("计算增量阶段成功", afterJobStatus.getCalIncrementStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("计算增量阶段成功，重跑次数为0", afterJobStatus.getCalIncrementStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("计算增量阶段成功，重跑次数为0", afterJobStatus.getCalIncrementStatus().getAgainNum(), is(0));

			assertThat("数据登记阶段成功", afterJobStatus.getDataRegistrationStatus().getStatusCode(), is(RunStatusConstant.SUCCEED.getCode()));
			assertThat("数据登记阶段成功，重跑次数为0", afterJobStatus.getDataRegistrationStatus().getIsAgain(), is(IsFlag.Fou.getCode()));
			assertThat("数据登记阶段成功，重跑次数为0", afterJobStatus.getDataRegistrationStatus().getAgainNum(), is(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private StageStatusInfo buildSucceedUnloadStatus(){
		StageStatusInfo unloadDataStatus = new StageStatusInfo();
		unloadDataStatus.setStatusCode(RunStatusConstant.SUCCEED.getCode());
		unloadDataStatus.setJobId(TABLE_ID);
		unloadDataStatus.setStartDate(DateUtil.getSysDate());
		unloadDataStatus.setEndDate(DateUtil.getSysDate());
		unloadDataStatus.setStartTime(DateUtil.getSysTime());
		unloadDataStatus.setEndTime(DateUtil.getSysTime());
		unloadDataStatus.setStageNameCode(StageConstant.UNLOADDATA.getCode());
		unloadDataStatus.setMessage("卸数执行成功");

		return unloadDataStatus;
	}

	private void buildChainForCollection() throws Exception{
		JobStatusInfo jobStatusInfo = JobStatusInfoUtil.getStartJobStatusInfo(STATUS_FILE_PATH, TABLE_ID);
		//卸数
		JobStageInterface unloadData = new DBUnloadDataStageImplSucceed();
		//上传
		JobStageInterface upload = new DBUploadStageImplSucceed();
		//加载
		JobStageInterface dataLoading = new DBDataLoadingStageImplSucceed();
		//增量
		JobStageInterface calIncrement = new DBCalIncrementStageImplSucceed();
		//登记
		JobStageInterface dataRegistration = new DBDataRegistrationStageImplSucceed();
		//利用JobStageController构建本次数据库直连采集作业流程
		JobStageController controller = new JobStageController();
		//构建责任链
		controller.registerJobStage(unloadData, upload, dataLoading, calIncrement, dataRegistration);
		//按顺序执行责任链
		controller.handleStageByOrder(STATUS_FILE_PATH, jobStatusInfo);
	}
}
