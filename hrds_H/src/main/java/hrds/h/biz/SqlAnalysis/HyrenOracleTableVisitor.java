//package hrds.h.biz.SqlAnalysis;
//
//
//import com.alibaba.druid.sql.SQLUtils;
//import com.alibaba.druid.sql.ast.SQLExpr;
//import com.alibaba.druid.sql.ast.SQLExprImpl;
//import com.alibaba.druid.sql.ast.SQLObject;
//import com.alibaba.druid.sql.ast.SQLStatement;
//import com.alibaba.druid.sql.ast.expr.*;
//import com.alibaba.druid.sql.ast.statement.*;
//import com.alibaba.druid.sql.dialect.oracle.ast.stmt.*;
//import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
//import com.alibaba.druid.util.JdbcConstants;
//import fd.ng.core.exception.BusinessSystemException;
//import org.apache.calcite.sql.SqlBinaryOperator;
//import org.apache.calcite.sql.SqlSelect;
//import org.apache.commons.lang.StringUtils;
//import org.apache.http.auth.BasicUserPrincipal;
//
//import java.util.*;
//
//
//public class HyrenOracleTableVisitor extends OracleASTVisitorAdapter {
//
//
//    private  Map<String, HashMap<String, String>> resultmap = new HashMap<String, HashMap<String, String>>();
//
//    private  List<HashMap<String, Object>> listmap = new ArrayList<HashMap<String, Object>>();
//    private  List<HashMap<String, Object>> columnlist = new ArrayList<HashMap<String, Object>>();
//    private  HashMap<String, Object> hashmap = new HashMap<String, Object>();
//    private  String mainSql = new String();
//
//    public  void main(String[] args) {
//        String sql = "select a,b,c,d,e from (select t1.a,t1.b,t2.c,t2.d,t3.e from (select a,b from table_B ) t1 , TableB t2,TableC t3 ) t4";
////        String sql = "SELECT CBS_CUST_ID FROM CCS_CCS_CAS_CREDITLEVEL,TableB";
////        String sql = "select a,b,c,d from (select b from table_0) table_1,(select c from table_2," +
////                "(select d from table_21 union select e from table_union) table_22  union select a from table_3  ) table_3 union select a from table_4";
//        getBloodRelationMap(sql);
//    }
//
//
//    public  HashMap<String, Object> getBloodRelationMap(String sql){
//        this.listmap.clear();
//        this.columnlist.clear();
//        this.hashmap.clear();
//        this.mainSql = new String();
//        mainSql = sql;
//        String dbType = JdbcConstants.ORACLE;
//        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
//        for (SQLStatement stmt : stmtList) {
//            SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) stmt;
//            SQLSelect sqlSelect = sqlSelectStatement.getSelect();
//            SQLSelectQuery sqlSelectQuery = sqlSelect.getQuery();
//            OracleSelectQueryBlock oracleSelectQueryBlock = (OracleSelectQueryBlock) sqlSelectQuery;
//            getBloodRelation(sqlSelectQuery);
//        }
//        return hashmap;
//    }
//    private  void getBloodRelation(SQLSelectQuery sqlSelectQuery) {
//        //拆分union
//        if (sqlSelectQuery instanceof SQLUnionQuery) {
//            SQLUnionQuery sqlUnionQuery = (SQLUnionQuery) sqlSelectQuery;
//            getBloodRelation(sqlUnionQuery.getLeft());
//            getBloodRelation(sqlUnionQuery.getRight());
//            //因为选择的是oracle的datatype 所以只考虑unionquery和oraclequeryblock这两种
//        } else if (sqlSelectQuery instanceof OracleSelectQueryBlock) {
//            OracleSelectQueryBlock oracleSelectQueryBlock = (OracleSelectQueryBlock) sqlSelectQuery;
//            //开始处理from的部分
//            handleFrom(oracleSelectQueryBlock.getFrom());
//        } else {
//            throw new BusinessSystemException("未知的sqlSelectQuery：" + sqlSelectQuery.toString() + " class:" + sqlSelectQuery.getClass());
//        }
//    }
//
//    private  void handleFrom(SQLTableSource sqlTableSource) {
//        //如果是join的形式 就递归继续拆分
//        if (sqlTableSource instanceof OracleSelectJoin) {
//            OracleSelectJoin oracleSelectJoin = (OracleSelectJoin) sqlTableSource;
//            handleFrom(oracleSelectJoin.getLeft());
//            handleFrom(oracleSelectJoin.getRight());
//        }
//        //如果是子查询，就记录字段，继续拆分from
//        else if (sqlTableSource instanceof OracleSelectSubqueryTableSource) {
//            OracleSelectSubqueryTableSource oracleSelectSubqueryTableSource = (OracleSelectSubqueryTableSource) sqlTableSource;
//            SQLSelect sqlSelect = oracleSelectSubqueryTableSource.getSelect();
//            SQLSelectQuery sqlSelectQuery = sqlSelect.getQuery();
//            handleFrom2(sqlSelectQuery);
//        }
//        //如果是单表的话 那么单表的parent一定是最简单的查询
//        else if (sqlTableSource instanceof OracleSelectTableReference) {
//            OracleSelectTableReference oracleSelectTableReference = (OracleSelectTableReference) sqlTableSource;
//            SQLObject sqlObject = oracleSelectTableReference.getParent();
//            while (sqlObject instanceof OracleSelectQueryBlock == false) {
//                sqlObject = sqlObject.getParent();
//            }
//            listmap.clear();
//            String upperealias = new String();
//            while (!trim(sqlObject.toString()).equalsIgnoreCase(trim(mainSql))) {
//                OracleSelectQueryBlock oracleSelectQueryBlock = (OracleSelectQueryBlock) sqlObject;
//                startHandleFromColumn(oracleSelectQueryBlock, sqlTableSource, upperealias);
//                sqlObject = sqlObject.getParent();
//                if (sqlObject.getParent() instanceof OracleSelectSubqueryTableSource) {
//                    OracleSelectSubqueryTableSource oracleSelectSubqueryTableSource = (OracleSelectSubqueryTableSource) sqlObject.getParent();
//                    upperealias = oracleSelectSubqueryTableSource.getAlias();
//                }
//                while (sqlObject instanceof OracleSelectQueryBlock == false) {
//                    sqlObject = sqlObject.getParent();
//                }
//            }
//            OracleSelectQueryBlock oracleSelectQueryBlock = (OracleSelectQueryBlock) sqlObject;
//            handleGetColumn(oracleSelectQueryBlock);
//            //如果listmap为空，说明这个table是最外层的，直接去寻找最外层selectexpr和该表关链的部分
//            if (listmap.isEmpty()) {
//                for (int j = 0; j < columnlist.size(); j++) {
//                    SQLExpr sqlexpr = (SQLExpr) columnlist.get(j).get("column");
//                    String alias = columnlist.get(j).get("alias").toString();
//                    ArrayList<HashMap<String, Object>> templist = getTempList(alias);
//                    if (sqlexpr instanceof SQLIdentifierExpr) {
//                        putResult(templist, alias, sqlexpr.toString(), sqlTableSource.toString());
//                    } else if (sqlexpr instanceof SQLPropertyExpr) {
//                        SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) sqlexpr;
//                        putResult(templist, alias, sqlPropertyExpr.getName(), sqlTableSource.toString());
//                    }
//                }
//            } else {
//                for (int i = 0; i < listmap.size(); i++) {
//                    HashMap<String, Object> stringObjectHashMap = listmap.get(i);
//                    List<String> uppercolumnlist = (ArrayList<String>) stringObjectHashMap.get("uppercolumn");
//                    for (String uppercolumn : uppercolumnlist) {
//                        for (int j = 0; j < columnlist.size(); j++) {
//                            SQLExpr sqlexpr = (SQLExpr) columnlist.get(j).get("column");
//                            String alias = columnlist.get(j).get("alias").toString();
//                            ArrayList<HashMap<String, Object>> templist = getTempList(alias);
//                            if (sqlexpr instanceof SQLIdentifierExpr) {
//                                if (uppercolumn.equalsIgnoreCase(sqlexpr.toString())) {
//                                    putResult(templist, alias, stringObjectHashMap.get("columnname") == null ? null : stringObjectHashMap.get("columnname").toString(),
//                                            stringObjectHashMap.get("table").toString());
//                                }
//                            } else if (sqlexpr instanceof SQLPropertyExpr) {
//                                SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) sqlexpr;
//                                if (uppercolumn.equalsIgnoreCase(sqlPropertyExpr.getName())
//                                        && sqlPropertyExpr.getOwner().toString().equalsIgnoreCase(upperealias)) {
//                                    putResult(templist, alias, stringObjectHashMap.get("columnname") == null ? null : stringObjectHashMap.get("columnname").toString(),
//                                            stringObjectHashMap.get("table").toString());
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } else {
//            throw new BusinessSystemException("未知的sqlTableSource：" + sqlTableSource.toString() + " class:" + sqlTableSource.getClass());
//        }
//    }
//
//    private  void putResult(ArrayList<HashMap<String, Object>> templist, String alias, String columnname, String table) {
//        if (columnname != null) {
//            HashMap<String, Object> temphashmap = new HashMap<String, Object>();
//            temphashmap.put("sourcecolumn", columnname);
//            temphashmap.put("sourcetable", table);
//            templist.add(temphashmap);
//            System.out.println(alias + " " + "sourcecolumn:" + columnname + " sourcetable:" + table);
//        }
//    }
//
//    private  ArrayList<HashMap<String, Object>> getTempList(String alias) {
//        ArrayList<HashMap<String, Object>> templist = new ArrayList<HashMap<String, Object>>();
//        if (hashmap.get(alias) != null) {
//            templist = (ArrayList<HashMap<String, Object>>) hashmap.get(alias);
//        } else {
//            hashmap.put(alias, templist);
//        }
//        return templist;
//    }
//
//    private  void handleFrom2(SQLSelectQuery sqlSelectQuery) {
//        //拆分union
//        if (sqlSelectQuery instanceof SQLUnionQuery) {
//            SQLUnionQuery sqlUnionQuery = (SQLUnionQuery) sqlSelectQuery;
//            handleFrom2(sqlUnionQuery.getLeft());
//            handleFrom2(sqlUnionQuery.getRight());
//            //因为选择的是oracle的datatype 所以只考虑unionquery和oraclequeryblock这两种
//        } else if (sqlSelectQuery instanceof OracleSelectQueryBlock) {
//            OracleSelectQueryBlock oracleSelectQueryBlock = (OracleSelectQueryBlock) sqlSelectQuery;
//            handleFrom(oracleSelectQueryBlock.getFrom());
//        } else {
//            throw new BusinessSystemException("未知的sqlSelectQuery：" + sqlSelectQuery.toString() + " class:" + sqlSelectQuery.getClass());
//        }
//    }
//
//    private  void startHandleFromColumn(OracleSelectQueryBlock oracleSelectQueryBlock, SQLTableSource sqlTableSource, String upperealias) {
//        SQLTableSource from = oracleSelectQueryBlock.getFrom();
//        Boolean isOracleSelectTableReference = true;
//        if (from instanceof OracleSelectTableReference) {
//            isOracleSelectTableReference = true;
//        } else if (from instanceof OracleSelectJoin) {
//            isOracleSelectTableReference = false;
//        } else {
//            throw new BusinessSystemException("未知的from类型");
//        }
//        //表示第一次开始寻找
//        if (listmap.isEmpty()) {
//            if (sqlTableSource instanceof OracleSelectTableReference) {
//                OracleSelectTableReference oracleSelectTableReference = (OracleSelectTableReference) sqlTableSource;
//                List<SQLSelectItem> selectList = oracleSelectQueryBlock.getSelectList();
//                for (SQLSelectItem sqlSelectItem : selectList) {
//                    handleColumn(sqlSelectItem, oracleSelectTableReference, isOracleSelectTableReference);
//                }
//            }
//        }
//        //表示底层的已经完成寻找，开始寻找上层
//        else {
//            for (int i = 0; i < listmap.size(); i++) {
//                HashMap<String, Object> stringObjectHashMap = listmap.get(i);
//                List<String> uppercolumnlist = (ArrayList<String>) stringObjectHashMap.get("uppercolumn");
//                List<String> newuppercolumnlist = new ArrayList<String>();
//                for (int k = 0; k < uppercolumnlist.size(); k++) {
//                    String uppercolumn = uppercolumnlist.get(k);
//                    handleGetColumn(oracleSelectQueryBlock);
//                    Boolean flag = true;
//                    for (int j = 0; j < columnlist.size(); j++) {
//                        SQLExpr sqlexpr = (SQLExpr) columnlist.get(j).get("column");
//                        String alias = columnlist.get(j).get("alias").toString();
//                        if (sqlexpr instanceof SQLIdentifierExpr) {
//                            if (uppercolumn.equalsIgnoreCase(sqlexpr.toString())) {
//                                newuppercolumnlist.add(alias);
//                                flag = false;
//                            }
//                        } else if (sqlexpr instanceof SQLPropertyExpr) {
//                            SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) sqlexpr;
//                            if (uppercolumn.equalsIgnoreCase(sqlPropertyExpr.getName())
//                                    && sqlPropertyExpr.getOwner().toString().equalsIgnoreCase(upperealias)) {
//                                newuppercolumnlist.add(alias);
//                                flag = false;
//                            }
//                        }
//                    }
//                    if (flag) {
//                        listmap.remove(i);
//                    }
//                }
//                stringObjectHashMap.put("uppercolumn", newuppercolumnlist);
//            }
//        }
//    }
//
//    private  String getAlias(SQLSelectItem sqlSelectItem) {
//        String alias = sqlSelectItem.getAlias();
//        if (StringUtils.isBlank(alias)) {
//            if (sqlSelectItem.getExpr() instanceof SQLIdentifierExpr) {
//                SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) sqlSelectItem.getExpr();
//                alias = sqlIdentifierExpr.toString();
//            } else if (sqlSelectItem.getExpr() instanceof SQLPropertyExpr) {
//                SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) sqlSelectItem.getExpr();
//                alias = sqlPropertyExpr.getName();
//            }
//            //如果存在*，抛出错误
//            else if (sqlSelectItem.getExpr() instanceof SQLAllColumnExpr) {
//                throw new BusinessSystemException("请明确字段，禁止使用 * 代替字段");
//            } else {
//                throw new BusinessSystemException("请明确字段 " + sqlSelectItem.getExpr() + " 没有别名 ");
//            }
//        }
//        return alias;
//    }
//
//    private  void handleColumn(SQLSelectItem sqlSelectItem, OracleSelectTableReference oracleSelectTableReference, Boolean isOracleSelectTableReference) {
//        String alias = getAlias(sqlSelectItem);
//        HashMap<String, Object> map = new HashMap<String, Object>();
//        columnlist.clear();
//        String tablename = StringUtils.isEmpty(oracleSelectTableReference.getAlias()) ? oracleSelectTableReference.getName().toString() : oracleSelectTableReference.getAlias();
//        getcolumn(sqlSelectItem.getExpr(), sqlSelectItem.getAlias());
//        for (int i = 0; i < columnlist.size(); i++) {
//            SQLExpr column = (SQLExpr) columnlist.get(i).get("column");
//            if (column instanceof SQLIdentifierExpr) {
//                if (isOracleSelectTableReference) {
//                    SQLIdentifierExpr sqlIdentifierExprcolumn = (SQLIdentifierExpr) column;
//                    map.put("columnname", sqlIdentifierExprcolumn.getName());
//                } else {
//                    throw new BusinessSystemException("请填写标准sql");
//                }
//            } else if (column instanceof SQLPropertyExpr) {
//                SQLPropertyExpr sqlPropertyExprcolumn = (SQLPropertyExpr) column;
//                String owner = sqlPropertyExprcolumn.getOwner().toString();
//                if (owner.trim().equalsIgnoreCase(tablename.trim())) {
//                    map.put("columnname", sqlPropertyExprcolumn.getName());
//                }
//            } else {
//                throw new BusinessSystemException("有问题");
//            }
//            map.put("table", oracleSelectTableReference.toString());
//            List<String> list = new ArrayList<String>();
//            list.add(alias);
//            map.put("uppercolumn", list);
//            listmap.add(map);
//        }
//    }
//
//    private  void getcolumn(SQLExpr sqlexpr, String alias) {
//        if (sqlexpr instanceof SQLIdentifierExpr) {
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("column", sqlexpr);
//            map.put("alias", alias);
//            columnlist.add(map);
//        } else if (sqlexpr instanceof SQLPropertyExpr) {
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("column", sqlexpr);
//            map.put("alias", alias);
//            columnlist.add(map);
//        }
//    }
//
//
//    //迭代来获取每一个查询表达式中，包含的所有字段放到AllSelectColumnList中
//    private void getColumnName(SQLExpr expr) {
//        if (expr instanceof SQLAggregateExpr) {
//
//        } else if (expr instanceof SQLAllColumnExpr) {
//
//        } else if (expr instanceof SQLAllExpr) {
//
//        } else if (expr instanceof SQLAnyExpr) {
//
//        } else if (expr instanceof SQLArrayExpr) {
//
//        } else if (expr instanceof SQLBetweenExpr) {
//
//        } else if (expr instanceof SQLBinaryExpr) {
//
//        } else if (expr instanceof SQLBinaryOpExpr) {
//
//        } else if (expr instanceof SQLBinaryOpExprGroup) {
//
//        } else if (expr instanceof SQLBooleanExpr) {
//
//        } else if (expr instanceof SQLCaseExpr) {
//
//        } else if (expr instanceof SQLCaseStatement) {
//
//        } else if (expr instanceof SQLCastExpr) {
//
//        } else if (expr instanceof SQLCharExpr) {
//
//        } else if (expr instanceof SQLContainsExpr) {
//
//        } else if (expr instanceof SQLCurrentOfCursorExpr) {
//
//        } else if (expr instanceof SQLDateExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLExistsExpr) {
//
//        } else if (expr instanceof SQLExprUtils) {
//
//        } else if (expr instanceof SQLFlashbackExpr) {
//
//        } else if (expr instanceof SQLGroupingSetExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
//        } else if (expr instanceof SQLDefaultExpr) {
//
////        } else if (expr instanceof SqlBinaryOperator) {
////            AllSelectColumnList.add(expr.toString());
////        } else if (expr instanceof SQLPropertyExpr) {
////            AllSelectColumnList.add(expr.toString());
////        } else if (expr instanceof SQLIntegerExpr) {
////            //如果是数字，则不做任何事
////        } else if (expr instanceof SQLCharExpr) {
////            //如果是字母，则不做任何事
//
//        }
//    }
//
//    private  String trim(String sql) {
//        sql = sql.replace("\r", "");
//        sql = sql.replace("\n", "");
//        sql = sql.replace("\r\n", "");
//        sql = sql.replace("\t", "");
//        sql = sql.replace(" ", "");
//        sql = sql.trim();
//        return sql;
//    }
//
//    private  void handleGetColumn(OracleSelectQueryBlock oracleSelectQueryBlock) {
//        List<SQLSelectItem> selectList = oracleSelectQueryBlock.getSelectList();
//        columnlist.clear();
//        for (SQLSelectItem sqlSelectItem : selectList) {
//            String alias = getAlias(sqlSelectItem);
//            getcolumn(sqlSelectItem.getExpr(), alias);
//        }
//    }
//}