/**
 * 
 */
package ga.workshop.com.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author User
 *
 */
@SuppressWarnings({"unused"})
@Slf4j
@Getter
@Setter
public class ActionBean{
	
	public static enum ACTION_TYPE {
		CREATE, UPDATE, DELETE;
	}
	
	public static enum ACTION_STATUS {
		INIT, EXECUTING, ENDING, ERROR;
	}
	
	public static enum ACTION_PROP {
		target, alert;
	}

	private ACTION_TYPE actionType;
	private Long creatTime;
	private ACTION_STATUS status;

	protected ActionBean() {
		creatTime = System.currentTimeMillis();
		status = ACTION_STATUS.INIT;
	}
	
	public ActionBean(ACTION_TYPE type) {
		this();
		this.actionType = type;
	}
	
	private Map<String,Object> properties = new HashMap<>();
	
	public ActionBean addProp(String key, Object value) {
		properties.put(key, value);
		return this;
	}
	
	public Object getProp(String key) {
		return properties.get(key);
	}
	
}
