package cn.com.optel.comm;

import cn.com.optel.data.Message;

class SessionResult {
	protected Message message;
	
	SessionResult(Message msg) {
		message = msg;
	}
	
	public Message getMsg() {
		return message;
	}
}
