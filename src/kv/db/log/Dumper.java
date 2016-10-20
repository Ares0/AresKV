package kv.db.log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.CRC32;

import kv.KVDataBase;
import kv.db.AbstractDB.Iterator;
import kv.utils.KVObject;
import kv.utils.NodeFacade;
import kv.utils.Type;

/*
 *  日志，DataOutputStream写入UTF；
 * FileOutput-BufferedOutput-DataOutput逐层增强
 * aresKV-length-int_type-key-int_type-value-curr-expire-watch-dirty-eof-crc
 * 读取int长度，到MAX_VALUE即为结束符。
 * **/
public class Dumper implements Runnable {

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

				NodeFacade<String, KVObject> e;
				StringBuilder sb = new StringBuilder();
				Iterator it = db.getIterator();

				while ((e = it.next()) != null) {
					String key = e.getKey();
					KVObject val = e.getValue();
					Object value = val.getValue();
					
					if (key != null && val != null && value != null) {
						sb.append(key + value.toString());

						writeType(key);
						writeType(value);  // kvObject

						out.writeLong(e.getCurrent());
						out.writeLong(e.getExpire());
						out.writeBoolean(e.isIswatch());
						out.writeLong(e.getCid());
						out.writeBoolean(e.isDirty());
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

	private void writeType(Object obj) throws IOException {
		String str = obj.toString();
		if (obj instanceof String) {
			out.writeInt(Type.STRING_TYPE);
			out.writeUTF(str);
		} else if (obj instanceof Integer) {
			out.writeInt(Type.INT_TYPE);
			out.writeInt(Integer.parseInt(str));
		} else if (obj instanceof Byte) {
			out.writeInt(Type.BYTE_TYPE);
			out.writeByte(Byte.parseByte(str));
		} else if (obj instanceof Float) {
			out.writeInt(Type.FLOAT_TYPE);
			out.writeFloat(Float.parseFloat(str));
		} else if (obj instanceof Double) {
			out.writeInt(Type.DOUBLE_TYPE);
			out.writeDouble(Double.parseDouble(str));
		} else if (obj instanceof Character) {
			out.writeInt(Type.CHAR_TYPE);
			out.writeUTF(str);
		} else if (obj instanceof Boolean) {
			out.writeInt(Type.BOOLEAN_TYPE);
			out.writeBoolean(Boolean.parseBoolean(str));
		} else if (obj instanceof Short) {
			out.writeInt(Type.SHORT_TYPE);
			out.writeShort(Short.parseShort(str));
		} else if (obj instanceof Long) {
			out.writeInt(Type.LONG_TYPE);
			out.writeLong(Long.parseLong(str));
		} else {
			out.writeInt(Type.STRING_TYPE);
			out.writeUTF(str);
		}
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
