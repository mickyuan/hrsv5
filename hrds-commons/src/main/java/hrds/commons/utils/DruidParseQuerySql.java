package hrds.commons.utils;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Name;
import fd.ng.core.utils.StringUtil;

import java.util.*;

/**
 * <p>标    题: 海云数服 V3.5</p>
 * <p>描    述: 使用Druid解析查询SQL语句/p>
 * <p>版    权: Copyright (c) 2019</p>
 * <p>公    司: 博彦科技(上海)有限公司</p>
 * <p>@author : Mr.Lee</p>
 * <p>创建时间 : 2019-06-14 12:00</p>
 * <p>version: JDK 1.8</p>
 */
public class DruidParseQuerySql {
	public List<SQLSelectItem> selectList = null;

	/**
	 * <p>方法描述: 将传入的SQL使用Oracle的方式进行解析</p>
	 * <p>开发人员: Mr.Lee </p>
	 * <p>创建时间: 2019/6/14</p>
	 * <p>参数:  </p>
	 * <p>return:  </p>
	 */
	public DruidParseQuerySql(String sql) {

		OracleStatementParser sqlStatementParser = new OracleStatementParser(sql);
		SQLSelectStatement parseSelect = (SQLSelectStatement)sqlStatementParser.parseSelect();
		SQLSelect select = parseSelect.getSelect();
		SQLSelectQuery query = select.getQuery();

		OracleSelectQueryBlock left = null;

		//这里检测SQL是否为UNION
		if( query instanceof SQLUnionQuery) {
			SQLUnionQuery unionQuery = (SQLUnionQuery)query;
			left = (OracleSelectQueryBlock)unionQuery.getLeft();
			selectList = left.getSelectList();

		}
		else {
			left = (OracleSelectQueryBlock)query;
			selectList = left.getSelectList();
		}

	}

	/**
	 * <p>方法描述: 解析查询SQL中的原字段信息,如果查询的列字段中有自定义字段,则放入自定义字段</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-06-25</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	public List<String> parseSelectOriginalField() {

		List<String> originalColumnSet = new ArrayList<String>();
		//处理解析后的字段信息
		selectList.forEach(val -> {
			SQLExpr expr = val.getExpr();
			if( expr instanceof SQLPropertyExpr) {
				//originalColumnSet.add(((SQLPropertyExpr)expr).getName());
				originalColumnSet.add(expr.toString());
			}
			else if( expr instanceof SQLIdentifierExpr) {
				SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr)expr;
				originalColumnSet.add(identifierExpr.getName());
			}
			else {
				originalColumnSet.add(val.getAlias());
			}
		});
		return originalColumnSet;
	}

	/**
	 * <p>方法描述: 解析查询SQL中查询字段的别名信息,如果没有别名将存入原字段信息</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-06-25</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	public List<String> parseSelectAliasField() {

		List<String> aliasColumnSet = new ArrayList<String>();
		//处理解析后的字段信息
		selectList.forEach(val -> {
			if( StringUtil.isNotBlank(val.getAlias()) ) {
				aliasColumnSet.add(val.getAlias());
				return;
			}
			SQLExpr expr = val.getExpr();
			if( expr instanceof SQLPropertyExpr ) {
				aliasColumnSet.add(((SQLPropertyExpr)expr).getName());
			}
			else if( expr instanceof SQLIdentifierExpr ) {
				SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr)expr;
				aliasColumnSet.add(identifierExpr.getName());
			}
			else {
				aliasColumnSet.add(val.getAlias());
			}
		});
		return aliasColumnSet;
	}

	/**
	 * <p>方法描述: 解析查询SQL中查询语句的表名</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-07-19</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	public static Set<Name> parseSqlTable(String sql) {

		SQLStatementParser parse = new OracleStatementParser(sql);
		SQLStatement parseStatement = parse.parseStatement();
		OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
		parseStatement.accept(visitor);
		Map<TableStat.Name, TableStat> tables = visitor.getTables();
		Set<Name> keySet = tables.keySet();
		return keySet;
	}

	/**
	 * 通过查询sql 获取其表明
	 * @param sql
	 * @return
	 */
	public static List<String> parseSqlTableToList(String sql){
		List<String> tableList = new ArrayList<>();
		Set<Name> parseSqlTable = parseSqlTable(sql);
		Iterator<Name> iterator = parseSqlTable.iterator();
		while( iterator.hasNext() ) {
			tableList.add(iterator.next().toString());
		}
		return tableList;
	}
}
