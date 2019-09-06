package hrds.agent.job.biz.bean;

import java.io.Serializable;

public class AllCleanResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private String is_all_repeat_result;
	private String clean_order;
	private String is_all_fille_result;
	private String isAllTrimResult;

	public String getIs_all_repeat_result() {
		return is_all_repeat_result;
	}

	public void setIs_all_repeat_result(String is_all_repeat_result) {
		this.is_all_repeat_result = is_all_repeat_result;
	}

	public String getClean_order() {
		return clean_order;
	}

	public void setClean_order(String clean_order) {
		this.clean_order = clean_order;
	}

	public String getIs_all_fille_result() {
		return is_all_fille_result;
	}

	public void setIs_all_fille_result(String is_all_fille_result) {
		this.is_all_fille_result = is_all_fille_result;
	}

	public String getIsAllTrimResult() {
		return isAllTrimResult;
	}

	public void setIsAllTrimResult(String isAllTrimResult) {
		this.isAllTrimResult = isAllTrimResult;
	}

	@Override
	public String toString() {
		return "AllCleanResult [is_all_repeat_result=" + is_all_repeat_result + ", clean_order=" + clean_order
				+ ", is_all_fille_result=" + is_all_fille_result + ", isAllTrimResult=" + isAllTrimResult + "]";
	}
}
