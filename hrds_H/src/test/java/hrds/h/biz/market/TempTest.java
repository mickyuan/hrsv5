package hrds.h.biz.market;

import fd.ng.core.annotation.DocClass;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import groovy.sql.Sql;
import hrds.commons.entity.*;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DocClass(desc = "集市信息查询类", author = "TBH", createdate = "2020年5月21日 16点48分")
public class TempTest extends WebBaseTestCase {

	private static List<String> districtlist = new ArrayList<>();

	static {
		districtlist.add("黄浦区");
		districtlist.add("徐汇区");
		districtlist.add("长宁区");
		districtlist.add("静安区");
		districtlist.add("普陀区");
		districtlist.add("虹口区");
		districtlist.add("杨浦区");
		districtlist.add("闵行区");
		districtlist.add("宝山区");
		districtlist.add("嘉定区");
		districtlist.add("金山区");
		districtlist.add("松江区");
		districtlist.add("青浦区");
		districtlist.add("奉贤区");
		districtlist.add("崇明区");
		districtlist.add("浦东新区");
	}

	public static void main(String args[]) {
		DatabaseWrapper db = null;
		try {
			db = new DatabaseWrapper();
			String sql = "select * from FDJZJD";
			List<Map<String, Object>> maps1 = SqlOperator.queryList(db, sql);
			sql = "update FDJGD1_6 set 分类结果 = null";
			SqlOperator.execute(db, sql);
			sql = "select * from FDJGD1_6";
			List<Map<String, Object>> maps2 = SqlOperator.queryList(db, sql);
			int zjd = 0;
			int zjdqx = 0;
			List<Object[]> gdbhList = new ArrayList<>();
			for (Map<String, Object> map2 : maps2) {
				Object Baddress = map2.get("诉求地址");
				Object gdbh = map2.get("工单编号");
				if (Baddress == null) {
					continue;
				}
				String s = Baddress.toString();
				for (Map<String, Object> map1 : maps1) {
					Object quxian = map1.get("区县");
					Object zhen = map1.get("镇");
					Object cun = map1.get("村");
					Object xiaoqu = map1.get("小区");
					Object dizhi = map1.get("地址");
					//村
					if (cun != null) {
						if (s.contains(cun.toString())) {
							boolean b = checkIfDistrictAndZhen(s, quxian, zhen);
							if (b) {
								gdbhList.add(new Object[]{gdbh.toString()});
								zjd++;
								break;
							}
						}
					}
					//小区
					if (xiaoqu != null)
						//判断镇 和 区 对不对
						if (s.contains(xiaoqu.toString())) {
							boolean b = checkIfDistrictAndZhen(s, quxian, zhen);
							if (b) {
								gdbhList.add(new Object[]{gdbh.toString()});
								zjd++;
								break;
							}
						}
					if (dizhi != null) {
						if (s.contains(dizhi.toString())) {
							boolean b = checkIfDistrictAndZhen(s, quxian, zhen);
							if (b) {
								gdbhList.add(new Object[]{gdbh.toString()});
								zjd++;
								break;
							}

						}
					}
				}
				if (gdbhList.size() == 50) {
					System.out.println("开始修改"+zjd+"条");
					update(db, gdbhList);
					gdbhList.clear();
					System.out.println("修改完成");
				}
			}
			System.out.println(zjd);
//			System.out.println(zjdqx);
			SqlOperator.commitTransaction(db);
			db.close();
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
			e.printStackTrace();
			throw e;
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	private static void update(DatabaseWrapper db, List<Object[]> gdbhList) {
		SqlOperator.executeBatch(db, "update fdjgd1_6 set 分类结果 = '宅基地'  where 工单编号 = ?", gdbhList);
	}

	private static boolean checkIfDistrictAndZhen(String s, Object district, Object zhen) {
		boolean districtFlag = false;
		String getdistrict = getdistrict(s);
		//如果 字符串中 有区域地址
		if (getdistrict != null) {
			String s1 = district.toString();
			//判断区域是否一致
			if (getdistrict.equals(s1)) {
				districtFlag = true;
			} else {
				districtFlag = false;
			}
		}
		//如果没有区域地址
		else {
			districtFlag = true;
		}
		boolean zhenFlag = false;
		if (zhen != null) {
			if (s.contains("镇")) {
				if (s.contains(zhen.toString())) {
					zhenFlag = true;
				} else {
					zhenFlag = false;
				}
			} else {
				//?????
				zhenFlag = true;
			}
		} else {
//?????
			zhenFlag = true;
		}
		return districtFlag && zhenFlag;
	}

	private static String getdistrict(String s) {
		for (String district : districtlist) {
			if (s.contains(district)) {
				return district;
			}
		}
		return null;
	}
}