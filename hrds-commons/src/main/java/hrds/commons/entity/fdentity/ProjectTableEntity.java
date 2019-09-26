package hrds.commons.entity.fdentity;

import fd.ng.db.entity.TableEntity;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.web.helper.HttpDataHolder;
import hrds.commons.exception.BusinessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProjectTableEntity extends TableEntity {
	private static final Logger logger = LogManager.getLogger();

	@Override
	public int add(final DatabaseWrapper db) {
		int nums = super.add(db);
		if(nums==1) return 1;

		logger.error(String.format(
				"%s Illegal insert for Entity[%s], data=%s",
				HttpDataHolder.getBizId(), this.getClass().getSimpleName(), this));
		if(nums==0)
			throw new EntityDealZeroException();
		else
			throw new EntityDealManyException();
	}

	@Override
	public int update(final DatabaseWrapper db) {
		int nums = super.update(db);
		if(nums==1) return 1;

		logger.error(String.format(
				"%s Illegal update for Entity[%s], data=%s",
				HttpDataHolder.getBizId(), this.getClass().getSimpleName(), this));
		if(nums==0)
			throw new EntityDealZeroException();
		else
			throw new EntityDealManyException();
	}

	@Override
	public int delete(final DatabaseWrapper db) {
		int nums = super.delete(db);
		if(nums==1) return 1;

		logger.error(String.format(
				"%s Illegal delete for Entity[%s], data=%s",
				HttpDataHolder.getBizId(), this.getClass().getSimpleName(), this));
		if(nums==0)
			throw new EntityDealZeroException();
		else
			throw new EntityDealManyException();
	}

	public static class EntityDealZeroException extends BusinessException {
		public EntityDealZeroException() {
			super("没有数据被处理！");
		}
	}
	public static class EntityDealManyException extends BusinessException {
		public EntityDealManyException() {
			super("处理了多条数据！");
		}
	}
}
