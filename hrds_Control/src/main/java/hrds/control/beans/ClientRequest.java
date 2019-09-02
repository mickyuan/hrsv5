package hrds.control.beans;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ClientRequest {
	//任务编号
	private String taskId;
	//作业编号
	private String jobId;
	private String runType;
	private String serverType;
	private String stbid;
	private String ipaddress;
	private Date ontime;
	private String username;
	private String usertoken;
	private String userId;
	private String cmstoken;
	
	public String getTaskId() {
	
		return taskId;
	}
	
	public void setTaskId(String taskId) {
	
		this.taskId = taskId;
	}
	
	public String getJobId() {
	
		return jobId;
	}
	
	public void setJobId(String jobId) {
	
		this.jobId = jobId;
	}
	
	public String getRunType() {
	
		return runType;
	}
	
	public void setRunType(String runType) {
	
		this.runType = runType;
	}
	
	public String getServerType() {
	
		return serverType;
	}
	
	public void setServerType(String serverType) {
	
		this.serverType = serverType;
	}
	
	public String getStbid() {
	
		return stbid;
	}
	
	public void setStbid(String stbid) {
	
		this.stbid = stbid;
	}
	
	public String getIpaddress() {
	
		return ipaddress;
	}
	
	public void setIpaddress(String ipaddress) {
	
		this.ipaddress = ipaddress;
	}
	
	public Date getOntime() {
	
		return ontime;
	}
	
	public void setOntime(Date ontime) {
	
		this.ontime = ontime;
	}
	
	public String getUsername() {
	
		return username;
	}
	
	public void setUsername(String username) {
	
		this.username = username;
	}
	
	public String getUsertoken() {
	
		return usertoken;
	}
	
	public void setUsertoken(String usertoken) {
	
		this.usertoken = usertoken;
	}
	
	public String getUserId() {
	
		return userId;
	}
	
	public void setUserId(String userId) {
	
		this.userId = userId;
	}
	
	public String getCmstoken() {
	
		return cmstoken;
	}
	
	public void setCmstoken(String cmstoken) {
	
		this.cmstoken = cmstoken;
	}
}
