package hrds.k.biz.dm.metadatamanage.transctrl;

import com.alibaba.fastjson.JSONObject;
import fd.ng.db.resultset.Result;

/**
 * 删除hadoop表的互斥锁异常
 */
public class MutexException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private Result rs;
    private JSONObject ctrlTable;

    /**
     * @return the rs
     */
    public Result getMutexList() {

        return rs;
    }

    public JSONObject getCtrlTable() {

        return ctrlTable;
    }

    public MutexException(String msg, Result rsMutex) {

        super(msg);
        rs = rsMutex;
        this.ctrlTable = null;
    }

    public MutexException(String msg, JSONObject ctrlTable) {

        super(msg);
        rs = null;
        this.ctrlTable = ctrlTable;
    }

    public MutexException(String msg, Result rsMutex, JSONObject ctrlTable) {

        super(msg);
        rs = rsMutex;
        this.ctrlTable = ctrlTable;
    }

    public MutexException(String msg, Result rsMutex, Throwable ex) {

        super(msg, ex);
        this.rs = rsMutex;
        this.ctrlTable = null;
    }

}
