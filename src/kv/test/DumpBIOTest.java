package kv.test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;

import kv.db.KVConnection;
import kv.db.KVDataBase;


// Buffer性能更高
public class DumpBIOTest {

	public static void main(String[] args) throws IOException {
//		testWrite();
		
		testRead();
		
		testBIORead();
	}
	
	public static void testWrite() throws IOException {
		KVDataBase db = KVDataBase.getDatabase();
		KVConnection con = db.getConnection();
		
		for (int i = 0; i < 10000; i++) {
			con.put("" + i, "" + i);
		}
		System.out.println("输入任意键 结束");
		System.in.read();
		con.close();
	}
	
	// 78ms
	public static void testRead() throws IOException {
		String url = "G://2016-08-29-16-14-56";
		File f = new File(url);
		FileInputStream fs = new FileInputStream(f);
		BufferedInputStream bas = new BufferedInputStream(fs);
		DataInputStream out = new DataInputStream(bas);
		
		System.out.println(out.readUTF());
		
		StringBuilder sb = new StringBuilder();
		
		String ss;
		boolean flag = true;
		
		long time = System.currentTimeMillis();
		
		while (!(ss = out.readUTF()).equals(Integer.MAX_VALUE + "")) {
			if (flag) {
				System.out.print("key: " + ss + " ");
				flag = false;
			} else {
				System.out.println("value: " + ss);
				flag = true;
			}
			sb.append(ss);
		}
		
		CRC32 crc = new CRC32();
		crc.update(sb.toString().getBytes());
		
		System.out.println("EOF: " + ss);
		System.out.println("CRC32" + out.readLong());
		
		System.out.println("CRC32" + crc.getValue());
		
		out.close();
		bas.close();
		fs.close();
		
		System.out.println(System.currentTimeMillis() - time);
	}
	
	// 168ms
	public static void testBIORead() throws IOException {
		String url = "G://2016-08-29-16-14-56";
		File f = new File(url);
		FileInputStream fs = new FileInputStream(f);
		DataInputStream out = new DataInputStream(fs);
		
		System.out.println(out.readUTF());
		
		StringBuilder sb = new StringBuilder();
		
		String ss;
		boolean flag = true;
		
		long time = System.currentTimeMillis();
		
		while (!(ss = out.readUTF()).equals(Integer.MAX_VALUE + "")) {
			if (flag) {
				System.out.print("key: " + ss + " ");
				flag = false;
			} else {
				System.out.println("value: " + ss);
				flag = true;
			}
			sb.append(ss);
		}
		
		CRC32 crc = new CRC32();
		crc.update(sb.toString().getBytes());
		
		System.out.println("EOF: " + ss);
		System.out.println("CRC32" + out.readLong());
		
		System.out.println("CRC32" + crc.getValue());
		
		out.close();
		fs.close();
		
		System.out.println(System.currentTimeMillis() - time);
	}
	
}
