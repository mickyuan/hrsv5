package hrds.commons.hadoop.hadoop_helper;

import hrds.commons.hadoop.readconfig.ConfigReader;
import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Used by the book examples to generate tables and fill them with test data.
 */
public class HBaseHelper implements Closeable {

	private static final Log logger = LogFactory.getLog(HBaseHelper.class);

	private Configuration configuration = null;

	private Connection connection = null;

	private Admin admin = null;

	private Table table = null;

	static {
		System.setProperty("HADOOP_USER_NAME", PropertyParaValue.getString("HADOOP_USER_NAME", "hyshf"));
	}

	protected HBaseHelper(Configuration configuration) throws IOException {

		this.configuration = configuration;
		this.connection = ConnectionFactory.createConnection(configuration);
		this.admin = connection.getAdmin();
	}

	public static HBaseHelper getHelper(Configuration configuration) throws IOException {

		return new HBaseHelper(configuration);
	}

	public static HBaseHelper getHelper() throws IOException {

		return new HBaseHelper(ConfigReader.getConfiguration());
	}

	public static HBaseHelper getHelper(String configPath) throws IOException {

		return new HBaseHelper(ConfigReader.getConfiguration(configPath));
	}

	@Override
	public void close() throws IOException {

		if (table != null)
			table.close();
		if (admin != null)
			admin.close();
		if (connection != null)
			connection.close();
	}

	public Connection getConnection() {

		return connection;
	}

	public Configuration getConfiguration() {

		return configuration;
	}

	public Table getTable(String tableName) throws IOException {

		return connection.getTable(TableName.valueOf(tableName));
	}

	public Table getTable() {

		if (table == null) {
			logger.info("Table is null");
			return null;
		}
		return table;
	}

	public void setTable(String tableName) throws IOException {

		if (!existsTable(tableName)) {
			throw new TableNotFoundException("Table " + tableName + "doesn't exist.");
		}
		table = connection.getTable(TableName.valueOf(tableName));
	}

	public Admin getAdmin() {

		return admin;
	}

	public void flush(TableName tableName) throws IOException {

		admin.flush(tableName);
	}

	public void truncateTable(String tableName, boolean preserveSplits) throws IOException {

		if (!admin.isTableDisabled(TableName.valueOf(tableName))) {
			admin.disableTable(TableName.valueOf(tableName));
		}
		admin.truncateTable(TableName.valueOf(tableName), preserveSplits);
	}

	public void createNamespace(String namespace) throws IOException {

		NamespaceDescriptor nd = NamespaceDescriptor.create(namespace).build();
		admin.createNamespace(nd);
	}

	public void dropNamespace(String namespace, boolean force) throws IOException {

		if (force) {
			TableName[] tableNames = admin.listTableNamesByNamespace(namespace);
			for (TableName name : tableNames) {
				admin.disableTable(name);
				admin.deleteTable(name);
			}
		}
		admin.deleteNamespace(namespace);
	}

	public boolean existsTable(String table) throws IOException {

		return existsTable(TableName.valueOf(table));
	}

	public boolean existsTable(TableName table) throws IOException {

		return admin.tableExists(table);
	}

	public void createTableWithNamespace(String namespace, String table, String... colfams) throws IOException {

		createTable(TableName.valueOf(namespace + ":" + table), 1, null, true, colfams);
	}

	public void createTable(String table, String... colfams) throws IOException {

		createTable(TableName.valueOf(table), 1, null, true, colfams);
	}

	public void createTable(TableName table, String... colfams) throws IOException {

		createTable(table, 1, null, true, colfams);
	}

	public void createTable(String table, int maxVersions, String... colfams) throws IOException {

		createTable(TableName.valueOf(table), maxVersions, null, true, colfams);
	}

	public void createTable(TableName table, int maxVersions, String... colfams) throws IOException {

		createTable(table, maxVersions, null, true, colfams);
	}

	public void createTable(String table, byte[][] splitKeys, String... colfams) throws IOException {

		createTable(TableName.valueOf(table), 1, splitKeys, true, colfams);
	}

