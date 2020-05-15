package hrds.h.biz.service;

import hrds.h.biz.config.MarketConf;
import hrds.h.biz.realloader.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public abstract class AbstractBusiness implements ILoadBussiness{

	protected static final Logger logger = LogManager.getLogger(AbstractBusiness.class);

	protected Loader loader;
	protected MarketConf conf;

	public AbstractBusiness(Loader loader) {

		this.loader = loader;
		conf = loader.getConf();
	}


	@Override
	public void close() throws IOException {

		if( loader != null ) {
			loader.close();
		}

	}
}
