package hrds.commons.utils.key;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.key.SnowflakeImpl;

import java.util.Random;

/**
 * <p> 标 题: 海云数服 </p>
 * <p> 描 述: 产生各表的主键 </p>
 * <p> 版 权: Copyright (c) 2011 </p>
 * <p> 公 司: 博彦科技 </p>
 * <p> 创建时间: 2011-7-20 下午04:41:33 </p>
 *
 * @author Administrator
 * @version 1.0 UuidGener
 */
public class PrimayKeyGener {
	private static final SnowflakeImpl idgenDef = new SnowflakeImpl(30, 30);
	private static final SnowflakeImpl idgenA = new SnowflakeImpl(0, 0);
	private static final SnowflakeImpl idgenB = new SnowflakeImpl(1, 1);
	private static final SnowflakeImpl idgenC = new SnowflakeImpl(2, 2);
	private static final SnowflakeImpl idgenD = new SnowflakeImpl(3, 3);
	private static final SnowflakeImpl idgenE = new SnowflakeImpl(4, 4);
	private static final SnowflakeImpl idgenF = new SnowflakeImpl(5, 5);
	private static final SnowflakeImpl idgenG = new SnowflakeImpl(6, 6);
	private static final SnowflakeImpl idgenH = new SnowflakeImpl(7, 7);
	private static final SnowflakeImpl idgenI = new SnowflakeImpl(8, 8);

	private PrimayKeyGener() {
	}

	private static SnowflakeImpl getCaller() {
		StackTraceElement stack[] = (new Throwable()).getStackTrace();
		for (int i = 0; i < stack.length; i++) {
			StackTraceElement ste = stack[i];
			String className = ste.getClassName();
			System.out.println(className);
			if (className.contains("hrds.a.")) return idgenA;
			else if (className.contains("hrds.b.")) return idgenB;
			else if (className.contains("hrds.c.")) return idgenC;
			else if (className.contains("hrds.d.")) return idgenD;
			else if (className.contains("hrds.e.")) return idgenE;
			else if (className.contains("hrds.f.")) return idgenF;
			else if (className.contains("hrds.g.")) return idgenG;
			else if (className.contains("hrds.H.")) return idgenH;
		}
		return idgenDef;
	}

	public static String getNextId() {
		SnowflakeImpl idgen = getCaller();
		return Long.toString(idgenB.nextId());
	}

	/**
	 * 生成6位随机数
	 *
	 * @return
	 */
	public static String getRandomStr() {

		Random r = new Random();
		long i = r.nextInt(100000);
		long number = i + 900000L;
		return Long.toString(number);
	}

	public static String getRandomTime() {

		return getRandomStr() + DateUtil.getSysTime();
	}

	/**
	 * 获取操作员ID
	 *
	 * @return
	 */
	public static String getOperId() {

		hrds.commons.utils.key.KeyGenerator keygen = KeyGenerator.getInstance();
		long val = keygen.getNextKey("tellers");
		long number = 5000L + val;
		StringBuffer str = new StringBuffer();
		str.append(number);
		return str.toString();
	}

	/**
	 * 获取工作组id和角色id
	 *
	 * @return
	 */
	public static String getRole() {

		hrds.commons.utils.key.KeyGenerator keygen = hrds.commons.utils.key.KeyGenerator.getInstance();
		long roleid = keygen.getNextKey("roleid"); //从KeyTable生成组编码
		long number = 100L + roleid;
		StringBuffer str = new StringBuffer();
		str.append(number);
		return str.toString();
	}
}
