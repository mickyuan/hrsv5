package hrds.agent.job.biz.bean;

public class AvroBean {

	private String uuid;
	private String file_name; //文件名
	private String file_scr_path; //源路径
	private String file_size; //文件大小
	private String file_time; //时间
	private String file_summary; //摘要
	private String file_text; // 文本信息
	private String file_md5; // md5值
	private String file_avro_path; // hdfs中的avro路径
	private String file_avro_block; // avro文件的块号
	private String is_big_file; // 区分是否是大小文件
	private String is_increasement; // 区分是否是大小文件
	private String is_cache; // 区分是否是大小文件

	/**
	 * @return the is_increasement
	 */
	public String getIs_increasement() {
		return is_increasement;
	}


	/**
	 * @param is_increasement the is_increasement to set
	 */
	public void setIs_increasement(String is_increasement) {
		this.is_increasement = is_increasement;
	}

	//不包含文件流

	/**
	 * @return the file_name
	 */
	public String getFile_name() {

		return file_name;
	}

	/**
	 * @param file_name the file_name to set
	 */
	public void setFile_name(String file_name) {

		this.file_name = file_name;
	}

	/**
	 * @return the file_scr_path
	 */
	public String getFile_scr_path() {

		return file_scr_path;
	}

	/**
	 * @param file_scr_path the file_scr_path to set
	 */
	public void setFile_scr_path(String file_scr_path) {

		this.file_scr_path = file_scr_path;
	}

	/**
	 * @return the file_size
	 */
	public String getFile_size() {

		return file_size;
	}

	/**
	 * @param file_size the file_size to set
	 */
	public void setFile_size(String file_size) {

		this.file_size = file_size;
	}

	/**
	 * @return the file_time
	 */
	public String getFile_time() {

		return file_time;
	}

	/**
	 * @param file_time the file_time to set
	 */
	public void setFile_time(String file_time) {

		this.file_time = file_time;
	}

	/**
	 * @return the file_summary
	 */
	public String getFile_summary() {

		return file_summary;
	}

	/**
	 * @param file_summary the file_summary to set
	 */
	public void setFile_summary(String file_summary) {

		this.file_summary = file_summary;
	}

	/**
	 * @return the file_text
	 */
	public String getFile_text() {

		return file_text;
	}

	/**
	 * @param file_text the file_text to set
	 */
	public void setFile_text(String file_text) {

		this.file_text = file_text;
	}

	/**
	 * @return the file_md5
	 */
	public String getFile_md5() {

		return file_md5;
	}

	/**
	 * @param file_md5 the file_md5 to set
	 */
	public void setFile_md5(String file_md5) {

		this.file_md5 = file_md5;
	}

	/**
	 * @return the file_avro_path
	 */
	public String getFile_avro_path() {

		return file_avro_path;
	}

	/**
	 * @param file_avro_path the file_avro_path to set
	 */
	public void setFile_avro_path(String file_avro_path) {

		this.file_avro_path = file_avro_path;
	}

	/**
	 * @return the file_avro_block
	 */
	public String getFile_avro_block() {

		return file_avro_block;
	}

	/**
	 * @param file_avro_block the file_avro_block to set
	 */
	public void setFile_avro_block(String file_avro_block) {

		this.file_avro_block = file_avro_block;
	}

	/**
	 * @return the is_big_file
	 */
	public String getIs_big_file() {

		return is_big_file;
	}

	/**
	 * @param is_big_file the is_big_file to set
	 */
	public void setIs_big_file(String is_big_file) {

		this.is_big_file = is_big_file;
	}


	/**
	 * @return the uuid
	 */
	public String getUuid() {

		return uuid;
	}


	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {

		this.uuid = uuid;
	}


	/**
	 * @return the is_cache
	 */
	public String getIs_cache() {

		return is_cache;
	}


	/**
	 * @param is_cache the is_cache to set
	 */
	public void setIs_cache(String is_cache) {

		this.is_cache = is_cache;
	}


}
