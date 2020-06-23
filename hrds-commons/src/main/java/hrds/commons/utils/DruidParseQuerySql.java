package hrds.commons.utils;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.*;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Name;
import com.alibaba.druid.util.JdbcConstants;
import fd.ng.core.exception.BusinessSystemException;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.TableStorage;
import hrds.commons.entity.Dm_datatable;
import hrds.commons.entity.Dm_operation_info;
import hrds.commons.exception.BusinessException;
import org.apache.commons.lang.StringUtils;

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
    public static String sourcecolumn = "sourcecolumn";
    public static String sourcetable = "sourcetable";
    public List<SQLSelectItem> selectList = null;
    private List<HashMap<String, Object>> listmap = new ArrayList<HashMap<String, Object>>();
    private List<HashMap<String, Object>> columnlist = new ArrayList<HashMap<String, Object>>();
    private HashMap<String, Object> hashmap = new HashMap<String, Object>();
    private String mainSql = new String();

    /**
     * <p>方法描述: 将传入的SQL使用Oracle的方式进行解析</p>
     * <p>开发人员: Mr.Lee </p>
     * <p>创建时间: 2019/6/14</p>
     * <p>参数:  </p>
     * <p>return:  </p>
     */
    public DruidParseQuerySql(String sql) {

        OracleStatementParser sqlStatementParser = new OracleStatementParser(sql);
        SQLSelectStatement parseSelect = (SQLSelectStatement) sqlStatementParser.parseSelect();
        SQLSelect select = parseSelect.getSelect();
        SQLSelectQuery query = select.getQuery();

        OracleSelectQueryBlock left = null;

        //这里检测SQL是否为UNION
        if (query instanceof SQLUnionQuery) {
            SQLUnionQuery unionQuery = (SQLUnionQuery) query;
            left = (OracleSelectQueryBlock) unionQuery.getLeft();
            selectList = left.getSelectList();

        } else {
            left = (OracleSelectQueryBlock) query;
            selectList = left.getSelectList();
        }
    }

    public DruidParseQuerySql() {
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
            if (expr instanceof SQLPropertyExpr) {
                //originalColumnSet.add(((SQLPropertyExpr)expr).getName());
                originalColumnSet.add(expr.toString());
            } else if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) expr;
                originalColumnSet.add(identifierExpr.getName());
            } else {
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
            if (StringUtil.isNotBlank(val.getAlias())) {
                aliasColumnSet.add(val.getAlias());
                return;
            }
            SQLExpr expr = val.getExpr();
            if (expr instanceof SQLPropertyExpr) {
                aliasColumnSet.add(((SQLPropertyExpr) expr).getName());
            } else if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) expr;
                aliasColumnSet.add(identifierExpr.getName());
            } else {
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
     *
     * @param sql
     * @return
     */
    public static List<String> parseSqlTableToList(String sql) {
        List<String> tableList = new ArrayList<>();
        Set<Name> parseSqlTable = parseSqlTable(sql);
        Iterator<Name> iterator = parseSqlTable.iterator();
        while (iterator.hasNext()) {
            tableList.add(iterator.next().toString());
        }
        return tableList;
    }

    /**
     * <p>方法描述: 解析SQL的血缘关系 该SQL必须为标准SQL 不能省略</p>
     * <p>@author: TBH </p>
     * <p>创建时间: 2020-04-10</p>
     * <p>参   数:  </p>
     * <p>return:  返回一个HashMap 其中key为sql的查询字段 value是一个list map 记录每一个血缘字段和来源表</p>
     */
    public HashMap<String, Object> getBloodRelationMap(String sql) {
        sql = sql.trim();
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        this.listmap.clear();
        this.columnlist.clear();
        this.hashmap.clear();
        String dbType = JdbcConstants.ORACLE;
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        for (SQLStatement stmt : stmtList) {
            SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) stmt;
            SQLSelect sqlSelect = sqlSelectStatement.getSelect();
            SQLSelectQuery sqlSelectQuery = sqlSelect.getQuery();
            getBloodRelation(sqlSelectQuery);
        }
        return hashmap;
    }

    /**
     * <p>方法描述: 获取查询query,判断union的话,拆分迭代,拿到queryblock开始处理from的部分</p>
     * <p>@author: TBH </p>
     * <p>创建时间: 2020-04-10</p>
     * <p>参   数:  </p>
     */
    private void getBloodRelation(SQLSelectQuery sqlSelectQuery) {
        //拆分union
        if (sqlSelectQuery instanceof SQLUnionQuery) {
            SQLUnionQuery sqlUnionQuery = (SQLUnionQuery) sqlSelectQuery;
            getBloodRelation(sqlUnionQuery.getLeft());
            getBloodRelation(sqlUnionQuery.getRight());
            //因为选择的是oracle的datatype 所以只考虑unionquery和oraclequeryblock这两种
        } else if (sqlSelectQuery instanceof OracleSelectQueryBlock) {
            OracleSelectQueryBlock oracleSelectQueryBlock = (OracleSelectQueryBlock) sqlSelectQuery;
            this.mainSql = oracleSelectQueryBlock.toString();
            //开始处理from的部分
            handleFrom(oracleSelectQueryBlock.getFrom());
        } else {
            throw new BusinessSystemException("未知的sqlSelectQuery：" + sqlSelectQuery.toString() + " class:" + sqlSelectQuery.getClass());
        }
    }

    /**
     * <p>方法描述: 迭代处理from的部分，目的是找到最简单的单表from部分
     * 然后对单表进行处理,向上寻找并记录改单表所拥有的字段，直至寻找到最上层（完整的sql),挂上血缘关系
     * </p>
     * <p>@author: TBH </p>
     * <p>创建时间: 2020-04-10</p>
     * <p>参   数:  </p>
     */
    private void handleFrom(SQLTableSource sqlTableSource) {
        //如果是join的形式 就递归继续拆分
        if (sqlTableSource instanceof OracleSelectJoin) {
            OracleSelectJoin oracleSelectJoin = (OracleSelectJoin) sqlTableSource;
            handleFrom(oracleSelectJoin.getLeft());
            handleFrom(oracleSelectJoin.getRight());
        }
        //如果是子查询，继续拆分from
        else if (sqlTableSource instanceof OracleSelectSubqueryTableSource) {
            OracleSelectSubqueryTableSource oracleSelectSubqueryTableSource = (OracleSelectSubqueryTableSource) sqlTableSource;
            SQLSelect sqlSelect = oracleSelectSubqueryTableSource.getSelect();
            SQLSelectQuery sqlSelectQuery = sqlSelect.getQuery();
            handleFrom2(sqlSelectQuery);
        }
        //如果是单表的话 那么单表的parent一定是最简单的查询
        else if (sqlTableSource instanceof OracleSelectTableReference) {
            OracleSelectTableReference oracleSelectTableReference = (OracleSelectTableReference) sqlTableSource;
            SQLObject sqlObject = oracleSelectTableReference.getParent();
            //寻找当前from部分所在的整个查询
            while (sqlObject instanceof OracleSelectQueryBlock == false) {
                sqlObject = sqlObject.getParent();
            }
            listmap.clear();
            String upperealias = new String();
            //循环直到找到全部的完整sql
            while (!trim(sqlObject.toString()).equalsIgnoreCase(trim(mainSql))) {
                OracleSelectQueryBlock oracleSelectQueryBlock = (OracleSelectQueryBlock) sqlObject;
                startHandleFromColumn(oracleSelectQueryBlock, sqlTableSource, upperealias);
                sqlObject = sqlObject.getParent();
                //判断 如果当前完整查询sql 为子查询的话 需要记录子查询的临时表名
                if (sqlObject.getParent() instanceof OracleSelectSubqueryTableSource) {
                    OracleSelectSubqueryTableSource oracleSelectSubqueryTableSource = (OracleSelectSubqueryTableSource) sqlObject.getParent();
                    upperealias = oracleSelectSubqueryTableSource.getAlias();
                }
                while (sqlObject instanceof OracleSelectQueryBlock == false) {
                    sqlObject = sqlObject.getParent();
                }
            }
            OracleSelectQueryBlock oracleSelectQueryBlock = (OracleSelectQueryBlock) sqlObject;
            Boolean isOracleSelectTableReference = oracleSelectQueryBlock.getFrom() instanceof OracleSelectTableReference;
            handleGetColumn(oracleSelectQueryBlock);
            //如果listmap为空，说明这个table是最外层的，直接去寻找最外层selectexpr和该表关链的部分
            if (listmap.isEmpty()) {
                for (int j = 0; j < columnlist.size(); j++) {
                    SQLExpr sqlexpr = (SQLExpr) columnlist.get(j).get("column");
                    String alias = columnlist.get(j).get("alias").toString();
                    ArrayList<HashMap<String, Object>> templist = getTempList(alias);
                    if (sqlexpr instanceof SQLIdentifierExpr) {
                        if (isOracleSelectTableReference) {
                            putResult(templist, alias, sqlexpr.toString(), sqlTableSource.toString());
                        } else {
                            throw new BusinessSystemException("请填写标准sql 字段" + sqlexpr.toString() + "未知来源");
                        }
                    } else if (sqlexpr instanceof SQLPropertyExpr) {
                        SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) sqlexpr;
                        putResult(templist, alias, sqlPropertyExpr.getName(), sqlTableSource.toString());
                    }
                }
            } else {
                for (int i = 0; i < listmap.size(); i++) {
                    HashMap<String, Object> stringObjectHashMap = listmap.get(i);
                    List<String> uppercolumnlist = (ArrayList<String>) stringObjectHashMap.get("uppercolumn");
                    for (String uppercolumn : uppercolumnlist) {
                        for (int j = 0; j < columnlist.size(); j++) {
                            SQLExpr sqlexpr = (SQLExpr) columnlist.get(j).get("column");
                            String alias = columnlist.get(j).get("alias").toString();
                            ArrayList<HashMap<String, Object>> templist = getTempList(alias);
                            if (sqlexpr instanceof SQLIdentifierExpr) {
                                if (uppercolumn.equalsIgnoreCase(sqlexpr.toString())) {
                                    putResult(templist, alias, stringObjectHashMap.get("columnname") == null ? null : stringObjectHashMap.get("columnname").toString(),
                                            stringObjectHashMap.get("table").toString());
                                }
                            } else if (sqlexpr instanceof SQLPropertyExpr) {
                                SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) sqlexpr;
                                if (uppercolumn.equalsIgnoreCase(sqlPropertyExpr.getName())
                                        && sqlPropertyExpr.getOwner().toString().equalsIgnoreCase(upperealias)) {
                                    putResult(templist, alias, stringObjectHashMap.get("columnname") == null ? null : stringObjectHashMap.get("columnname").toString(),
                                            stringObjectHashMap.get("table").toString());
                                }
                            }
                        }
                    }
                }
            }
        } else {
            String message;
            if (sqlTableSource == null) {
                message = "未知的From来源";
            } else {
                message = "未知的From来源：" + sqlTableSource.toString() + " class:" + sqlTableSource.getClass();
            }
            throw new BusinessSystemException(message);
        }
    }

    /**
     * <p>方法描述: 将结果放入hashmap中</p>
     * <p>@author: TBH </p>
     * <p>创建时间: 2020-04-10</p>
     * <p>参   数:  </p>
     */
    private void putResult(ArrayList<HashMap<String, Object>> templist, String alias, String columnname, String table) {
        if (columnname != null) {
            HashMap<String, Object> temphashmap = new HashMap<String, Object>();
            temphashmap.put(sourcecolumn, columnname.toLowerCase());
            temphashmap.put(sourcetable, table.toLowerCase());
            templist.add(temphashmap);
            System.out.println(alias + " " + "sourcecolumn:" + columnname.toLowerCase() + " sourcetable:" + table.toLowerCase());
        }
    }

    /**
     * <p>方法描述: 获取hashmap中表示当前字段的templist/p>
     * <p>@author: TBH </p>
     * <p>创建时间: 2020-04-10</p>
     * <p>参   数:  </p>
     */
    private ArrayList<HashMap<String, Object>> getTempList(String alias) {
        ArrayList<HashMap<String, Object>> templist = new ArrayList<HashMap<String, Object>>();
        if (hashmap.get(alias) != null) {
            templist = (ArrayList<HashMap<String, Object>>) hashmap.get(alias);
        } else {
            hashmap.put(alias, templist);
        }
        return templist;
    }

    /**
     * <p>方法描述: 在处理from的过程中，如果当前from是子查询，继续向下寻找</p>
     * <p>@author: TBH </p>
     * <p>创建时间: 2020-04-10</p>
     * <p>参   数:  </p>
     */
    private void handleFrom2(SQLSelectQuery sqlSelectQuery) {
        //拆分union
        if (sqlSelectQuery instanceof SQLUnionQuery) {
            SQLUnionQuery sqlUnionQuery = (SQLUnionQuery) sqlSelectQuery;
            handleFrom2(sqlUnionQuery.getLeft());
            handleFrom2(sqlUnionQuery.getRight());
            //因为选择的是oracle的datatype 所以只考虑unionquery和oraclequeryblock这两种
        } else if (sqlSelectQuery instanceof OracleSelectQueryBlock) {
            OracleSelectQueryBlock oracleSelectQueryBlock = (OracleSelectQueryBlock) sqlSelectQuery;
            handleFrom(oracleSelectQueryBlock.getFrom());
        } else {
            String message;
            if (sqlSelectQuery == null) {
                message = "未知的SelectQuery";
            } else {
                message = "未知的SelectQuery来源：" + sqlSelectQuery.toString() + " class:" + sqlSelectQuery.getClass();
            }
            throw new BusinessSystemException(message);
        }
    }

    /**
     * <p>方法描述: 处理字段，如果是第一次，则记录字段，如果不是，则根据最底层关链上的字段，向上级寻找字段血缘关系</p>
     * <p>@author: TBH </p>
     * <p>创建时间: 2020-04-10</p>
     * <p>参   数:  </p>
     */
    private void startHandleFromColumn(OracleSelectQueryBlock oracleSelectQueryBlock, SQLTableSource sqlTableSource, String upperealias) {
        SQLTableSource from = oracleSelectQueryBlock.getFrom();
        Boolean isOracleSelectTableReference = oracleSelectQueryBlock.getFrom() instanceof OracleSelectTableReference;
        //表示第一次开始寻找
        if (listmap.isEmpty()) {
            if (sqlTableSource instanceof OracleSelectTableReference) {
                OracleSelectTableReference oracleSelectTableReference = (OracleSelectTableReference) sqlTableSource;
                List<SQLSelectItem> selectList = oracleSelectQueryBlock.getSelectList();
                for (SQLSelectItem sqlSelectItem : selectList) {
                    handleColumn(sqlSelectItem, oracleSelectTableReference, isOracleSelectTableReference);
                }
            }
        }
        //表示底层的已经完成寻找，开始寻找上层
        else {
            for (int i = 0; i < listmap.size(); i++) {
                HashMap<String, Object> stringObjectHashMap = listmap.get(i);
                List<String> uppercolumnlist = (ArrayList<String>) stringObjectHashMap.get("uppercolumn");
                List<String> newuppercolumnlist = new ArrayList<String>();
                for (int k = 0; k < uppercolumnlist.size(); k++) {
                    String uppercolumn = uppercolumnlist.get(k);
                    handleGetColumn(oracleSelectQueryBlock);
                    Boolean flag = true;
                    for (int j = 0; j < columnlist.size(); j++) {
                        SQLExpr sqlexpr = (SQLExpr) columnlist.get(j).get("column");
                        String alias = columnlist.get(j).get("alias").toString();
                        if (sqlexpr instanceof SQLIdentifierExpr) {
                            if (uppercolumn.equalsIgnoreCase(sqlexpr.toString())) {
                                newuppercolumnlist.add(alias);
                                flag = false;
                            }
                        } else if (sqlexpr instanceof SQLPropertyExpr) {
                            SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) sqlexpr;
                            if (uppercolumn.equalsIgnoreCase(sqlPropertyExpr.getName())
                                    && sqlPropertyExpr.getOwner().toString().equalsIgnoreCase(upperealias)) {
                                newuppercolumnlist.add(alias);
                                flag = false;
                            }
                        }
                    }
                    if (flag) {
                        listmap.remove(i);
                    }
                }
                stringObjectHashMap.put("uppercolumn", newuppercolumnlist);
            }
        }
    }

    /**
     * <p>方法描述: 获取别名</p>
     * <p>@author: TBH </p>
     * <p>创建时间: 2020-04-10</p>
     * <p>参   数:  </p>
     */
    private String getAlias(SQLSelectItem sqlSelectItem) {
        String alias = sqlSelectItem.getAlias();
        if (StringUtils.isBlank(alias)) {
            if (sqlSelectItem.getExpr() instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) sqlSelectItem.getExpr();
                alias = sqlIdentifierExpr.toString();
            } else if (sqlSelectItem.getExpr() instanceof SQLPropertyExpr) {
                SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) sqlSelectItem.getExpr();
                alias = sqlPropertyExpr.getName();
            }
            //如果存在*，抛出错误
            else if (sqlSelectItem.getExpr() instanceof SQLAllColumnExpr) {
                throw new BusinessSystemException("请明确字段，禁止使用 * 代替字段");
            } else {
                throw new BusinessSystemException("请明确字段 " + sqlSelectItem.getExpr() + " 没有别名 ");
            }
        }
        return alias;
    }

    /**
     * <p>方法描述: 处理字段，放入listmap中</p>
     * <p>@author: TBH </p>
     * <p>创建时间: 2020-04-10</p>
     * <p>参   数:  </p>
     */
    private void handleColumn(SQLSelectItem sqlSelectItem, OracleSelectTableReference oracleSelectTableReference, Boolean isOracleSelectTableReference) {
        String alias = getAlias(sqlSelectItem);
        HashMap<String, Object> map = new HashMap<String, Object>();
        columnlist.clear();
        getcolumn(sqlSelectItem.getExpr(), sqlSelectItem.getAlias());
        for (int i = 0; i < columnlist.size(); i++) {
            SQLExpr column = (SQLExpr) columnlist.get(i).get("column");
            if (column instanceof SQLIdentifierExpr) {
                //如果是简单select 需要判断from的部分是否来自单表 如果不是，则需要明确字段来源
                if (isOracleSelectTableReference) {
                    SQLIdentifierExpr sqlIdentifierExprcolumn = (SQLIdentifierExpr) column;
                    map.put("columnname", sqlIdentifierExprcolumn.getName());
                } else {
                    throw new BusinessSystemException("请填写标准sql 字段" + column + "未知来源");
                }
            } else if (column instanceof SQLPropertyExpr) {
                SQLPropertyExpr sqlPropertyExprcolumn = (SQLPropertyExpr) column;
                String owner = sqlPropertyExprcolumn.getOwner().toString();
                String tablename = StringUtils.isEmpty(oracleSelectTableReference.getAlias()) ?
                        oracleSelectTableReference.getName().toString() : oracleSelectTableReference.getAlias();
                if (owner.trim().equalsIgnoreCase(tablename.trim())) {
                    map.put("columnname", sqlPropertyExprcolumn.getName());
                }
            } else {
                throw new BusinessSystemException("未知的column类型 column:" + column + " 类型：" + column.getClass());
            }
            map.put("table", oracleSelectTableReference.toString());
            List<String> list = new ArrayList<String>();
            list.add(alias);
            map.put("uppercolumn", list);
            listmap.add(map);
        }
    }

    /**
     * TODO 这个方法将迭代获取一个select表达式中所包含的所有字段，需要根据表达式的类型进行分类讨论
     * Druid提供大概40种Select类型，目前对其中的大部分进行处理，日后可能需要新增
     * 详情参见 com.alibaba.druid.sql.ast.expr;
     * <p>方法描述: 根据select表达式，获取字段</p>
     * <p>@author: TBH </p>
     * <p>创建时间: 2020-04-10</p>
     * <p>参   数:  </p>
     */
    public void getcolumn(SQLExpr sqlexpr, String alias) {
        if (null == sqlexpr) {
            return;
        }
        if (sqlexpr instanceof SQLIdentifierExpr) {
            //简单类型 例如：a
            HashMap<String, Object> map = new HashMap<>();
            map.put("column", sqlexpr);
            map.put("alias", alias);
            columnlist.add(map);
        } else if (sqlexpr instanceof SQLPropertyExpr) {
            //属于类型 例如：t1.a
            HashMap<String, Object> map = new HashMap<>();
            map.put("column", sqlexpr);
            map.put("alias", alias);
            columnlist.add(map);
        } else if (sqlexpr instanceof SQLAggregateExpr) {
            SQLAggregateExpr sqlAggregateExpr = (SQLAggregateExpr) sqlexpr;
            List<SQLExpr> arguments = sqlAggregateExpr.getArguments();
            for (SQLExpr sqlExpr : arguments) {
                getcolumn(sqlExpr, alias);
            }
        } else if (sqlexpr instanceof SQLAllColumnExpr) {
            return;
        } else if (sqlexpr instanceof SQLAllExpr) {
            throw new BusinessSystemException("SQLAllExpr未开发 有待开发");
        } else if (sqlexpr instanceof SQLAnyExpr) {
            throw new BusinessSystemException("SQLAnyExpr 有待开发");
        } else if (sqlexpr instanceof SQLArrayExpr) {
            throw new BusinessSystemException("SQLArrayExpr 有待开发");
        } else if (sqlexpr instanceof SQLBetweenExpr) {
            SQLBetweenExpr sqlBetweenExpr = (SQLBetweenExpr) sqlexpr;
            getcolumn(sqlBetweenExpr.getBeginExpr(), alias);
            getcolumn(sqlBetweenExpr.getTestExpr(), alias);
            getcolumn(sqlBetweenExpr.getEndExpr(), alias);
        } else if (sqlexpr instanceof SQLBinaryExpr) {
            return;
        } else if (sqlexpr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) sqlexpr;
            getcolumn(sqlBinaryOpExpr.getLeft(), alias);
            getcolumn(sqlBinaryOpExpr.getRight(), alias);
        } else if (sqlexpr instanceof SQLBinaryOpExprGroup) {
            throw new BusinessSystemException("SQLBinaryOpExprGroup 有待开发");
        } else if (sqlexpr instanceof SQLBooleanExpr) {
            return;
        } else if (sqlexpr instanceof SQLCaseExpr) {
            SQLCaseExpr sqlCaseExpr = (SQLCaseExpr) sqlexpr;
            List<SQLCaseExpr.Item> items = sqlCaseExpr.getItems();
            for (int i = 0; i < items.size(); i++) {
                SQLCaseExpr.Item item = items.get(i);
                getcolumn(item.getConditionExpr(), alias);
                getcolumn(item.getValueExpr(), alias);
            }
            SQLExpr elseExpr = sqlCaseExpr.getElseExpr();
            getcolumn(elseExpr, alias);
            SQLExpr valueExpr = sqlCaseExpr.getValueExpr();
            getcolumn(valueExpr, alias);
        } else if (sqlexpr instanceof SQLCaseStatement) {
            //不清楚与SQLCaseExpr有何区别
            throw new BusinessSystemException("SQLCaseStatement 有待开发");
        } else if (sqlexpr instanceof SQLCastExpr) {
            SQLCastExpr sqlCastExpr = (SQLCastExpr) sqlexpr;
            getcolumn(sqlCastExpr.getExpr(), alias);
        } else if (sqlexpr instanceof SQLCharExpr) {
            return;
        } else if (sqlexpr instanceof SQLContainsExpr) {
            SQLContainsExpr sqlContainsExpr = (SQLContainsExpr) sqlexpr;
            getcolumn(sqlContainsExpr.getExpr(), alias);
            List<SQLExpr> targetList = sqlContainsExpr.getTargetList();
            for (SQLExpr sqlExpr : targetList) {
                getcolumn(sqlExpr, alias);
            }
        } else if (sqlexpr instanceof SQLCurrentOfCursorExpr) {
            return;
        } else if (sqlexpr instanceof SQLDateExpr) {
            throw new BusinessSystemException("SQLDateExpr 有待开发");
        } else if (sqlexpr instanceof SQLExistsExpr) {
            throw new BusinessSystemException("SQLExistsExpr 有待开发");
        } else if (sqlexpr instanceof SQLExprUtils) {
            throw new BusinessSystemException("SQLExprUtils 有待开发");
        } else if (sqlexpr instanceof SQLFlashbackExpr) {
            throw new BusinessSystemException("SQLFlashbackExpr 有待开发");
        } else if (sqlexpr instanceof SQLGroupingSetExpr) {
            SQLGroupingSetExpr sqlGroupingSetExpr = (SQLGroupingSetExpr) sqlexpr;
            List<SQLExpr> parameters = sqlGroupingSetExpr.getParameters();
            for (SQLExpr sqlExpr : parameters) {
                getcolumn(sqlexpr, alias);
            }
        } else if (sqlexpr instanceof SQLHexExpr) {
            return;
        } else if (sqlexpr instanceof SQLInListExpr) {
            SQLInListExpr sqlInListExpr = (SQLInListExpr) sqlexpr;
            getcolumn(sqlInListExpr.getExpr(), alias);
            List<SQLExpr> targetList = sqlInListExpr.getTargetList();
            for (SQLExpr sqlExpr : targetList) {
                getcolumn(sqlExpr, alias);
            }
        } else if (sqlexpr instanceof SQLInSubQueryExpr) {
            SQLInSubQueryExpr sqlInSubQueryExpr = (SQLInSubQueryExpr) sqlexpr;
            getcolumn(sqlInSubQueryExpr.getExpr(), alias);
        } else if (sqlexpr instanceof SQLIntegerExpr) {
            return;
        } else if (sqlexpr instanceof SQLIntervalExpr) {
            return;
        } else if (sqlexpr instanceof SQLListExpr) {
            SQLListExpr sqlListExpr = (SQLListExpr) sqlexpr;
            List<SQLExpr> items = sqlListExpr.getItems();
            for (SQLExpr sqlExpr : items) {
                getcolumn(sqlExpr, alias);
            }
        } else if (sqlexpr instanceof SQLMethodInvokeExpr) {
            SQLMethodInvokeExpr sqlMethodInvokeExpr = (SQLMethodInvokeExpr) sqlexpr;
            List<SQLExpr> parameters = sqlMethodInvokeExpr.getParameters();
            for (SQLExpr sqlExpr : parameters) {
                getcolumn(sqlExpr, alias);
            }
        } else if (sqlexpr instanceof SQLNCharExpr) {
            return;
        } else if (sqlexpr instanceof SQLNotExpr) {
            SQLNotExpr sqlNotExpr = (SQLNotExpr) sqlexpr;
            getcolumn(sqlNotExpr.getExpr(), alias);
        } else if (sqlexpr instanceof SQLNullExpr) {
            return;
        } else if (sqlexpr instanceof SQLNumberExpr) {
            return;
        } else if (sqlexpr instanceof SQLQueryExpr) {
            throw new BusinessSystemException("SQLQueryExpr 有待开发");
        } else if (sqlexpr instanceof SQLRealExpr) {
            return;
        } else if (sqlexpr instanceof SQLSequenceExpr) {
            throw new BusinessSystemException("SQLSequenceExpr 有待开发");
        } else if (sqlexpr instanceof SQLSomeExpr) {
            throw new BusinessSystemException("SQLSomeExpr 有待开发");
        } else if (sqlexpr instanceof SQLTextLiteralExpr) {
            return;
        } else if (sqlexpr instanceof SQLTimestampExpr) {
            return;
        } else if (sqlexpr instanceof SQLUnaryExpr) {
            SQLUnaryExpr sqlUnaryExpr = (SQLUnaryExpr) sqlexpr;
            getcolumn(sqlUnaryExpr.getExpr(), alias);
        } else if (sqlexpr instanceof SQLValuesExpr) {
            SQLValuesExpr sqlValuesExpr = (SQLValuesExpr) sqlexpr;
            List<SQLListExpr> values = sqlValuesExpr.getValues();
            for (SQLListExpr sqlListExpr : values) {
                getcolumn(sqlListExpr, alias);
            }
        } else if (sqlexpr instanceof SQLVariantRefExpr) {
            return;
        } else {
            throw new BusinessSystemException("未知的sqlexpr类型 sqlexpr：" + sqlexpr.toString() + "class:" + sqlexpr.getClass());
        }
    }

    /**
     * <p>方法描述: 处理sql,用于比较sqlobject.parent是否等于mainsql时用</p>
     * <p>@author: TBH </p>
     * <p>创建时间: 2020-04-10</p>
     * <p>参   数:  </p>
     */
    private String trim(String sql) {
        sql = sql.replace("\r", "");
        sql = sql.replace("\n", "");
        sql = sql.replace("\r\n", "");
        sql = sql.replace("\t", "");
        sql = sql.replace(" ", "");
        sql = sql.trim();
        return sql;
    }

    /**
     * <p>方法描述: 处理字段，根据查询块获取selectlist,然后循环处理每一个字段</p>
     * <p>@author: TBH </p>
     * <p>创建时间: 2020-04-10</p>
     * <p>参   数:  </p>
     */
    private void handleGetColumn(OracleSelectQueryBlock oracleSelectQueryBlock) {
        List<SQLSelectItem> selectList = oracleSelectQueryBlock.getSelectList();
        columnlist.clear();
        for (SQLSelectItem sqlSelectItem : selectList) {
            String alias = getAlias(sqlSelectItem);
            getcolumn(sqlSelectItem.getExpr(), alias);
        }
    }

    /**
     * 判断视图
     *
     * @param sql
     * @return
     */
    public String GetNewSql(String sql) {
        String dbType = JdbcConstants.ORACLE;
        List<String> sqllist = new ArrayList<>();
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            for (SQLStatement stmt : stmtList) {
                SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) stmt;
                SQLSelect sqlSelect = sqlSelectStatement.getSelect();
                SQLSelectQuery sqlSelectQuery = sqlSelect.getQuery();
                setFrom(sqlSelectQuery, db);
                sqllist.add(sqlSelectQuery.toString());
            }
        }
        return sqllist.get(0);

    }

    /**
     * 遍历每一个query的部分用以获取from
     *
     * @param sqlSelectQuery
     */
    private void setFrom(SQLSelectQuery sqlSelectQuery, DatabaseWrapper db) {
        if (sqlSelectQuery instanceof SQLUnionQuery) {
            SQLUnionQuery sqlUnionQuery = (SQLUnionQuery) sqlSelectQuery;
            setFrom(sqlUnionQuery.getLeft(), db);
            setFrom(sqlUnionQuery.getRight(), db);
            // 因为选择的是oracle的datatype 所以只考虑unionquery和oraclequeryblock这两种
        } else if (sqlSelectQuery instanceof OracleSelectQueryBlock) {
            OracleSelectQueryBlock oracleSelectQueryBlock = (OracleSelectQueryBlock) sqlSelectQuery;
            handleSetFrom(oracleSelectQueryBlock.getFrom(), db);
        } else {
            String message;
            if (sqlSelectQuery == null) {
                message = "未知的SelectQuery";
            } else {
                message = "未知的SelectQuery来源：" + sqlSelectQuery.toString() + " class:" + sqlSelectQuery.getClass();
            }
        }
    }

    /**
     * 拆分from 直至单表
     *
     * @param sqlTableSource
     */
    private void handleSetFrom(SQLTableSource sqlTableSource, DatabaseWrapper db) {
        // 如果是join的形式 就递归继续拆分
        if (sqlTableSource instanceof OracleSelectJoin) {
            OracleSelectJoin oracleSelectJoin = (OracleSelectJoin) sqlTableSource;
            handleSetFrom(oracleSelectJoin.getLeft(), db);
            handleSetFrom(oracleSelectJoin.getRight(), db);
        }
        // 如果是子查询，继续拆分from
        else if (sqlTableSource instanceof OracleSelectSubqueryTableSource) {
            OracleSelectSubqueryTableSource oracleSelectSubqueryTableSource = (OracleSelectSubqueryTableSource) sqlTableSource;
            SQLSelect sqlSelect = oracleSelectSubqueryTableSource.getSelect();
            SQLSelectQuery sqlSelectQuery = sqlSelect.getQuery();
            setFrom(sqlSelectQuery, db);
        } else if (sqlTableSource instanceof OracleSelectTableReference) {
            OracleSelectTableReference oracleSelectTableReference = (OracleSelectTableReference) sqlTableSource;
            String tablename = oracleSelectTableReference.getExpr().toString();
            List<Map<String, Object>> maps = SqlOperator.queryList(db, "select t2.execute_sql from " + Dm_datatable.TableName + " t1 left join " + Dm_operation_info.TableName +
                            " t2 on t1.datatable_id = t2.datatable_id where lower(t1.datatable_en_name) = ? and t1.table_storage = ?",
                    tablename.toLowerCase(), TableStorage.ShuJuShiTu.getCode());
            if (!maps.isEmpty()) {
                String execute_sql = maps.get(0).get("execute_sql").toString();
                oracleSelectTableReference.setExpr(" ( " + execute_sql + " ) " + tablename);
            }
        } else {
            String message;
            if (sqlTableSource == null) {
                message = "sqlTableSource";
            } else {
                message = "未知的sqlTableSource来源：" + sqlTableSource.toString() + " class:" + sqlTableSource.getClass();
            }
        }
    }


    public static String getInDeUpSqlTableName(String sql) {
        String tablename = "";
        String dbType = JdbcConstants.ORACLE;
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        for (SQLStatement stmt : stmtList) {
            if (stmt instanceof OracleUpdateStatement) {
                OracleUpdateStatement oracleUpdateStatement = (OracleUpdateStatement) stmt;
                SQLName tableName = oracleUpdateStatement.getTableName();
                tablename = tableName.toString();
            } else if (stmt instanceof OracleInsertStatement) {
                OracleInsertStatement oracleInsertStatement = (OracleInsertStatement) stmt;
                SQLName tableName = oracleInsertStatement.getTableName();
                tablename = tableName.toString();
            } else if (stmt instanceof OracleDeleteStatement) {
                OracleDeleteStatement oracleDeleteStatement = (OracleDeleteStatement) stmt;
                SQLName tableName = oracleDeleteStatement.getTableName();
                tablename = tableName.toString();
            } else {
                throw new BusinessException("SQL非Delete,Update或者Insert中的一种，请检查");
            }
        }
        return tablename;
    }
}
