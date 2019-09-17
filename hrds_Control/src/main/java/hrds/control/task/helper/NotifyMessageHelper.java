package hrds.control.task.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hrds.control.constans.ControlConfigure;

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

    private NotifyMessageHelper() {

        smsAccountName = ControlConfigure.NotifyConfig.smsAccountName;
        smsAccountPasswd = ControlConfigure.NotifyConfig.smsAccountPasswd;
        cmHostIp = ControlConfigure.NotifyConfig.cmHostIp;
        cmHostPort = ControlConfigure.NotifyConfig.cmHostPort;
        wsHostIp = ControlConfigure.NotifyConfig.wsHostIp;
        wsHostPort = ControlConfigure.NotifyConfig.wsHostPort;
        bizType = ControlConfigure.NotifyConfig.bizType;
        phoneNumber = ControlConfigure.NotifyConfig.phoneNumber;
    }

    /**
     * 获取NotifyMessageHelper实例
     * @author Tiger.Wang
     * @date 2019/9/6
     * @return hrds.control.task.helper.NotifyMessageHelper
     */
    public static NotifyMessageHelper getInstance() {

        return INSTANCE;
    }

    /**
     * 发送警告信息。注意，发送的对象配置信息（手机号等）在配置文件中，该类在第一次加载时会读取配置文件来构造实例。
     * @author Tiger.Wang
     * @date 2019/9/6
     * @param message   信息内容
     */
    public void SendMsg(String message) {
        //TODO 此处缺少实现，因为此处使用的postmsg-ump-2.1.jar在maven中找不到，不知道该jar应该放在什么地方
//        Account ac = new Account(smsAccountName, smsAccountPasswd);// 设置帐号密码
//        PostMsg pm = new PostMsg(); // 新建一个PostMsg对象
//        pm.getCmHost().setHost(cmHostIp, cmHostPort);// 您设置的下行端口 400
//        pm.getWsHost().setHost(wsHostIp, wsHostPort);
//
//        MTPack pack = new MTPack();
//        pack.setBatchID(UUID.randomUUID());
//        pack.setBatchName(smsAccountName + "-" + String.valueOf(System.currentTimeMillis())); //换成时间戳
//        pack.setMsgType(MTPack.MsgType.SMS);
//        pack.setSendType(MTPack.SendType.MASS);
//        pack.setBizType(bizType);
//        pack.setDistinctFlag(false);
//
//        ArrayList<MessageData> msgs = new ArrayList<MessageData>();
//
//        String[] phoneNumberArray = phoneNumber.split(",");
//        String message = currBathDate + " " + jobName + "调度失败!";
//        for(int i = 0; i < phoneNumberArray.length; ++i) {
//            msgs.add(new MessageData(phoneNumberArray[i], message));
//        }
//        pack.setMsgs(msgs);
//        GsmsResponse resp;
//        try {
//            resp = pm.post(ac, pack);
//            System.out.println(resp);
//            System.out.println("您的UUID为：" + resp.getUuid());
//            System.out.println("系统返回值为：" + resp.getResult());// 返回系统返回值 枚举类型
//            System.out.println(msgs);
//        }
//        catch(Exception e) {
//            log.error("Exception:" + e.getMessage());
//        }
    }
}
