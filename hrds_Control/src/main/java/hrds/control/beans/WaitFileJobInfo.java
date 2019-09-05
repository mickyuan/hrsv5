package hrds.control.beans;

public class WaitFileJobInfo {

	private String strJobName;
	private String strBathDate;
	private String waitFilePath;

	public String getStrJobName() {
		return strJobName;
	}

	public void setStrJobName(String strJobName) {
		this.strJobName = strJobName;
	}

	public String getStrBathDate() {
		return strBathDate;
	}

	public void setStrBathDate(String strCurrBath) {
		this.strBathDate = strCurrBath;
	}

	public String getWaitFilePath() {
		return waitFilePath;
	}

	public void setWaitFilePath(String waitFilePath) {
		this.waitFilePath = waitFilePath;
	}
}
