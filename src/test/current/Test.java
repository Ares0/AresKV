package test.current;

import com.alibaba.fastjson.JSON;

import kv.Command;
import kv.bean.RemoteRequest;
import kv.utils.DataType;
import kv.utils.KVObject;

public class Test {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
//		System.out.println("12312s".hashCode());
		
		KVObject val = new KVObject();
		val.setV("1");
		val.setT(DataType.STRING_TYPE);
		
		RemoteRequest rq = new RemoteRequest(Command.PUT, "1", val, 1);
		
		Object o = false;
		
		String str = JSON.toJSONString(rq);
		System.out.println(str);
		
		Object rq2 =  JSON.parseObject(str, RemoteRequest.class);
		
		System.out.println(rq2.getClass().getName());
	}

}
