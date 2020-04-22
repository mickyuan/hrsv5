package hrds.h.biz.service;

import hrds.h.biz.realloader.Loader;
import hrds.h.biz.config.MarketConf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public abstract class AbstractBusiness implements ILoadBussiness{

	protected static final Log logger = LogFactory.getLog(AbstractBusiness.class);

	protected Loader load;
	protected MarketConf conf = null;

	public AbstractBusiness(Loader load) {

		this.load = load;
		conf = load.getConf();
	}


	@Override
	public void close() throws IOException {

		if( load != null ) {
			load.close();
		}

	}
}
