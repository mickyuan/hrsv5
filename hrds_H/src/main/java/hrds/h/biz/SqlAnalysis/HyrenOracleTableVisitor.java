package hrds.h.biz.SqlAnalysis;


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.*;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import com.alibaba.druid.util.JdbcConstants;
import fd.ng.core.exception.BusinessSystemException;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HyrenOracleTableVisitor extends OracleASTVisitorAdapter {

    private List<String> tablelist = new ArrayList<>();
    private List<Map<String,Object>> maplist = new ArrayList<Map<String,Object>>();
    private List<String> columnNameList = new ArrayList<String>();
    private int visitqueryblocktimes ;

    public int getVisitqueryblocktimes() {
        return visitqueryblocktimes;
    }

    public void setVisitqueryblocktimes(int visitqueryblocktimes) {
        this.visitqueryblocktimes = visitqueryblocktimes;
    }

    public List<String> getColumnNameList() {
        return columnNameList;
    }

    public void setColumnNameList(List<String> columnNameList) {
        this.columnNameList = columnNameList;
    }

    //用来分隔union
    public boolean visit(OracleSelectQueryBlock x) {
        if (visitqueryblocktimes == 0) {
            List<SQLSelectItem> selectList = x.getSelectList();
            getSelectPart(selectList);
            visitqueryblocktimes++;
        }
        return true;
    }

    public boolean visit(OracleSelectSubqueryTableSource x) {

        return true;
    }

    //用来分割每个union部分的 join
    public boolean visit(OracleSelectJoin x) {
        System.out.println(x);
        System.out.println(x.getLeft());
        System.out.println(x.getRight());
        System.out.println(" ");

        return true;
    }


    //获取所有表名
    public boolean visit(OracleSelectTableReference x) {
        tablelist.add(x.getExpr().toString());
//        System.out.println(tablelist);
        return true;
    }

    private void getSelectPart(List<SQLSelectItem> selectList) {
        for (SQLSelectItem SelectItem : selectList) {
            SQLExpr expr = SelectItem.getExpr();
            //如果有别名 直接取别名作为字段最终名称
            if (!StringUtils.isBlank(SelectItem.getAlias())) {
                columnNameList.add(SelectItem.getAlias());
            }
            //如果没有别名，则判断
            else {
                //如果是单字段，那么字段名就是
                if (SelectItem.getExpr() instanceof SQLIdentifierExpr) {
                    columnNameList.add(SelectItem.getExpr().toString());
                }
                //如果存在*，抛出错误
                else if (SelectItem.getExpr() instanceof SQLAllColumnExpr) {
                    throw new BusinessSystemException("请明确字段，禁止使用 * 代替字段");
                } else {
                    throw new BusinessSystemException("请明确字段 " + SelectItem.getExpr() + " 没有别名 ");
                }
            }
        }
    }


    public static void main(String[] args) {
        String sql = "select a from (select b from table_0) table_1 left join (select c from table_2) table_3 left join table_4 union " +
                "select a  from table_5 union select a from table_6 union all select a from table_7 union select a from " +
                "table_8 ";//
        String dbType = JdbcConstants.ORACLE;
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        HyrenOracleTableVisitor visitor = new HyrenOracleTableVisitor();
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }
    }

}