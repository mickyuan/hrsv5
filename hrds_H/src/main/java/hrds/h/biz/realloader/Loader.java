package hrds.h.biz.realloader;


import hrds.h.biz.service.FirstLoad;
import hrds.h.biz.service.NonFirstLoad;

import java.io.Closeable;

public interface Loader extends FirstLoad, NonFirstLoad, Closeable {

}
