package hrds.commons.utils.xlstoxml.util;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.StorageType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SignalParser {

	private String filename;
	private Long filesize;
	private Long rowcount;
	private String createdatetime;
	private String sql;
	private Long rowlength;
	private Long columncount;
	private String storagetype;
	private List<JSONObject> columndescription = new ArrayList<JSONObject>();

	public SignalParser(String signal_path) throws Exception {
		parse(signal_path);
	}

	public void parse(String signal_path) throws Exception {

		BufferedReader reader = new BufferedReader(new FileReader(signal_path));

		String str = null;
		while ((str = reader.readLine()) != null) {
			if (!str.contains("=")) {
				continue;
			}
			if (!StringUtil.isEmpty(str)) {
				String[] split = str.split("=");
				if ("FILENAME".equals(split[0])) {
					filename = split[1];
				} else if ("FILESIZE".equals(split[0])) {
					filesize = Long.parseLong(split[1]);
				} else if ("ROWCOUNT".equals(split[0])) {
					rowcount = Long.parseLong(split[1]);
				} else if ("CREATEDATETIME".equals(split[0])) {
					createdatetime = split[1];
				} else if ("SQL".equals(split[0])) {
					sql = split[1];
				} else if ("ROWLENGTH".equals(split[0])) {
					rowlength = Long.parseLong(split[1]);
				} else if ("COLUMNCOUNT".equals(split[0])) {
					columncount = Long.parseLong(split[1]);
				} else if ("STORAGETYPE".equals(split[0])) {
					storagetype = split[1];
				} else if ("COLUMNDESCRIPTION".equals(split[0])) {
					if (split.length > 1) {
						if (split[1].contains("$$")) {
							addDescription(split[1]);
						}
					}
					String column = reader.readLine();
					while (!StringUtil.isEmpty(column)) {
						addDescription(column);
						column = reader.readLine();
					}
				}
			}
		}

		reader.close();
	}

	private void addDescription(String column) {

		List<String> info = StringUtil.split(column, "$$");
		JSONObject col = new JSONObject();
		col.put("index", info.get(0));
		col.put("name", info.get(1));
		col.put("type", SignalParser.getType(info.get(2)));
		String substring = info.get(3);
		String[] sub = substring.replace("(", "").replace(")", "").split(",");
		col.put("start", sub[0]);
		col.put("end", sub[1]);
		columndescription.add(col);
	}

	private static String getType(String type) {

		String res = "";
		String num = "";
		String str = "";

		int index = type.indexOf("(");
		if (index != -1) {
			str = type.substring(0, index);
			num = type.substring(index + 1, type.length() - 1);
			if (str.equalsIgnoreCase("char")) {
				res = "character";
			} else {
				res = str.toLowerCase();
			}
			res += "(" + num + ")";
		} else {
			res = type.toLowerCase();
			if (res.equals("int")) {
				res = "integer";
			}
		}

		return res;
	}

	public String getStoragetype() {

		if (StringUtil.isEmpty(storagetype)) {
			return StorageType.QuanLiang.getCode();
		}

		return storagetype;
	}

	public String getFilename() {

		return filename;
	}

	public Long getFilesize() {

		return filesize;
	}

	public Long getRowcount() {

		return rowcount;
	}

	public String getCreatedatetime() {

		return createdatetime;
	}

	public String getSql() {

		return sql;
	}

	public Long getRowlength() {

		return rowlength;
	}

	public Long getColumncount() {

		return columncount;
	}

	public List<JSONObject> getColumndescription() {

		return columndescription;
	}

}
