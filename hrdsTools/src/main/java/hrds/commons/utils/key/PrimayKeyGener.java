package hrds.commons.utils.key;

import fd.ng.core.utils.DateUtil;

import java.util.Random;

/**
 * <p> 标 题: 海云数服 </p>
 * <p> 描 述: 产生各表的主键 </p>
 * <p> 版 权: Copyright (c) 2011 </p>
 * <p> 公 司: xchao </p>
 * <p> 创建时间: 2011-7-20 下午04:41:33 </p>
 *
 * @author Administrator
 * @version 1.0 UuidGener
 */
public class PrimayKeyGener {

    private PrimayKeyGener() {
    }

    public static String getNextId() {

        long val = genNextvalFromKeyPool("hrds");
        long number = 1000000000L + val;
        StringBuffer str = new StringBuffer();
        str.append(number);
        return str.toString();
    }
    /**
     * 订单号
     * @return
     */
    public static String getOrderNO() {

        long val = genNextvalFromKeyPool("hrds");
        long number = 600000000000L + val;
        StringBuffer str = new StringBuffer();
        str.append(number);
        str.append(DateUtil.getSysDate().substring(2));
        return str.toString();
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
     * @return
     */
    public static String getOperId() {

        long val = genNextvalFromKeyPool("tellers");
        long number = 5000L + val;
        StringBuffer str = new StringBuffer();
        str.append(number);
        return str.toString();
    }
    /**
     * 获取工作组id和角色id
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

    /**
     * 获取序列主键值
     * @param seqname
     * @return
     */
    private static long genNextvalFromKeyPool(String seqname) {

        hrds.commons.utils.key.KeyGenerator keygen = KeyGenerator.getInstance();
        long seqno = keygen.getNextKey(seqname);
        return seqno;
    }
}
