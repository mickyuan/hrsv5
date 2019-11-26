package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.exception.BusinessException;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.Closeable;
import java.io.File;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@DocClass(desc = "创建mapDB数据库的操作类", author = "zxz", createdate = "2019/10/12 15:44")
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
		db = DBMaker.newFileDB(new File(path + File.separator + fileName)).mmapFileEnableIfSupported()
				.closeOnJvmShutdown().cacheSize(500).make();
	}

	@Method(desc = "在mapDB下构建一个表，用于存值",
			logicStep = "1.在mapDB下构建一个表，用于存值" +
					"2.存在则取已经存在的表" +
					"3.不存在则在mapDB下构建一个表，用于存值")
	@Param(name = "tableName", desc = "mapDB下指定的表名", range = "不可为空")
	@Param(name = "afterWriter", range = "不可为空",
			desc = "是指定项在一定时间内没有读写，会从缓存移除该key，下次取的时候从文件中取")
	@Return(desc = "自定义的表对象HTreeMap", range = "不会为空")
	public ConcurrentMap<String, String> htMap(String tableName, int afterWriter) {
		ConcurrentMap<String, String> map;
		//1.判断mapDb下是否存在该表
		if (db.exists(tableName)) {
			//2.存在则取已经存在的表
			map = db.getHashMap(tableName);
		} else {
			//3.不存在则在mapDB下构建一个表，用于存值
			map = db.createHashMap(tableName).keySerializer(Serializer.STRING)
					.valueSerializer(Serializer.STRING).expireAfterWrite(afterWriter, TimeUnit.MINUTES).make();
		}
		return map;
	}

	@Method(desc = "实现Closeable重写的方法，try中构造这个对象，结束方法后会自动调用这个方法",
			logicStep = "1.实现Closeable重写的方法，try中构造这个对象，结束方法后会自动调用这个方法")
	@Override
	public void close() {
		if (db != null && !db.isClosed()) {
			db.close();
		}
	}

	@Method(desc = "提交到mapDB",
			logicStep = "1.提交到mapDB")
	public void commit() {
		if (db != null && !db.isClosed()) {
			db.commit();
		}
	}

	public static void main(String[] args) {
		try (MapDBHelper mapDBHelper = new MapDBHelper("D:\\hrsv5\\trunk\\10000001\\33332222", "33332222.db")) {
			ConcurrentMap<String, String> zzz = mapDBHelper.htMap("33332222", 1);
			for(String key : zzz.keySet()){
				System.out.println(zzz.get(key));
			}
			mapDBHelper.commit();
		}
	}
}
