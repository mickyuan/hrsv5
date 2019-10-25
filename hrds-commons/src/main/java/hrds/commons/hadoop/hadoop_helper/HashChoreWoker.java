package hrds.commons.hadoop.hadoop_helper;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;

import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

public class HashChoreWoker {

	//随机取机数目  
	private int baseRecord;
	//rowkey生成器  
	private RowKeyGenerator rkGen;
	
	//rowkey生成器  
	private String[] split;
	//取样时，由取样数目及region数相除所得的数量.  
	private int splitKeysBase;
	//splitkeys个数  
	private int splitKeysNumber;
	//由抽样计算出来的splitkeys结果  
	private byte[][] splitKeys;

	public HashChoreWoker(int baseRecord, int prepareRegions) {

		this.baseRecord = baseRecord;
		//实例化rowkey生成器  
		rkGen = new HashRowKeyGenerator();
		splitKeysNumber = prepareRegions - 1;
		splitKeysBase = baseRecord / prepareRegions;
	}
	public HashChoreWoker(String coustomSplitKeys) {

		split = coustomSplitKeys.split(",");
		this.baseRecord = split.length;
		//实例化rowkey生成器  
		rkGen = new HashRowKeyGenerator();
	}

	public byte[][] coustomSplitKeys() {
		
		splitKeys = new byte[baseRecord][];
		//使用treeset保存抽样数据，已排序过  
		TreeSet<byte[]> rows = new TreeSet<byte[]>(Bytes.BYTES_COMPARATOR);
		for(int i = 0; i < baseRecord; i++) {
			rows.add(Bytes.toBytes(split[i]));
		}
		Iterator<byte[]> rowKeyIter = rows.iterator();
		int index = 0;
		while( rowKeyIter.hasNext() ) {
			
			byte[] tempRow = rowKeyIter.next();  
            rowKeyIter.remove();  
            splitKeys[index] = tempRow;  
            index++;  
		}
		rows.clear();
		rows = null;
		return splitKeys;
	}
	public byte[][] calcSplitKeys() {

		splitKeys = new byte[splitKeysNumber][];
		//使用treeset保存抽样数据，已排序过  
		TreeSet<byte[]> rows = new TreeSet<byte[]>(Bytes.BYTES_COMPARATOR);
		for(int i = 0; i < baseRecord; i++) {
			rows.add(rkGen.nextId());
		}
		int pointer = 0;
		Iterator<byte[]> rowKeyIter = rows.iterator();
		int index = 0;
		while( rowKeyIter.hasNext() ) {
			byte[] tempRow = rowKeyIter.next();
			rowKeyIter.remove();
			if( (pointer != 0) && (pointer % splitKeysBase == 0) ) {
				if( index < splitKeysNumber ) {
					splitKeys[index] = tempRow;
					index++;
				}
			}
			pointer++;
		}
		rows.clear();
		rows = null;
		return splitKeys;
	}

	public interface RowKeyGenerator {

		byte[] nextId();
	}

	public class HashRowKeyGenerator implements RowKeyGenerator {

		private long currentId = 1;
		private long currentTime = System.currentTimeMillis();
		private Random random = new Random();

		public byte[] nextId() {

			try {
				currentTime += random.nextInt(1000);
				byte[] lowT = Bytes.copy(Bytes.toBytes(currentTime), 4, 4);
				byte[] lowU = Bytes.copy(Bytes.toBytes(currentId), 4, 4);
				byte[] currId = Bytes.toBytes(String.valueOf(currentId));
				return Bytes.add(MD5Hash.getMD5AsHex(Bytes.add(lowU, lowT)).substring(0, 8).getBytes(),currId);
			}
			finally {
				currentId++;
			}
		}
	}
	public static void main(String[] args) {
		String key = "dsd,rere,afa,sgh,try,lkj,hjk";
		HashChoreWoker worker = new HashChoreWoker(1000000, 10);
		HashChoreWoker worker2 = new HashChoreWoker(key);
		byte[][] splitKeys = worker.calcSplitKeys();
		byte[][] splitKey2 = worker2.coustomSplitKeys();
		for(int i = 0; i < splitKeys.length; i++) {
			byte dd[] = splitKeys[i];
			System.out.println(new String(dd));
		}
		for(int i = 0; i < splitKey2.length; i++) {
			byte dd[] = splitKey2[i];
			System.out.println("+++++++++"+new String(dd));
		}
	}
}
