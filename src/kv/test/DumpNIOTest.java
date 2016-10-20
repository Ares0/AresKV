package kv.test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.CRC32;


/*
 *  需要自己处理字节流
 * NIO多用于网络IO，文件IO多用Stream
 * **/
public class DumpNIOTest {

	public static void main(String[] args) throws IOException {
		testNIO1Read();
	}
	
	public static void testNIO1Read() throws IOException {
		String url = "G://2016-08-29-16-14-56";
		File f = new File(url);
		FileInputStream fs = new FileInputStream(f);
		DataInputStream out = new DataInputStream(fs);
		
		ByteBuffer bf = ByteBuffer.allocate(64);
		FileChannel fc = fs.getChannel();
		fc.read(bf);
		
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