	public void createTable(String table, byte[][] splitKeys, boolean snappycompress, String... colfams) throws IOException {

		createTable(TableName.valueOf(table), 1, splitKeys, snappycompress, colfams);
	}

	public void createTable(TableName table, int maxVersions, byte[][] splitKeys, boolean compress, String... colfams) throws IOException {

		createTable(table, maxVersions, splitKeys, compress, 0, colfams);
	}

	public void createTable(TableName table, int maxVersions, byte[][] splitKeys, boolean compress, int scope, String... colfams) throws IOException {

		HTableDescriptor desc = new HTableDescriptor(table);
		for (String cf : colfams) {
			HColumnDescriptor coldef = new HColumnDescriptor(cf);
			coldef.setMaxVersions(maxVersions);
			if (compress) {
				coldef.setCompressionType(Algorithm.SNAPPY);
			}
			coldef.setScope(scope);
			desc.addFamily(coldef);
		}
		if (splitKeys != null) {
			admin.createTable(desc, splitKeys);
		} else {
			admin.createTable(desc);
		}
	}

	public void disableTable(String table) throws IOException {

		disableTable(TableName.valueOf(table));
	}

	public void disableTable(TableName table) throws IOException {

		admin.disableTable(table);
	}

	public void dropTable(String table) throws IOException {

		dropTable(TableName.valueOf(table));
	}

	public void dropTable(TableName table) throws IOException {

		if (existsTable(table)) {
			if (admin.isTableEnabled(table))
				disableTable(table);
			admin.deleteTable(table);
		}
	}

	public void put(String table, String row, String fam, String qual, String val) throws IOException {

		put(TableName.valueOf(table), row, fam, qual, val);
	}

	public void put(TableName table, String row, String fam, String qual, String val) throws IOException {

		Table tbl = connection.getTable(table);
		Put put = new Put(Bytes.toBytes(row));
		put.addColumn(Bytes.toBytes(fam), Bytes.toBytes(qual), Bytes.toBytes(val));
		tbl.put(put);
		tbl.close();
	}

	public void put(String table, String row, String fam, String qual, long ts, String val) throws IOException {

		put(TableName.valueOf(table), row, fam, qual, ts, val);
	}

	public void put(TableName table, String row, String fam, String qual, long ts, String val) throws IOException {

		Table tbl = connection.getTable(table);
		Put put = new Put(Bytes.toBytes(row));
		put.addColumn(Bytes.toBytes(fam), Bytes.toBytes(qual), ts, Bytes.toBytes(val));
		tbl.put(put);
		tbl.close();
	}

	public void put(String table, String[] rows, String[] fams, String[] quals, long[] ts, String[] vals) throws IOException {

		put(TableName.valueOf(table), rows, fams, quals, ts, vals);
	}

	public void put(TableName table, String[] rows, String[] fams, String[] quals, long[] ts, String[] vals) throws IOException {

		Table tbl = connection.getTable(table);
		for (String row : rows) {
			Put put = new Put(Bytes.toBytes(row));
			for (String fam : fams) {
				int v = 0;
				for (String qual : quals) {
					String val = vals[v < vals.length ? v : vals.length - 1];
					long t = ts[v < ts.length ? v : ts.length - 1];
					logger.debug("Adding: " + row + " " + fam + " " + qual + " " + t + " " + val);
					put.addColumn(Bytes.toBytes(fam), Bytes.toBytes(qual), t, Bytes.toBytes(val));
					v++;
				}
			}
			tbl.put(put);
		}
		tbl.close();
	}

	public void dump(String table, String[] rows, String[] fams, String[] quals) throws IOException {

		dump(TableName.valueOf(table), rows, fams, quals);
	}

