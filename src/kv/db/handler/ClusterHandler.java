package kv.db.handler;

import kv.Command;
import kv.db.ClusterDB;
import kv.db.DbRequest;
import kv.db.DbResponse;
import kv.utils.KVObject;
import kv.utils.Range;
import kv.utils.DataType;


/**
 *  ClusterHandler
 * Cluster���key��node��ַ��KVObject��value��int[2]����
 * �ͻ��ˣ�move��true��value��String���͵ķ�������ַ 127.0.0.1:8768��
 * */
public class ClusterHandler extends AbstractHandler {

	private static Range rc = new Range(0, 0);
	
	@Override
	public void process(DbRequest req) {
		int type = req.getCommand();
		ClusterDB cdb = (ClusterDB)db;
		
		if (type == Command.ADD_CLUSTER_NODE) {
			int[] range = (int[]) req.getValue().getValue();
			if (range[0] < 0 || range[1] < 0 
					|| range[0] > ClusterDB.KEY_RANGE_MAX_VALUE || range[0] > ClusterDB.KEY_RANGE_MAX_VALUE) {
				throw new IllegalArgumentException("��Χ��������");
			}
			
			rc.setStart(range[0]);
			rc.setEnd(range[1]);
			if (getClusterNodeAddress() != null) {
				throw new IllegalArgumentException("���ڷ�Χ����");
			}
			
			cdb.getClusterRange().put(req.getKey(), new Range(range[0], range[1]));
		} else if (type == Command.PUT || type == Command.GET || type == Command.REMOVE) {
			int range = getRange(req.getKey());
			Range r = cdb.getKeyRange();
			if (range < r.getStart() || range > r.getEnd()) {
				DbResponse rep = moveReponse(req, range);
				db.getResponseQueue().produce(rep);
				// �����з���
				return;
			}
		} else if (type == Command.CHANGE_CLUSTER_RANGE) {
			// TODO ������Ҫ���������·�Ƭ
		}
		
		// ����������
		next.process(req);
	}

	private int getRange(String key) {
		return key.hashCode() % ClusterDB.KEY_RANGE_MAX_VALUE;
	}
	
	private DbResponse moveReponse(DbRequest req, int range) {
		DbResponse rep = new DbResponse();
		rep.setClientId(req.getClientId());
		rep.setKey(req.getKey());
		
		KVObject v = new KVObject();
		v.setType(DataType.STRING_TYPE);
		
		rc.setStart(range);
		rc.setEnd(range);
		v.setValue(getClusterNodeAddress());
		
		rep.setValue(v);
		rep.setMove(true);
		return rep;
	}

	private String getClusterNodeAddress() {
		return ((ClusterDB)db).getRangeCluster().get(rc);
	}

}
