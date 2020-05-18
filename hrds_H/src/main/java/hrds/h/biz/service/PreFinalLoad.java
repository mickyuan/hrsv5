package hrds.h.biz.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 前后置作业接口
 *
 * @author mick
 * @title: PreFinalLoad
 * @projectName hrsv5
 * @description: TODO
 * @date 2020/5/15上午11:49
 */
public interface PreFinalLoad {
    Logger logger = LogManager.getLogger(PreFinalLoad.class);

    /**
     * 后置作业
     */
    void finalWork();


}
