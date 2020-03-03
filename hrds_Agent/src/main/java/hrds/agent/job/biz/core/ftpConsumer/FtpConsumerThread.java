package hrds.agent.job.biz.core.ftpConsumer;

import com.alibaba.fastjson.JSONObject;
import hrds.agent.job.biz.utils.CommunicationUtil;
import hrds.agent.trans.biz.ftpcollect.FtpCollectJob;
import hrds.commons.entity.Ftp_transfered;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * FtpConsumerThread
 * date: 2020/3/2 10:51
 * author: zxz
 */
public class FtpConsumerThread extends Thread {
	private final static Logger LOGGER = LoggerFactory.getLogger(FtpCollectJob.class);
	public static ConcurrentMap<String, ArrayBlockingQueue<String>> queueMap = new ConcurrentHashMap<>();
	//某一个任务结束标识，当有一个任务结束，需要保证所有的数据batch提交到数据库一次
	public static volatile boolean flag = false;
	//ftp采集配置表的主键
	private String ftpId;
	//插入的sql
	private static final String addSql = "INSERT " +
			"INTO " +
			Ftp_transfered.TableName +
			"    (" +
			"        ftp_transfered_id," +
			"        ftp_id," +
			"        transfered_name," +
			"        file_path," +
			"        ftp_filemd5," +
			"        ftp_date," +
			"        ftp_time" +
			"    ) " +
			"    VALUES " +
			"    ( ?, ?, ?, ?, ?, ?, ?)";

	public FtpConsumerThread(String ftpId) {
		this.ftpId = ftpId;
	}

	@Override
	public void run() {
		LOGGER.info("开始FtpConsumerThread程序...");
		int count = 0;
		List<Object[]> addParamsPool = new ArrayList<>();
		while (true) {
			try {
				//从队列中拿出信息，当队列中无信息时，该线程阻塞
				String queueMeta = queueMap.get(ftpId).take();
				JSONObject queueJb = JSONObject.parseObject(queueMeta);
				Object[] objects = new Object[7];
				objects[0] = UUID.randomUUID().toString();
				objects[1] = Long.parseLong(ftpId);
				objects[2] = queueJb.getString("fileName");
				objects[3] = queueJb.getString("absolutePath");
				objects[4] = queueJb.getString("md5");
				objects[5] = queueJb.getString("ftpDate");
				objects[6] = queueJb.getString("ftpTime");
				addParamsPool.add(objects);
				count++;
				if (count % 5000 == 0 || flag) {
					CommunicationUtil.batchAddFtpTransfer(addParamsPool, addSql, ftpId);
					addParamsPool.clear();
					flag = false;
				}
			} catch (Exception e) {
				LOGGER.error("ftp采集消费线程记录到数据库异常", e);
			}
		}
	}
}
