package hrds.agent.job.biz.core.increasement;

/**
 * Increasement
 * date: 2020/5/22 11:16
 * author: zxz
 */
public interface Increasement {
	/**
	 * 比较出所有的增量数据入增量表
	 */
	void calculateIncrement() throws Exception;

	/**
	 * 根据临时增量表合并出新的增量表，删除以前的增量表
	 */
	void mergeIncrement() throws Exception;

	/**
	 * 追加
	 */
	void append();

	/**
	 * 替换
	 */
	void replace();

}
