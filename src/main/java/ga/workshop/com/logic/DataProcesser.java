package ga.workshop.com.logic;

import ga.workshop.com.model.Target;

public interface DataProcesser {

	Target processData(Target target ,String jsonStr) throws Exception;
	
}
