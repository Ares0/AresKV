package kv.utils;

import com.alibaba.fastjson.JSON;

import kv.Command;

public class Utils {

	private static final String delimit = "$";
	
	private static final String remoteDelimit = "\n";
	
	public static String getCidKey(long cid, String key) {
		StringBuilder sb = new StringBuilder();
		sb.append(cid);
		sb.append(delimit);
		sb.append(key);
		return sb.toString();
	}
	
	public static Object getJsonObject(String str, Class<?> clazz) {
		return JSON.parseObject(str, clazz);
	}
	
	public static String getObjectJson(Object obj) {
		return JSON.toJSONString(obj).concat(remoteDelimit);
	}
	
	public static void volidateCommond(Command command) {
		if (command != Command.PUT && command != Command.GET 
				&& command != Command.REMOVE && command != Command.RESET && command != Command.CLOSE
				&& command != Command.EXPIRE && command != Command.DIRTY
				&& command != Command.ADD_CLUSTER_NODE && command != Command.CHANGE_CLUSTER_RANGE) {
			throw new IllegalArgumentException();
		}
	}
	
}
