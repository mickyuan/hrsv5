package hrds.commons.collection;

import fd.ng.core.utils.DateUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.CollectType;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.collection.bean.LayerTypeBean;
import hrds.commons.entity.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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

	@Before
	public void insertData() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Data_store_reg dst = new Data_store_reg();
			dst.setFile_id(idS1);
			dst.setCollect_type(CollectType.DBWenJianCaiJi.getCode());
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

			dst.setFile_id(idS2);
			dst.setCollect_type(CollectType.DBWenJianCaiJi.getCode());
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

			Data_relation_table drt = new Data_relation_table();
			drt.setStorage_id(idL1);
			drt.setDsl_id(idL1);
			int add2 = drt.add(db);

			drt.setStorage_id(idL2);
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
			dsla.setStorage_property_key("database_drive");
			dsla.setStorage_property_val("org.postgresql.Driver");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL1);
			int add4 = dsla.add(db);

			dsla.setDsla_id("2234567890");
			dsla.setStorage_property_key("database_drive");
			dsla.setStorage_property_val("org.postgresql.Driver");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL2);
			add4 = dsla.add(db);


			dsla.setDsla_id("1234567891");
			dsla.setStorage_property_key("jdbc_url");
			dsla.setStorage_property_val("jdbc:postgresql://10.71.4.52:31001/hrsdxgtest");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL1);
			add4 = dsla.add(db);

			dsla.setDsla_id("2234567891");
			dsla.setStorage_property_key("jdbc_url");
			dsla.setStorage_property_val("jdbc:postgresql://10.71.4.52:31001/hrsdxgtest");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL2);
			add4 = dsla.add(db);

			dsla.setDsla_id("1234567892");
			dsla.setStorage_property_key("user_name");
			dsla.setStorage_property_val("hrsdxg");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL1);
			add4 = dsla.add(db);

			dsla.setDsla_id("2234567892");
			dsla.setStorage_property_key("user_name");
			dsla.setStorage_property_val("hrsdxg");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL2);
			add4 = dsla.add(db);

			dsla.setDsla_id("1234567893");
			dsla.setStorage_property_key("database_pad");
			dsla.setStorage_property_val("hrsdxg");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL1);

			add4 = dsla.add(db);
			dsla.setDsla_id("2234567893");
			dsla.setStorage_property_key("database_pad");
			dsla.setStorage_property_val("hrsdxg");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL2);
			add4 = dsla.add(db);

			dsla.setDsla_id("1234567894");
			dsla.setStorage_property_key("database_type");
			dsla.setStorage_property_val("11");
			dsla.setIs_file("0");
			dsla.setDsl_id(idL1);
			add4 = dsla.add(db);

			dsla.setDsla_id("2234567894");
			dsla.setStorage_property_key("database_type");
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
			SqlOperator.execute(db, "delete from " + Data_store_reg.TableName + " where file_id in(?,?)", idS1, idS2);
			long num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_reg.TableName +
					"  where file_id in(?,?)", idS1, idS2).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));

			SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + " where storage_id in(?,?)", idL1, idL2);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName +
					"  where storage_id in(?,?)", idL1, idL2).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));

			SqlOperator.execute(db, "delete from " + Data_relation_table.TableName + " where storage_id in(?,?)", idL1, idL2);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_relation_table.TableName +
					"  where storage_id in(?,?)", idL1, idL2).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));

			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id in(?,?)", idL1, idL2);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer.TableName +
					"  where dsl_id in(?,?)", idL1, idL2).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));


			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsl_id in(?,?)", idL1, idL2);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_attr.TableName +
					"  where dsl_id in(?,?)", idL1, idL2).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));

			db.commit();
		}
	}

	@Test
	public void getDataLayer() throws SQLException {
		List<Map<String, Object>> mm = new ArrayList<>();
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			new ProcessingData() {
				@Override
				public void dealLine(Map<String, Object> map) throws Exception {
					mm.add(map);//在这里，对数据处理，如写文件，清洗、还是组成什么格式给前端，有你来定
				}
			}.getDataLayer("select * from sys_user a join sys_role b on a.role_id = b.role_id", db);
			System.out.println(mm);
			assertThat("不等于0就ok", mm.size(), not(0L));
		} catch (Exception e) {
			e.printStackTrace();
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
			assertThat(allTableIsLayer.getConnType(), is(LayerTypeBean.ConnPyte.oneJdbc));
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