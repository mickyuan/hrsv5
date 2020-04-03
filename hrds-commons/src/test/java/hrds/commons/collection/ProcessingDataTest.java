package hrds.commons.collection;

import fd.ng.core.utils.DateUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.CollectType;
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
	public Long id = 1999998876L;
	private String idS = String.valueOf(id);
	@Before
	public void insertData(){
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Data_store_reg dst = new Data_store_reg();
			dst.setFile_id(idS);
			dst.setCollect_type(CollectType.DBWenJianCaiJi.getCode());
			dst.setOriginal_update_date(DateUtil.getSysDate());
			dst.setOriginal_update_time(DateUtil.getSysTime());
			dst.setOriginal_name("sys_user");
			dst.setTable_name("sys_user");
			dst.setHyren_name("sys_user");
			dst.setStorage_date(DateUtil.getSysDate());
			dst.setStorage_time(DateUtil.getSysTime());
			dst.setFile_size(20L);
			dst.setAgent_id(id);
			dst.setSource_id(id);
			dst.setDatabase_id(id);
			dst.setTable_id(id);
			int add = dst.add(db);

			Table_storage_info tsi = new Table_storage_info();
			tsi.setStorage_id(id);
			tsi.setFile_format("0");
			tsi.setStorage_type("1");
			tsi.setIs_zipper("1");
			tsi.setStorage_time("0");
			tsi.setTable_id(id);
			int add1 = tsi.add(db);

			Data_relation_table drt = new Data_relation_table();
			drt.setStorage_id(id);
			drt.setDsl_id(id);
			int add2 = drt.add(db);

			Data_store_layer dsl = new Data_store_layer();
			dsl.setDsl_id(id);
			dsl.setDsl_name("cccc");
			dsl.setStore_type("1");
			dsl.setIs_hadoopclient("1");
			int add3 = dsl.add(db);

			Data_store_layer_attr dsla = new Data_store_layer_attr();
			dsla.setDsla_id("1234567890");
			dsla.setStorage_property_key("database_drive");
			dsla.setStorage_property_val("org.postgresql.Driver");
			dsla.setIs_file("0");
			dsla.setDsl_id(id);

			int add4 = dsla.add(db);
			dsla.setDsla_id("1234567891");
			dsla.setStorage_property_key("jdbc_url");
			dsla.setStorage_property_val("jdbc:postgresql://10.71.4.52:31001/hrsdxgtest");
			dsla.setIs_file("0");
			dsla.setDsl_id(id);
			add4 = dsla.add(db);

			dsla.setDsla_id("1234567892");
			dsla.setStorage_property_key("user_name");
			dsla.setStorage_property_val("hrsdxg");
			dsla.setIs_file("0");
			dsla.setDsl_id(id);
			add4 = dsla.add(db);

			dsla.setDsla_id("1234567893");
			dsla.setStorage_property_key("database_pad");
			dsla.setStorage_property_val("hrsdxg");
			dsla.setIs_file("0");
			dsla.setDsl_id(id);
			add4 = dsla.add(db);

			dsla.setDsla_id("1234567894");
			dsla.setStorage_property_key("database_type");
			dsla.setStorage_property_val("11");
			dsla.setIs_file("0");
			dsla.setDsl_id(id);
			add4 = dsla.add(db);

			db.commit();
		}
	}

	@After
	public void deleteData(){
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			SqlOperator.execute(db, "delete from " + Data_store_reg.TableName + " where file_id=?", idS);
			long num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_reg.TableName +
					"  where file_id=?", idS).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));

			SqlOperator.execute(db, "delete from " + Table_storage_info.TableName + " where storage_id=?", id);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Table_storage_info.TableName +
					"  where storage_id=?", id).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));

			SqlOperator.execute(db, "delete from " + Data_relation_table.TableName + " where storage_id=?", id);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_relation_table.TableName +
					"  where storage_id=?", id).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));

			SqlOperator.execute(db, "delete from " + Data_store_layer.TableName + " where dsl_id=?", id);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer.TableName +
					"  where dsl_id=?", id).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));


			SqlOperator.execute(db, "delete from " + Data_store_layer_attr.TableName + " where dsl_id=?", id);
			num = SqlOperator.queryNumber(db, "select count(1) from " + Data_store_layer_attr.TableName +
					"  where dsl_id=?", id).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num, is(0L));

			db.commit();
		}
	}
	@Test
	public void getSQLEngine() throws SQLException {
		List<Map<String,Object>> mm = new ArrayList<>();
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			new ProcessingData() {
				@Override
				public void dealLine(Map<String, Object> map) throws Exception {
					mm.add(map);//在这里，对数据处理，如写文件，清洗、还是组成什么格式给前端，有你来定
				}
			}.getSQLEngine("select * from sys_user", db);
			assertThat("不等于0就ok", mm.size(), not(0L));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}