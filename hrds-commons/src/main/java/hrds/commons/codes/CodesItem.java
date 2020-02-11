package hrds.commons.codes;;																															
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
                                                
import java.lang.reflect.Method;
import java.util.*;
                                                                                       
public class CodesItem{                        
	private static final Log logger = LogFactory.getLog(CodesItem.class);                                                      
	/**数据申请类型   */
	protected static final String applyType=new String("ApplyType");
	/**权限类型   */
	protected static final String authType=new String("AuthType");
	/**存储层类型   */
	protected static final String store_type=new String("Store_type");
	/**文件类型   */
	protected static final String fileType=new String("FileType");
	/**存储层附件属性   */
	protected static final String storeLayerAdded=new String("StoreLayerAdded");
	/**数据源类型   */
	protected static final String dataSourceType=new String("DataSourceType");
	/**用户类型   */
	protected static final String userType=new String("UserType");
	/**是否标识   */
	protected static final String isFlag=new String("IsFlag");
	/**用户状态   */
	protected static final String userState=new String("UserState");
	/**ETL作业类型   */
	protected static final String pro_Type=new String("Pro_Type");
	/**用户优先级   */
	protected static final String userPriority=new String("UserPriority");
	/**ETL调度频率   */
	protected static final String dispatch_Frequency=new String("Dispatch_Frequency");
	/**ETL调度类型   */
	protected static final String dispatch_Type=new String("Dispatch_Type");
	/**ETl作业有效标志   */
	protected static final String job_Effective_Flag=new String("Job_Effective_Flag");
	/**ETL作业状态   */
	protected static final String job_Status=new String("Job_Status");
	/**ETL当天调度标志   */
	protected static final String today_Dispatch_Flag=new String("Today_Dispatch_Flag");
	/**ETL主服务器同步   */
	protected static final String main_Server_Sync=new String("Main_Server_Sync");
	/**ETL状态   */
	protected static final String status=new String("Status");
	/**ETL干预类型   */
	protected static final String meddle_type=new String("Meddle_type");
	/**ETL干预状态   */
	protected static final String meddle_status=new String("Meddle_status");
	/**ETL变类型   */
	protected static final String paramType=new String("ParamType");
	/**组件状态   */
	protected static final String compState=new String("CompState");
	/**组件类型   */
	protected static final String compType=new String("CompType");
	/**Agent状态   */
	protected static final String agentStatus=new String("AgentStatus");
	/**Agent类别   */
	protected static final String agentType=new String("AgentType");
	/**数据库类型   */
	protected static final String databaseType=new String("DatabaseType");
	/**启动方式   */
	protected static final String executeWay=new String("ExecuteWay");
	/**压缩范围   */
	protected static final String reduceScope=new String("ReduceScope");
	/**运行状态   */
	protected static final String executeState=new String("ExecuteState");
	/**采集类型   */
	protected static final String collectType=new String("CollectType");
	/**进数方式   */
	protected static final String storageType=new String("StorageType");
	/**采集编码   */
	protected static final String dataBaseCode=new String("DataBaseCode");
	/**记录总数   */
	protected static final String countNum=new String("CountNum");
	/**DB文件格式   */
	protected static final String fileFormat=new String("FileFormat");
	/**压缩格式   */
	protected static final String reduceType=new String("ReduceType");
	/**数据类型   */
	protected static final String collectDataType=new String("CollectDataType");
	/**对象采集方式   */
	protected static final String objectCollectType=new String("ObjectCollectType");
	/**对象数据类型   */
	protected static final String objectDataType=new String("ObjectDataType");
	/**ftp目录规则   */
	protected static final String ftpRule=new String("FtpRule");
	/**时间类型   */
	protected static final String timeType=new String("TimeType");
	/**字符拆分方式   */
	protected static final String charSplitType=new String("CharSplitType");
	/**数据抽取方式   */
	protected static final String dataExtractType=new String("DataExtractType");
	/**清洗方式   */
	protected static final String cleanType=new String("CleanType");
	/**补齐方式   */
	protected static final String fillingType=new String("FillingType");
	/**对标-数据类别   */
	protected static final String dbmDataType=new String("DbmDataType");


	public static final Map<String,Class> mapCat= new HashMap<String,Class>(100);
	static{
		mapCat.put(applyType,ApplyType.class);
		mapCat.put(authType,AuthType.class);
		mapCat.put(store_type,Store_type.class);
		mapCat.put(fileType,FileType.class);
		mapCat.put(storeLayerAdded,StoreLayerAdded.class);
		mapCat.put(dataSourceType,DataSourceType.class);
		mapCat.put(userType,UserType.class);
		mapCat.put(isFlag,IsFlag.class);
		mapCat.put(userState,UserState.class);
		mapCat.put(pro_Type,Pro_Type.class);
		mapCat.put(userPriority,UserPriority.class);
		mapCat.put(dispatch_Frequency,Dispatch_Frequency.class);
		mapCat.put(dispatch_Type,Dispatch_Type.class);
		mapCat.put(job_Effective_Flag,Job_Effective_Flag.class);
		mapCat.put(job_Status,Job_Status.class);
		mapCat.put(today_Dispatch_Flag,Today_Dispatch_Flag.class);
		mapCat.put(main_Server_Sync,Main_Server_Sync.class);
		mapCat.put(status,Status.class);
		mapCat.put(meddle_type,Meddle_type.class);
		mapCat.put(meddle_status,Meddle_status.class);
		mapCat.put(paramType,ParamType.class);
		mapCat.put(compState,CompState.class);
		mapCat.put(compType,CompType.class);
		mapCat.put(agentStatus,AgentStatus.class);
		mapCat.put(agentType,AgentType.class);
		mapCat.put(databaseType,DatabaseType.class);
		mapCat.put(executeWay,ExecuteWay.class);
		mapCat.put(reduceScope,ReduceScope.class);
		mapCat.put(executeState,ExecuteState.class);
		mapCat.put(collectType,CollectType.class);
		mapCat.put(storageType,StorageType.class);
		mapCat.put(dataBaseCode,DataBaseCode.class);
		mapCat.put(countNum,CountNum.class);
		mapCat.put(fileFormat,FileFormat.class);
		mapCat.put(reduceType,ReduceType.class);
		mapCat.put(collectDataType,CollectDataType.class);
		mapCat.put(objectCollectType,ObjectCollectType.class);
		mapCat.put(objectDataType,ObjectDataType.class);
		mapCat.put(ftpRule,FtpRule.class);
		mapCat.put(timeType,TimeType.class);
		mapCat.put(charSplitType,CharSplitType.class);
		mapCat.put(dataExtractType,DataExtractType.class);
		mapCat.put(cleanType,CleanType.class);
		mapCat.put(fillingType,FillingType.class);
		mapCat.put(dbmDataType,DbmDataType.class);
	}


}                                                                                                  


