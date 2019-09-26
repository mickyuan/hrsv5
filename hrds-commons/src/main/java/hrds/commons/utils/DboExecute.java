package hrds.commons.utils;

import fd.ng.web.helper.HttpDataHolder;
import fd.ng.web.util.Dbo;
import hrds.commons.exception.BusinessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class DboExecute {
	private static final Logger logger = LogManager.getLogger();

	/**
	 * 更新一条数据。比如：基于主键的更新。
	 * @param errorMsg 出错后的提示信息，抛异常出去。
	 * @param sql
	 * @param params
	 */
	public static void updatesOrThrow(final String errorMsg,
	                                  final String sql, Object... params) {
		updatesOrThrow(1, errorMsg, sql, params);
	}

	/**
	 * 更新一条数据。比如：基于主键的更新。
	 * 出错后的提示信息(默认提示“数据修改失败，修改了0条或多条数据")，抛异常出去。
	 * @param sql
	 * @param params
	 */
	public static void updatesOrThrowNoMsg(final String sql, Object... params) {
		updatesOrThrow(1, "数据修改失败，修改了0条或多条数据", sql, params);
	}

	/**
	 * 更新多条数据。
	 * @param expectedUpdateNums 预期被更新的数量
	 * @param errorMsg 出错后的提示信息，抛异常出去。
	 * @param sql
	 * @param params
	 */
	public static void updatesOrThrow(final int expectedUpdateNums, final String errorMsg,
	                                  final String sql, Object... params) {
		int nums = Dbo.execute(sql, params);
		if(nums==expectedUpdateNums) return;

		String goodSql = formatSQL(sql, params);
		logger.error(String.format(
				"%s Illegal update for SQL[%s], expected %d but %d", HttpDataHolder.getBizId(), goodSql, expectedUpdateNums, nums));
		if(nums==0) {
			throw new BusinessException(errorMsg==null?"update nothing!":errorMsg);
		} else {
			throw new BusinessException(errorMsg==null?"Illegal update!":errorMsg);
		}
	}

	/**
	 * 更新多条数据。
	 * 出错后的提示信息(默认提示“数据修改失败”)，抛异常出去。
	 * @param expectedUpdateNums 预期被更新的数量
	 * @param sql
	 * @param params
	 */
	public static void updatesOrThrowNoMsg(final int expectedUpdateNums,
	                                  final String sql, Object... params) {
		updatesOrThrow(expectedUpdateNums, "数据修改失败", sql, params);
	}

	/**
	 * 插入一条数据。
	 * @param errorMsg 出错后的提示信息，抛异常出去。
	 * @param sql
	 * @param params
	 */
	public static void insertsOrThrow(final String errorMsg,
	                                  final String sql, Object... params) {
		int nums = Dbo.execute(sql, params);
		if(nums==1) return;

		String goodSql = formatSQL(sql, params);
		logger.error(String.format(
				"%s Illegal insert for SQL[%s], expected %d but %d", HttpDataHolder.getBizId(), goodSql, 1, nums));
		if(nums==0) {
			throw new BusinessException(errorMsg==null?"insert nothing!":errorMsg);
		} else {
			throw new BusinessException(errorMsg==null?"Too many insert!":errorMsg);
		}
	}
	/**
	 * 插入一条数据。
	 * 出错后的提示信息(默认提"数据新增失败，返回数据不等于1")，抛异常出去。
	 * @param sql
	 * @param params
	 */
	public static void insertsOrThrowNoMsg(final String sql, Object... params) {
		insertsOrThrow("数据新增失败，返回数据不等于1",sql,params);
	}

	/**
	 * 删除一条数据。比如：基于主键的删除。
	 * @param errorMsg 出错后的提示信息，抛异常出去。
	 * @param sql
	 * @param params
	 */
	public static void deletesOrThrow(final String errorMsg,
	                                  final String sql, Object... params) {
		deletesOrThrow(1, errorMsg, sql, params);
	}
	/**
	 * 删除一条数据。比如：基于主键的删除。
	 * 出错后的提示信息(默认提示“数据删除失败，删除了0条或多条数据”)，抛异常出去。
	 * @param sql
	 * @param params
	 */
	public static void deletesOrThrowNoMsg(final String sql, Object... params) {
		deletesOrThrow(1, "数据删除失败，删除了0条或多条数据", sql, params);
	}

	/**
	 * 更新多条数据。比如：基于主键的更新。
	 * @param expectedDeleteNums 预期被删除的数量
	 * @param errorMsg 出错后的提示信息，抛异常出去。
	 * @param sql
	 * @param params
	 */
	public static void deletesOrThrow(final int expectedDeleteNums, final String errorMsg,
	                                  final String sql, Object... params) {
		int nums = Dbo.execute(sql, params);
		if(nums==expectedDeleteNums) return;

		String goodSql = formatSQL(sql, params);
		logger.error(String.format(
				"%s Illegal delete for SQL[%s], expected %d but %d", HttpDataHolder.getBizId(), goodSql, expectedDeleteNums, nums));
		if(nums==0) {
			throw new BusinessException(errorMsg==null?"delete nothing!":errorMsg);
		} else {
			throw new BusinessException(errorMsg==null?"Illegal delete!":errorMsg);
		}
	}
	/**
	 * 删除一条数据。比如：基于主键的删除。
	 * 出错后的提示信息(默认提示“数据删除失败”)，抛异常出去。
	 * @param sql
	 * @param params
	 */
	public static void deletesOrThrowNoMsg(final int expectedDeleteNums,
	                                  final String sql, Object... params) {
		deletesOrThrow(expectedDeleteNums, "数据删除失败", sql, params);
	}

	public static String formatSQL(final String sql, Object... params) {
		// 这是临时写的，要改成自己的习惯
		return "SQL=[" + sql + "], Params=" + Arrays.toString(params);
	}
	private DboExecute(){}
}
