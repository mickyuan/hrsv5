package hrds.k.biz.algorithms.conf;

import hrds.commons.utils.Constant;

import java.io.Serializable;

public class AlgorithmsConf implements Serializable {

	private String inputFilePath = "";
	private String outputFilePath = "";
	private String driver = "";
	private String user = "";
	private String password = "";
	private String[] selectColumnArray = null;
	private String jdbcUrl = "";
	private String table_name = "";
	private String database_name = "";
	private String[] predicates = null;
	private String sys_code = "";

	private Boolean useParquet = false;
	private Boolean useCsv = false;
	private String inputFileSeparator = ",";
	private Boolean inputFileHasHeader = false;
	private Integer maxDepth = 3;
	private Integer numPartition = 56;
	private Integer batchSize = 56;
	private Integer validationBatchSize = 5000;
	private Integer jdbcLimit = 1500000;

	@Override
	public String toString() {
		return "Config:\r\n\t" +
				"inputFilePath: " + this.inputFilePath + "\r\n\t" +
				"outputFilePath: " + this.outputFilePath + "\r\n\t" +
				"useParquet: " + this.useParquet + "\r\n\t" +
				"inputFileSeparator: " + this.inputFileSeparator + "\r\n\t" +
				"inputFileHasHeader: " + this.inputFileHasHeader + "\r\n\t" +
				"maxDepth: " + this.maxDepth + "\r\n\t" +
				"numPartition: " + this.numPartition + "\r\n\t" +
				"batchSize: " + this.batchSize + "\r\n\t" +
				"validationBatchSize: " + this.validationBatchSize;
	}

	public static AlgorithmsConf getConf(String[] param) {
		AlgorithmsConf algorithmsConf = new AlgorithmsConf();
		int flag = 0;

		for (int i = 0; i < param.length; i++) {
			if (i % 2 == 0) {
				switch (param[i]) {
					case "--inputFilePath":
						algorithmsConf.inputFilePath = param[i + 1];
						flag += 1;
						break;
					case "--outputFilePath":
						algorithmsConf.outputFilePath = param[i + 1];
						flag += 1;
						break;
					case "--useParquet":
						algorithmsConf.useParquet = Boolean.valueOf(param[i + 1]);
						flag += 1;
						break;
					case "--inputFileSeparator":
						algorithmsConf.inputFileSeparator = param[i + 1];
						flag += 1;
						break;
					case "--inputFileHasHeader":
						algorithmsConf.inputFileHasHeader = Boolean.valueOf(param[i + 1]);
						flag += 1;
						break;
					case "--maxDepth":
						algorithmsConf.maxDepth = Integer.valueOf(param[i + 1]);
						flag += 1;
						break;
					case "--numPartition":
						algorithmsConf.numPartition = Integer.valueOf(param[i + 1]);
						flag += 1;
						break;
					case "--batchSize":
						algorithmsConf.batchSize = Integer.valueOf(param[i + 1]);
						flag += 1;
						break;
					case "--validationBatchSize":
						algorithmsConf.validationBatchSize = Integer.valueOf(param[i + 1]);
						flag += 1;
						break;
					case "--useCsv":
						algorithmsConf.useCsv = Boolean.valueOf(param[i + 1]);
						flag += 1;
						break;
					case "--jdbcLimit":
						algorithmsConf.jdbcLimit = Integer.valueOf(param[i + 1]);
						flag += 1;
						break;
					case "--driver":
						algorithmsConf.driver = param[i + 1];
						flag += 1;
						break;
					case "--user":
						algorithmsConf.user = param[i + 1];
						flag += 1;
						break;
					case "--password":
						algorithmsConf.password = param[i + 1];
						flag += 1;
						break;
					case "--selectColumnArray":
						algorithmsConf.selectColumnArray = param[i + 1].split(",");
						flag += 1;
						break;
					case "--jdbcUrl":
						algorithmsConf.jdbcUrl = param[i + 1];
						flag += 1;
						break;
					case "--table_name":
						algorithmsConf.table_name = param[i + 1];
						flag += 1;
						break;
					case "--database_name":
						algorithmsConf.database_name = param[i + 1];
						flag += 1;
						break;
					case "--predicates":
						algorithmsConf.predicates = param[i + 1].split(Constant.DATADELIMITER);
						flag += 1;
						break;
					case "--sys_code":
						algorithmsConf.sys_code = param[i + 1];
						flag += 1;
						break;
					default:
						throw new IllegalArgumentException("No Such Conf " + param[i]);
				}
			}
		}
		if (flag == 0) {
			throw new RuntimeException("Maybe you forget to set the outputFilePath");
		}
		return algorithmsConf;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getDatabase_name() {
		return database_name;
	}

	public void setDatabase_name(String database_name) {
		this.database_name = database_name;
	}

	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] getSelectColumnArray() {
		return selectColumnArray;
	}

	public void setSelectColumnArray(String[] selectColumnArray) {
		this.selectColumnArray = selectColumnArray;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public void setUseParquet(Boolean useParquet) {
		this.useParquet = useParquet;
	}

	public void setUseCsv(Boolean useCsv) {
		this.useCsv = useCsv;
	}

	public void setInputFileSeparator(String inputFileSeparator) {
		this.inputFileSeparator = inputFileSeparator;
	}

	public void setInputFileHasHeader(Boolean inputFileHasHeader) {
		this.inputFileHasHeader = inputFileHasHeader;
	}

	public void setMaxDepth(Integer maxDepth) {
		this.maxDepth = maxDepth;
	}

	public void setNumPartition(Integer numPartition) {
		this.numPartition = numPartition;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	public void setValidationBatchSize(Integer validationBatchSize) {
		this.validationBatchSize = validationBatchSize;
	}

	public Integer getJdbcLimit() {
		return jdbcLimit;
	}

	public void setJdbcLimit(Integer jdbcLimit) {
		this.jdbcLimit = jdbcLimit;
	}

	public String getInputFilePath() {
		return inputFilePath;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}

	public Boolean getUseParquet() {
		return useParquet;
	}

	public String getInputFileSeparator() {
		return inputFileSeparator;
	}

	public Boolean getInputFileHasHeader() {
		return inputFileHasHeader;
	}

	public Integer getMaxDepth() {
		return maxDepth;
	}

	public Integer getNumPartition() {
		return numPartition;
	}

	public Integer getBatchSize() {
		return batchSize;
	}

	public Integer getValidationBatchSize() {
		return validationBatchSize;
	}

	public Boolean getUseCsv() {
		return useCsv;
	}

	public String[] getPredicates() {
		return predicates;
	}

	public void setPredicates(String[] predicates) {
		this.predicates = predicates;
	}

	public String getSys_code() {
		return sys_code;
	}

	public void setSys_code(String sys_code) {
		this.sys_code = sys_code;
	}
}
