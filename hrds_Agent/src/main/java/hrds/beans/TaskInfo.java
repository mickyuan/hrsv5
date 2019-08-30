package hrds.beans;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TaskInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	//任务编号
	private String taskId;
	private String taskPath;
    private List<JobInfo> groupcontent;
    private DBConfigBean database_param;
    private String crontime;
    private String jobstartdate;
    private String jobstarttime;
    private JobParamBean job_param;
    private String is_parser;
    private String groupcount;
    private AllCleanResult all_clean_result;
    private String signal_file;
    private String jobenddate;

    
	public String getTaskId() {
	
		return taskId;
	}
	
	public void setTaskId(String taskId) {
	
		this.taskId = taskId;
	}
	
	
	public String getTaskPath() {
	
		return taskPath;
	}
	
	public void setTaskPath(String taskPath) {
	
		this.taskPath = taskPath;
	}

	public List<JobInfo> getGroupcontent() {
        return groupcontent;
    }

    public void setGroupcontent(List<JobInfo> groupcontent) {
        this.groupcontent = groupcontent;
    }

    public DBConfigBean getDatabase_param() {
        return database_param;
    }

    public void setDatabase_param(DBConfigBean database_param) {
        this.database_param = database_param;
    }

    public String getCrontime() {
        return crontime;
    }

    public void setCrontime(String crontime) {
        this.crontime = crontime;
    }

    public String getJobstartdate() {
        return jobstartdate;
    }

    public void setJobstartdate(String jobstartdate) {
        this.jobstartdate = jobstartdate;
    }

    public String getJobstarttime() {
        return jobstarttime;
    }
    public void setJobstarttime(String jobstarttime) {
        this.jobstarttime = jobstarttime;
    }

    public JobParamBean getJob_param() {
        return job_param;
    }

    public void setJob_param(JobParamBean job_param) {
        this.job_param = job_param;
    }

    public String getIs_parser() {
        return is_parser;
    }

    public void setIs_parser(String is_parser) {
        this.is_parser = is_parser;
    }

    public String getGroupcount() {
        return groupcount;
    }

    public void setGroupcount(String groupcount) {
        this.groupcount = groupcount;
    }

    public AllCleanResult getAll_clean_result() {
        return all_clean_result;
    }

    public void setAll_clean_result(AllCleanResult all_clean_result) {
        this.all_clean_result = all_clean_result;
    }

    public String getSignal_file() {
        return signal_file;
    }

    public void setSignal_file(String signal_file) {
        this.signal_file = signal_file;
    }

    public String getJobenddate() {
        return jobenddate;
    }

    public void setJobenddate(String jobenddate) {
        this.jobenddate = jobenddate;
    }

    @Override
    public String toString() {
        return "TaskBean [groupcontent=" + groupcontent + ", database_param=" + database_param + ", crontime="
                + crontime + ", jobstartdate=" + jobstartdate + ", job_param=" + job_param + ", is_parser=" + is_parser
                + ", groupcount=" + groupcount + ", all_clean_result=" + all_clean_result + ", signal_file="
                + signal_file + ", jobenddate=" + jobenddate + "]";
    }
}
