package kv.db.handler;

import kv.Command;
import kv.bean.DbRequest;
import kv.bean.DbResponse;
import kv.utils.DataType;
import kv.utils.KVObject;


/**
 *  FollowerHandler
 * */
public class FollowerHandler extends AbstractHandler {

	@Override
	public void process(DbRequest req) {
		Command type = req.getCommand();
		if (type == Command.PUT || type == Command.REMOVE) {
			doSalveResponse(req);
		} else {
			next.process(req);   // ¼ÌÐø´¦ÀíÁ´
		}
	}

	private void doSalveResponse(DbRequest req) {
		DbResponse rep = new DbResponse();
		rep.setClientId(req.getClientId());
		rep.setKey(req.getKey());
		
		KVObject ko = req.getValue();
		ko.setT(DataType.STRING_TYPE);
		ko.setV("can not be modified from the slave");
		rep.setValue(ko);
		
		db.getResponseQueue().produce(rep);
	}

}
