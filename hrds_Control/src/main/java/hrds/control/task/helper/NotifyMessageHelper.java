package hrds.control.task.helper;

import com.esms.MessageData;
import com.esms.PostMsg;
import com.esms.common.entity.Account;
import com.esms.common.entity.GsmsResponse;
import com.esms.common.entity.MTPack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hrds.control.constans.ControlConfigure;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ClassName: NotifyMessageHelper
 * Description: 用于发送通知：短信通知、邮件通知。
 * Author: Tiger.Wang
 * Date: 2019/9/6 10:04
 * Since: JDK 1.8
 **/
public class NotifyMessageHelper {

    private static final Logger logger = LogManager.getLogger();

    private final String smsAccountName;
    private final String smsAccountPasswd;
    private final String cmHostIp;
    private final int cmHostPort;
    private final String wsHostIp;
    private final int wsHostPort;
    private final String bizType;
    private final String phoneNumber;

    private static final NotifyMessageHelper INSTANCE = new NotifyMessageHelper();

    /**
     * NotifyMessageHelper类构造器。<br>
     * 1.初始化类变量。
     *
     * @author Tiger.Wang
     * @date 2019/10/9
     */
    private NotifyMessageHelper() {

        //1.初始化类变量。
        this.smsAccountName = ControlConfigure.NotifyConfig.smsAccountName;
        this.smsAccountPasswd = ControlConfigure.NotifyConfig.smsAccountPasswd;
        this.cmHostIp = ControlConfigure.NotifyConfig.cmHostIp;
        this.cmHostPort = ControlConfigure.NotifyConfig.cmHostPort;
        this.wsHostIp = ControlConfigure.NotifyConfig.wsHostIp;
        this.wsHostPort = ControlConfigure.NotifyConfig.wsHostPort;
        this.bizType = ControlConfigure.NotifyConfig.bizType;
        this.phoneNumber = ControlConfigure.NotifyConfig.phoneNumber;
    }

    /**
     * 获取NotifyMessageHelper实例。<br>
     * 1.返回NotifyMessageHelper实例。
     *
     * @return hrds.control.task.helper.NotifyMessageHelper
     * 含义：NotifyMessageHelper实例。
     * 取值范围：不会为null。
     * @author Tiger.Wang
     * @date 2019/10/9
     */
    public static NotifyMessageHelper getInstance() {

        //1.返回NotifyMessageHelper实例。
        return INSTANCE;
    }

    /**
     * 发送警告信息。注意，发送的对象配置信息（手机号等）在配置文件中，
     * 该类在第一次加载时会读取配置文件来构造实例。<br>
     * 1.发送警告信息。
     *
     * @param message 含义：信息内容。
     *                取值范围：任意字符串，不能为null。
     * @author Tiger.Wang
     * @date 2019/10/9
     */
    public void sendMsg(String message) {

        //TODO 此处缺少实现，因为此处使用的postmsg-ump-2.1.jar在maven中找不到，不知道该jar应该放在什么地方
        // 这个jar包是不是自己写的？
        //1.发送警告信息。
        try {
            Account ac = new Account(smsAccountName, smsAccountPasswd);// 设置帐号密码
            PostMsg pm = new PostMsg(); // 新建一个PostMsg对象
            pm.getCmHost().setHost(cmHostIp, cmHostPort);// 您设置的下行端口 400
            pm.getWsHost().setHost(wsHostIp, wsHostPort);

            MTPack pack = new MTPack();
            pack.setBatchID(UUID.randomUUID());
            pack.setBatchName(smsAccountName + "-" + System.currentTimeMillis()); //换成时间戳
            pack.setMsgType(MTPack.MsgType.SMS);
            pack.setSendType(MTPack.SendType.MASS);
            pack.setBizType(Integer.parseInt(bizType));
            pack.setDistinctFlag(false);

            List<MessageData> msgs = new ArrayList<>();

            String[] phoneNumberArray = phoneNumber.split(",");
            for (String s : phoneNumberArray) {
                msgs.add(new MessageData(s, message));
            }
            pack.setMsgs(msgs);
            GsmsResponse resp;
            resp = pm.post(ac, pack);
            logger.info(resp);
            logger.info("您的UUID为：" + resp.getUuid());
            logger.info("系统返回值为：" + resp.getResult());// 返回系统返回值 枚举类型
            logger.info(msgs);
        } catch (Exception e) {
            logger.error("Exception:" + e.getMessage());
        }
    }
}
