package hrds.commons.collection;

import fd.ng.core.utils.DateUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.AgentType;
import hrds.commons.utils.StorageTypeKey;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.collection.bean.LayerTypeBean;
import hrds.commons.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

public class ProcessingDataTest {
	public Long idL1 = 1999998876L;
	private String idS1 = String.valueOf(idL1);

	public Long idL2 = 1999998879L;
	private String idS2 = String.valueOf(idL2);

	public Long idL3 = 2999998879L;
	private String idS3 = String.valueOf(idL3);

	private static final Logger logger = LogManager.getLogger();
	@Before
	public void insertData() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Data_store_reg dst = new Data_store_reg();
			dst.setFile_id(idS1);
			dst.setCollect_type(AgentType.DBWenJian.getCode());
			dst.setOriginal_update_date(DateUtil.getSysDate());
			dst.setOriginal_update_time(DateUtil.getSysTime());
			dst.setOriginal_name("sys_user");
			dst.setTable_name("sys_user");
			dst.setHyren_name("sys_user");
			dst.setStorage_date(DateUtil.getSysDate());
			dst.setStorage_time(DateUtil.getSysTime());
			dst.setFile_size(20L);
			dst.setAgent_id(idL1);
			dst.setSource_id(idL1);
			dst.setDatabase_id(idL1);
			dst.setTable_id(idL1);
			int add = dst.add(db);

			dst.setFile_id(idS3);
			dst.setCollect_type(AgentType.DBWenJian.getCode());
			dst.setOriginal_update_date(DateUtil.getSysDate());
			dst.setOriginal_update_time(DateUtil.getSysTime());
			dst.setOriginal_name("xc_CODE_INFO");
			dst.setTable_name("xc_CODE_INFO");
			dst.setHyren_name("xc_CODE_INFO");
			dst.setStorage_date(DateUtil.getSysDate());
			dst.setStorage_time(DateUtil.getSysTime());
			dst.setFile_size(20L);
			dst.setAgent_id(idL3);
			dst.setSource_id(idL3);
			dst.setDatabase_id(idL3);
			dst.setTable_id(idL3);
			add = dst.add(db);


			dst.setFile_id(idS2);
			dst.setCollect_type(AgentType.DBWenJian.getCode());
			dst.setOriginal_update_date(DateUtil.getSysDate());
			dst.setOriginal_update_time(DateUtil.getSysTime());
			dst.setOriginal_name("sys_role");
			dst.setTable_name("sys_role");
			dst.setHyren_name("sys_role");
			dst.setStorage_date(DateUtil.getSysDate());
			dst.setStorage_time(DateUtil.getSysTime());
			dst.setFile_size(20L);
			dst.setAgent_id(idL2);
			dst.setSource_id(idL2);
			dst.setDatabase_id(idL2);
			dst.setTable_id(idL2);
			add = dst.add(db);

			Table_storage_info tsi = new Table_storage_info();
			tsi.setStorage_id(idL1);
			tsi.setFile_format("0");
			tsi.setStorage_type("1");
			tsi.setIs_zipper("1");
			tsi.setStorage_time("0");
			tsi.setTable_id(idL1);
			int add1 = tsi.add(db);

			tsi.setStorage_id(idL2);
			tsi.setFile_format("0");
			tsi.setStorage_type("1");
			tsi.setIs_zipper("1");
			tsi.setStorage_time("0");
			tsi.setTable_id(idL2);
			add1 = tsi.add(db);

			tsi.setStorage_id(idL3);
			tsi.setFile_format("0");
			tsi.setStorage_type("1");
			tsi.setIs_zipper("1");
			tsi.setStorage_time("0");
			tsi.setTable_id(idL3);
			add1 = tsi.add(db);

			Data_relation_table drt = new Data_relation_table();
			drt.setStorage_id(idL1);
			drt.setDsl_id(idL1);
			int add2 = drt.add(db);

			drt.setStorage_id(idL2);
			drt.setDsl_id(idL1);//关系
			add2 = drt.add(db);

			drt.setStorage_id(idL3);
			drt.setDsl_id(idL1);//关系
			add2 = drt.add(db);


			Data_store_layer dsl = new Data_store_layer();
			dsl.setDsl_id(idL1);
			dsl.setDsl_name("cccc");
			dsl.setStore_type("1");
			dsl.setIs_hadoopclient("1");
			int add3 = dsl.add(db);

			dsl.setDsl_id(idL2);
			dsl.setDsl_name("cccc111");
			dsl.setStore_type("1");
			dsl.setIs_hadoopclient("1");
			add3 = dsl.add(db);

