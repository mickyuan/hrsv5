package hrds.f.biz.timer;

import fd.ng.core.utils.DateUtil;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

public class TimerManager {

  private Timer timer = null;
  // 时间间隔
  private static final long PERIOD_DAY = 5 * 1000;

  public TimerManager() {

    timer = new Timer();
  }

  /** 通讯定时 */
  public void AutoMonCommunication() {

    Date date = getDate();
    CommunicationManager com = new CommunicationManager();
    timer.schedule(com, date, PERIOD_DAY);
  }
  private Date getDate() {

    String TimePoint;
    Date date;

    TimePoint = DateUtil.getSysDate() + " " + DateUtil.parseStr2TimeWith6Char(DateUtil.getSysTime());
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd hh:mm:ss");

    try {
      date = format.parse(TimePoint);
    } catch (Exception e) {
      if (e instanceof BusinessException) throw (BusinessException) e;
      else throw new AppSystemException(e);
    }
    return date;
  }
}