	public void dump(TableName table, String[] rows, String[] fams, String[] quals) throws IOException {

		Table tbl = connection.getTable(table);
		List<Get> gets = new ArrayList<Get>();
		for (String row : rows) {
			Get get = new Get(Bytes.toBytes(row));
			get.setMaxVersions();
			if (fams != null) {
				for (String fam : fams) {
					for (String qual : quals) {
						get.addColumn(Bytes.toBytes(fam), Bytes.toBytes(qual));
					}
				}
			}
			gets.add(get);
		}
		Result[] results = tbl.get(gets);
		for (Result result : results) {
			for (Cell cell : result.rawCells()) {
				logger.debug(
						"Cell: " + cell + ", Value: " + Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
			}
		}
		tbl.close();
	}

	public void dump(String table) throws IOException {

		dump(TableName.valueOf(table));
	}

	public void dump(TableName table) throws IOException {

		try (Table t = connection.getTable(table); ResultScanner scanner = t.getScanner(new Scan())) {
			for (Result result : scanner) {
				dumpResult(result);
			}
		}
	}

	public void dumpResult(Result result) {

		for (Cell cell : result.rawCells()) {
			logger.debug("Cell: " + cell + ", Value: " + Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
		}
	}

	public List<HBaseProtos.SnapshotDescription> getSnapshots() throws IOException {

		return admin.listSnapshots();
	}

	public void createSnapshot(String snapshotName, String tableName) throws IOException {

		admin.snapshot(snapshotName, TableName.valueOf(tableName));
		logger.info("create " + snapshotName + " successful!");
	}

	public void restoreSnapshot(String snapshotName, String tableName) throws IOException {

		admin.disableTable(TableName.valueOf(tableName));
		logger.info("restore ： " + tableName);
		admin.restoreSnapshot(snapshotName);
		admin.enableTable(TableName.valueOf(tableName));
		logger.info("restore table " + tableName + " successful via " + snapshotName);
	}

	public void deleteSnapshot(String snapshotName) throws IOException {

		admin.deleteSnapshot(snapshotName);
		logger.info("Delete snapshot " + snapshotName + "successful!");
	}

	public List<String> listNamespaceName() throws IOException {

		List<String> list = new ArrayList<>();
		NamespaceDescriptor[] listNamespaceDescriptors = admin.listNamespaceDescriptors();
		for (NamespaceDescriptor namespaceDescriptor : listNamespaceDescriptors) {
			String nameSpaceName = namespaceDescriptor.getName();
			list.add(nameSpaceName);
		}
		return list;
	}

	public Map<String, List<String>> listNameSpaceWithTableNames() throws IOException {

		Map<String, List<String>> map = new HashMap<>();
		List<String> nameSpaces = listNamespaceName();
		for (String nameSpace : nameSpaces) {
			List<String> tableNames = new ArrayList<>();
			TableName[] listTableNamesByNamespace = admin.listTableNamesByNamespace(nameSpace);
			for (TableName tableName : listTableNamesByNamespace) {
				String tableNameString = tableName.getNameAsString();
				//有可能类似于${namespace}:${tablename}，只取后者
				String[] split = StringUtils.split(tableNameString, ":");
				if (split.length > 1) {
					tableNames.add(split[1]);
				} else {
					tableNames.add(tableName.getNameAsString());
				}
			}
			map.put(nameSpace, tableNames);
		}
		return map;
	}

	public static void main(String[] args) throws IOException {

		System.setProperty("hadoop.home.dir", "C:\\HyrenCloud");
		Table table = null;
		try (HBaseHelper helper = HBaseHelper.getHelper()) {
			//			helper.setTable("test001");
			//			HPut put = new HPut("aaa".getBytes());
			//			put.addColumn("info".getBytes(), "d".getBytes(), "value".getBytes());
			//			HPut put1 = new HPut("aaa1".getBytes());
			//			put1.addColumn("info".getBytes(), "d".getBytes(), "value".getBytes());
			//			List<HPut> list = new ArrayList<>();
			//			list.add(put);
			//			list.add(put1);
			//			helper.put(list);
			//
			//			helper.flush(TableName.valueOf("test001"));
			System.out.println("---------------------");
			table = helper.getTable("test001");
			System.out.println("---------------------");
			table.close();
			System.out.println("---------------------");
			System.out.println(helper.getConnection().isClosed());
			System.out.println(helper.getConnection().isAborted());
			System.out.println("---------------------");

		} catch (Exception e) {
			logger.error(e);
		}
		System.out.println("---------------------");
		table.close();
		System.out.println("---------------------");
		System.out.println(table.getName());
	}
}
