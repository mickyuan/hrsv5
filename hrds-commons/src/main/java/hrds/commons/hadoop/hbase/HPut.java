package hrds.commons.hadoop.hbase;

import org.apache.hadoop.hbase.client.Put;

public class HPut {

	Put put = null;

	public HPut(byte[] rowkey) {

		put = new Put(rowkey);
	}
	
	public Put getPut() {
	
		return put;
	}
	
	public void setPut(Put put) {
	
		this.put = put;
	}



	public void addColumn(byte[] family, byte[] qualifier, byte[] value) {

		put.addColumn(family, qualifier, value);
	}

	public void addColumn(byte[] family, byte[] qualifier, long ts, byte[] value) {

		 put.addColumn(family, qualifier, ts, value);
	}
	
}