			Data_store_layer_attr dsla = new Data_store_layer_attr();
			dsla.setDsla_id("1234567890");
			dsla.setStorage_property_key(StorageTypeKey.database_driver);
			dsla.setStorage_property_val("org.postgresql.Driver");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL1);
			int add4 = dsla.add(db);

			dsla.setDsla_id("2234567890");
			dsla.setStorage_property_key(StorageTypeKey.database_driver);
			dsla.setStorage_property_val("org.postgresql.Driver");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL2);
			add4 = dsla.add(db);


			dsla.setDsla_id("1234567891");
			dsla.setStorage_property_key(StorageTypeKey.jdbc_url);
			dsla.setStorage_property_val("jdbc:postgresql://10.71.4.57:31001/hrsdxg");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL1);
			add4 = dsla.add(db);

			dsla.setDsla_id("2234567891");
			dsla.setStorage_property_key(StorageTypeKey.jdbc_url);
			dsla.setStorage_property_val("jdbc:postgresql://10.71.4.57:31001/hrsdxg");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL2);
			add4 = dsla.add(db);

			dsla.setDsla_id("1234567892");
			dsla.setStorage_property_key(StorageTypeKey.user_name);
			dsla.setStorage_property_val("hrsdxg");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL1);
			add4 = dsla.add(db);

			dsla.setDsla_id("2234567892");
			dsla.setStorage_property_key(StorageTypeKey.user_name);
			dsla.setStorage_property_val("hrsdxg");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL2);
			add4 = dsla.add(db);

			dsla.setDsla_id("1234567893");
			dsla.setStorage_property_key(StorageTypeKey.database_pwd);
			dsla.setStorage_property_val("hrsdxg");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL1);

			add4 = dsla.add(db);
			dsla.setDsla_id("2234567893");
			dsla.setStorage_property_key(StorageTypeKey.database_pwd);
			dsla.setStorage_property_val("hrsdxg");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL2);
			add4 = dsla.add(db);

			dsla.setDsla_id("1234567894");
			dsla.setStorage_property_key(StorageTypeKey.database_type);
			dsla.setStorage_property_val("11");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL1);
			add4 = dsla.add(db);

			dsla.setDsla_id("2234567894");
			dsla.setStorage_property_key(StorageTypeKey.database_type);
			dsla.setStorage_property_val("11");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL2);
			add4 = dsla.add(db);

			db.commit();
		}
	}

	@After
	public void deleteData() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			SqlOperator.execute(db, "delete from " + Data_store_reg.TableName + " where file_id in(?,?,?)", idS1, idS2,idS3);
			long num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_reg.TableName +
					"  where file_id in(?,?,?)", idS1, idS2,idS3).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));

			SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + " where storage_id in(?,?,?)", idL1, idL2,idL3);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName +
					"  where storage_id in(?,?,?)", idL1, idL2,idL3).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));

			SqlOperator.execute(db, "delete from " + Data_relation_table.TableName + " where storage_id in(?,?,?)", idL1, idL2,idL3);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_relation_table.TableName +
					"  where storage_id in(?,?,?)", idL1, idL2,idL3).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));

			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id in(?,?,?)", idL1, idL2,idL3);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer.TableName +
					"  where dsl_id in(?,?,?)", idL1, idL2,idL3).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));


			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsl_id in(?,?,?)", idL1, idL2,idL3);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_attr.TableName +
					"  where dsl_id in(?,?,?)", idL1, idL2,idL3).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));

			db.commit();
		}
	}

	@Test
	public void getDataLayer() throws SQLException, IOException {
		List<Map<String, Object>> mm = new ArrayList<>();
		//BufferedWriter bw = new BufferedWriter(new FileWriter("d://bw.txt"));
		int lineCounter = 0;
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<String> dataLayer = new ProcessingData() {
				@Override
				public void dealLine(Map<String, Object> map) throws Exception {
					mm.add(map);//在这里，对数据处理，如写文件，清洗、还是组成什么格式给前端，有你来定
					//dataPro(map, bw);
				}
			}.getDataLayer("select * from sys_user a join sys_role b on a.role_id = b.role_id where 1=2", db);
			assertThat("不等于0就ok", mm.size(), not(0L));
			//bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	int lineCounter = 0;
	@Test
	public void getDataLayerBatch() throws SQLException, IOException {
		List<Map<String, Object>> mm = new ArrayList<>();
		BufferedWriter bw = new BufferedWriter(new FileWriter("d://bw.txt"));
		int lineCounter = 0;
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			new ProcessingData() {
				@Override
				public void dealLine(Map<String, Object> map) throws Exception {
					dataPro(map, bw);
				}
			}.getDataLayer("select * from xc_CODE_INFO ", db);
			assertThat("不等于0就ok", mm.size(), not(0L));
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 只允许参考
	 * @param map
	 * @param bw
	 * @throws Exception
	 */
	public void dataPro(Map<String, Object> map, BufferedWriter bw) throws Exception {

		lineCounter++;
		StringBuilder colsb = new StringBuilder();
		StringBuilder valsb = new StringBuilder();
		for (Map.Entry<String, Object> m : map.entrySet()) {
			String key = m.getKey();
			colsb.append(key).append(",");
			Object value = m.getValue();
			valsb.append(value).append(",");
		}
		if(lineCounter == 1){
			colsb.append(System.lineSeparator());
			bw.write(colsb.toString());
			bw.flush();
		}
		valsb.append(System.lineSeparator());
		bw.write(valsb.toString());
		if( lineCounter % 24608 == 0 ) {
			logger.info("已经处理了 ：" + lineCounter + " 行数据！");
			bw.flush();
		}
	}



	@Test
	public void getAllTableIsLayer() throws SQLException {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			LayerTypeBean allTableIsLayer = ProcessingData.getAllTableIsLayer("select * from sys_user a join sys_role b on a.role_id = b.role_id", db);
			/**
			 * allTableIsLayer.getConnType()会返回四个值
			 * oneJdbc  只有一个存储层，且直接支持jdbc
			 * moreJdbc  返回多个存储层，且都支持jdbc，可以使用dblink的方式
			 * oneOther  返回一个存储层，为其他，是什么可以使用allTableIsLayer.getLayerBean().getStore_type() 判断
			 * moreOther 返回多个存储层，每个存储层什么什么
			 *          List<LayerBean> layerBeanList = allTableIsLayer.getLayerBeanList();
			 * 			for (LayerBean layerBean : layerBeanList) {
			 * 				String store_type = layerBean.getStore_type();
			 *          }
			 */
			assertThat(allTableIsLayer.getConnType(), is(LayerTypeBean.ConnType.oneJdbc));
		}
	}
	public void batchData(){
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			/*db.ExecDDL("DROP TABLE IF EXISTS xc_CODE_INFO ;\n" +
					"CREATE TABLE xc_CODE_INFO(\n" +
					"CI_SP_CODE                                        VARCHAR(20) NOT NULL, --代码值\n" +
					"CI_SP_CLASS                                       VARCHAR(20) NOT NULL, --所属类别号\n" +
					"CI_SP_CLASSNAME                                   VARCHAR(80) NOT NULL, --类别名称\n" +
					"CI_SP_NAME                                        VARCHAR(255) NOT NULL, --代码名称\n" +
					"CI_SP_REMARK                                      VARCHAR(512) NULL --备注\n" +
					");");*/
			List<Object[]> para = new ArrayList<>();
			for (int i = 0; i < 1000000; i++) {
				Object[] aa = new Object[]{"80", "MAN", "数据质量执行方式", "手工", "DqcExecMode"};
				para.add(aa);
			}
			SqlOperator.executeBatch(db, "INSERT INTO xc_CODE_INFO  VALUES (?,?,?, ?,?); ",para);
			SqlOperator.commitTransaction(db);
		}
	}
	/**
	 * 根据表名获取存储信息，map，已表明为主
	 */
	@Test
	public void getLayerByTable() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<LayerBean> sys_user = ProcessingData.getLayerByTable("sys_user", db);
			for (LayerBean layerBean : sys_user) {
				assertThat(layerBean.getDsl_id(), is(1999998876L));
				assertThat(layerBean.getLayerAttr().get("user_name"), is("hrsdxg"));
			}
		}
	}

	/**
	 * 多表获取不同的存储层
	 */
	@Test
	public void getLayerByAllTable() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<String> table = new ArrayList<>();
			table.add("sys_user");
			table.add("sys_role");
			Map<String, List<LayerBean>> layerByTable = ProcessingData.getLayerByTable(table, db);
			List<LayerBean> list = layerByTable.get("sys_user");
			for (LayerBean layerBean : list) {
				assertThat(layerBean.getDsl_id(), is(1999998876L));
				assertThat(layerBean.getLayerAttr().get("user_name"), is("hrsdxg"));
			}
		}
	}
}
