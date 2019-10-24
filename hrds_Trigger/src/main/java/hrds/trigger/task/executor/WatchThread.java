package hrds.trigger.task.executor;

import java.io.*;

class WatchThread extends Thread {

	private InputStream inputStream;
	private String logDirc;

	public WatchThread(InputStream inputStream, String logDirc) {

		this.inputStream = inputStream;
		this.logDirc = logDirc;
	}

	public void run() {

		OutputStreamWriter outputStreamWriter = null;
		PrintWriter pw = null;
		BufferedReader br = null;
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			br = new BufferedReader(inputStreamReader);
			String line = null;

			File f = new File(logDirc);
			if( !f.exists() ) {
				f.createNewFile();
			}
			@SuppressWarnings("resource")
			OutputStream outputStream = new FileOutputStream(f, true);
			outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
			pw = new PrintWriter(outputStreamWriter, true);
			while( br != null && (line = br.readLine()) != null ) {
				pw.write(line + "\r\n");
				pw.flush();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if( br != null ) {
				try {
					br.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
			if( pw != null ) {
				pw.close();
			}
			if( outputStreamWriter != null ) {
				try {
					outputStreamWriter.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
