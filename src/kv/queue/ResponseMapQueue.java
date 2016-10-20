package kv.queue;

import java.util.HashMap;

import kv.bean.DbResponse;
import kv.utils.Utils;

/**
 *  queue by map
 * key cid*key£¬keyÊÇÎ¨Ò»µÄ¡£
 * */
public class ResponseMapQueue extends AbstractResponseQueue {
	
	private HashMap<String, DbResponse> reps;
	
	public ResponseMapQueue() {
		reps = new HashMap<>();
	}

	@Override
	public void produce(DbResponse rep) {
		reps.put(Utils.getCidKey(rep.getClientId(), rep.getKey()), rep);
	}

	@Override
	public DbResponse consume(String key) {
		DbResponse rep = reps.get(key);
		if (rep != null) {
			reps.remove(key);
		}
		return rep;
	}
	
}
