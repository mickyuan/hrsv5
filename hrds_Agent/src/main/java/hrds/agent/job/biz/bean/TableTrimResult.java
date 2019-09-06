package hrds.agent.job.biz.bean;

import java.io.Serializable;

public class TableTrimResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private String clean_type;

	public String getClean_type() {
		return clean_type;
	}

	public void setClean_type(String clean_type) {
		this.clean_type = clean_type;
	}

	@Override
	public String toString() {
		return "TableTrimResult [clean_type=" + clean_type + "]";
	}
}
