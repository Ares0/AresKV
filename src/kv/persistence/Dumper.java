package kv.persistence;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.CRC32;

import kv.db.KVDataBase;
import kv.db.KVMap.Node;

/*
 *  日志，DataOutputStream写入UTF；
 * FileOutput-BufferedOutput-DataOutput逐层增强
 * aresKV - keyLength - key - valueLength - value - eof - crc
 * **/
public class Dumper implements Runnable{
	
	private KVDataBase db;
	
	private DateFormat df;
	
	private FileOutputStream fs;
	
	private BufferedOutputStream bas;
	
	private DataOutputStream out;
	
	private static int DEFAULT_SLEEP_TIME = 5 * 60 * 1000;
	
	private String DEFAULT_LOG_PREFIX_PATH = "G://";
	
	private String LOG_FLAG = "aresKV";
	
	private String LOG_EOF_FLAG = "" + Integer.MAX_VALUE;
	
	private boolean isRunning;
	
	private Thread dump;
	
	public Dumper(KVDataBase db) {
		this.db = db;
		isRunning = true;
		df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	}
	
	public void start() {
		dump = new Thread(this);
		dump.setName("dump-thread");
		dump.start();
		System.out.println("dump start");
	}
	
	@Override
	public void run() {
		while (isRunning) {
			try {
				Thread.sleep(DEFAULT_SLEEP_TIME);
				
				File f = createLog();
				beforeContentWrite(f);
				
				StringBuilder sb = new StringBuilder();
				Node<String, String>[] nodes = db.getNodes();
				
				if (nodes == null || nodes.length <= 0)
					continue;
				
				for (Node<String, String> e : nodes) {
					if (e != null) {
						sb.append(e.getKey() + e.getValue());
						out.writeUTF(e.getKey());
						out.writeUTF(e.getValue());
					}
				}
				
				afterContentWrite(f, sb);
			} catch (InterruptedException e) {
				System.out.println("dump interrupt");
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
		}
		System.out.println("dump stop");
	}
	
	public void stop() {
		isRunning = false;
		dump.interrupt();
		
		try {
			if (out != null) {
				out.close();
				bas.close();
				fs.close(); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File createLog() throws IOException {
		Date d = new Date();
		String postfix = df.format(d);
		
		File f = new File(DEFAULT_LOG_PREFIX_PATH + postfix);
		f.createNewFile();
		return f;
	}
	
	private void beforeContentWrite(File f) throws IOException {
		fs = new FileOutputStream(f, true);
		bas = new BufferedOutputStream(fs);
		out = new DataOutputStream(bas);
		
		out.writeUTF(LOG_FLAG);
	}
	
	private void afterContentWrite(File f, StringBuilder sb) throws IOException {
		out.writeUTF(LOG_EOF_FLAG);
		
		CRC32 crc = new CRC32();
		crc.update(sb.toString().getBytes());
		out.writeLong(crc.getValue());
		
		out.flush();
	}
	
}
