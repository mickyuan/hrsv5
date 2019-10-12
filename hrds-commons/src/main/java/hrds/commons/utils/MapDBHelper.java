package hrds.commons.utils;

import hrds.commons.exception.BusinessException;
import org.beyoundsoft.mapdb.DB;
import org.beyoundsoft.mapdb.DBMaker;
import org.beyoundsoft.mapdb.HTreeMap;
import org.beyoundsoft.mapdb.Serializer;

import java.io.Closeable;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * 创建mapDB数据库的操作类
 * date: 2019/10/12 15:44
 * author: zxz
 */
public class MapDBHelper implements Closeable {
	//mapDB的数据库操作对象
	private DB db;

	/**
	 * MapDBHelper操作类构造方法
	 * <p>
	 * 1.判断path文件夹不存在，创建文件夹
	 * 2.指定文件创建mapDB对象
	 *
	 * @param path     String
	 *                 含义：mapDB文件库指定的路径
	 *                 取值范围：不能为空
	 * @param fileName String
	 *                 含义：mapDB文件库的指定文件名
	 *                 取值范围：不能为空
	 */
	public MapDBHelper(String path, String fileName) {
		//1.判断path文件夹不存在，创建文件夹
		File dir = new File(path);
		if (!dir.exists()) {
			final boolean mkdirs = dir.mkdirs();
			if (!mkdirs) {
				throw new BusinessException("创建文件夹失败");
			}
		}
		//2.指定文件创建mapDB对象
		db = DBMaker.newFileDB(new File(path + File.separator + fileName))
				.mmapFileEnableIfSupported()
				.cacheSize(500)
				.closeOnJvmShutdown()
				.make();
	}

	/**
	 * 在mapDB下构建一个表，用于存值
	 *
	 * @param tableName   String
	 *                    含义：mapDB下指定的表名
	 *                    取值范围：不可为空
	 * @param afterWriter int
	 *                    含义：是指定项在一定时间内没有读写，会从缓存移除该key，下次取的时候从文件中取
	 *                    取值范围：不可为空
	 * @return HTreeMap<String, String>
	 * 含义：自定义的表对象HTreeMap
	 * 取值范围：不会为空
	 */
	public HTreeMap<String, String> htMap(String tableName, int afterWriter) {
		return db.createHashMap(tableName).keySerializer(Serializer.STRING).valueSerializer(Serializer.STRING)
				.expireAfterWrite(afterWriter, TimeUnit.MINUTES).makeOrGet();
	}

	/**
	 * 实现Closeable重写的方法，try中构造这个对象，结束方法后会自动调用这个方法
	 */
	@Override
	public void close() {
		if (db != null && !db.isClosed()) {
			db.close();
		}
	}

	/**
	 * 提交到mapDB
	 */
	public void commit() {
		if (db != null && !db.isClosed()) {
			db.commit();
		}
	}

}
