package hrds.commons.codes;;																															
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
                                                
import java.lang.reflect.Method;
import java.util.*;
                                                                                       
public class CodesItem{                        
	private static final Log logger = LogFactory.getLog(CodesItem.class);                                                      
	/**补齐方式   */
	protected static final String fillingType=new String("55");
	/**数据申请类型   */
	protected static final String applyType=new String("57");
	/**权限类型   */
	protected static final String authType=new String("58");
	/**存储层类型   */
	protected static final String store_type=new String("59");
	/**文件类型   */
	protected static final String fileType=new String("60");
	/**用户类型   */
	protected static final String userType=new String("17");
	/**是否标识   */
	protected static final String isFlag=new String("18");
	/**用户状态   */
	protected static final String userState=new String("19");
	/**ETL作业类型   */
	protected static final String pro_Type=new String("20");
	/**用户优先级   */
	protected static final String userPriority=new String("21");
	/**ETL调度频率   */
	protected static final String dispatch_Frequency=new String("22");
	/**ETL调度类型   */
	protected static final String dispatch_Type=new String("23");
	/**ETl作业有效标志   */
	protected static final String job_Effective_Flag=new String("24");
	/**ETL作业状态   */
	protected static final String job_Status=new String("25");
	/**ETL当天调度标志   */
	protected static final String today_Dispatch_Flag=new String("26");
	/**ETL主服务器同步   */
	protected static final String main_Server_Sync=new String("27");
	/**ETL状态   */
	protected static final String status=new String("28");
	/**ETL干预类型   */
	protected static final String meddle_type=new String("29");
	/**ETL干预状态   */
	protected static final String meddle_status=new String("30");
	/**ETL变类型   */
	protected static final String paramType=new String("31");
	/**组件状态   */
	protected static final String compState=new String("32");
	/**组件类型   */
	protected static final String compType=new String("33");
	/**Agent状态   */
	protected static final String agentStatus=new String("34");
	/**Agent类别   */
	protected static final String agentType=new String("35");
	/**数据库类型   */
	protected static final String databaseType=new String("36");
	/**启动方式   */
	protected static final String executeWay=new String("37");
	/**压缩范围   */
	protected static final String reduceScope=new String("38");
	/**运行状态   */
	protected static final String executeState=new String("39");
	/**采集类型   */
	protected static final String collectType=new String("40");
	/**进数方式   */
	protected static final String storageType=new String("41");
	/**采集编码   */
	protected static final String dataBaseCode=new String("42");
	/**记录总数   */
	protected static final String countNum=new String("43");
	/**DB文件格式   */
	protected static final String fileFormat=new String("44");
	/**压缩格式   */
	protected static final String reduceType=new String("45");
	/**数据类型   */
	protected static final String collectDataType=new String("46");
	/**对象采集方式   */
	protected static final String objectCollectType=new String("47");
	/**对象数据类型   */
	protected static final String objectDataType=new String("48");
	/**ftp目录规则   */
	protected static final String ftpRule=new String("49");
	/**时间类型   */
	protected static final String timeType=new String("50");
	/**hive文件存储类型   */
	protected static final String hiveStorageType=new String("51");
	/**字符拆分方式   */
	protected static final String charSplitType=new String("52");
	/**数据抽取方式   */
	protected static final String dataExtractType=new String("53");
	/**清洗方式   */
	protected static final String cleanType=new String("54");


	public static final Map<String,Class> mapCat= new HashMap<String,Class>(100);
	static{
		mapCat.put(fillingType,FillingType.class);
		mapCat.put(applyType,ApplyType.class);
		mapCat.put(authType,AuthType.class);
		mapCat.put(store_type,store_type.class);
		mapCat.put(fileType,FileType.class);
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
		mapCat.put(hiveStorageType,HiveStorageType.class);
		mapCat.put(charSplitType,CharSplitType.class);
		mapCat.put(dataExtractType,DataExtractType.class);
		mapCat.put(cleanType,CleanType.class);
	}


}                                                                                                  


