package ga.workshop.com.crud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Service
public class FlowOfCRUD {

    @Autowired
    public TargetCRUD targetCRUD;
    
    @Autowired
    public TrackedTargetCRUD trackedTargetCRUD;
    
    @Autowired
    public TargetAlertCRUD targetAlertCRUD;
    
    public Object process(String type , String processType , String... args ) {
    	Object returnObject = null;
    	try {
			switch (type) {
			case "target":
				returnObject = processTargets(processType, args);
				return returnObject;
	
			case "tracked":
				returnObject = processTrackedTargets(processType, args);
				return returnObject;
				
			case "alert":
				returnObject = processAlerts(processType, args);
				return returnObject;
				
			default:
				log.debug("type undefined => {}",type);;
				return "type undefined";
			}
		} catch (Exception e) {
			log.error("process with type({}) fail , exception => {}",type,e.toString());
		}
    	return returnObject;
	}
    
    private Object processTargets(String processType , String... args){
    	switch (processType) {
		case "output":
			return targetCRUD.outputDatas();
			
		case "create":
			return targetCRUD.createTarget(args[0]);
			
		case "get":
			return targetCRUD.getTarget(args[0]);
			
		case "list":
			return targetCRUD.listAll(Integer.parseInt(args[0]));
			
		case "maxPages":
			return targetCRUD.maxPages();
			
		case "update":
			return targetCRUD.updateTarget(args[0]);
			
		case "delete":
			return targetCRUD.deleteTarget(args[0]);
			
		default:
			log.debug("processType undefined => {}",processType);;
			return "processType undefined";
		}
    }
    
    private Object processTrackedTargets(String processType , String... args){
    	switch (processType) {
		case "add":
			return trackedTargetCRUD.addTarget(args[0]);

		case "remove":
			return trackedTargetCRUD.removeTarget(args[0]);
			
		case "update":
			String[] restArgs = new String[args.length - 1];
			for(int i=0;i<restArgs.length;i++){
				restArgs[i] = args[i+1];
			}
			return trackedTargetCRUD.updateTarget(args[0], restArgs);
			
		case "list":
			return trackedTargetCRUD.listAll(Integer.parseInt(args[0]));
			
		case "output":
			return trackedTargetCRUD.outputDatas();
			
		case "maxPages":
			return trackedTargetCRUD.maxPages();
			
		case "get":
			return trackedTargetCRUD.getTarget(args[0]);
			
		default:
			log.debug("processType undefined => {}",processType);;
			return "processType undefined";
		}
    }
    
    private Object processAlerts(String processType , String... args){
    	String[] restArgs;
    	switch (processType) {
		case "add":
			restArgs = new String[args.length - 1];
			for(int i=0;i<restArgs.length;i++){
				restArgs[i] = args[i+1];
			}
			return targetAlertCRUD.addTarget(args[0], restArgs);
			
		case "list":
			return targetAlertCRUD.listAll();
			
		case "listTriggered":
			return targetAlertCRUD.listAllTriggered();

		case "remove":
			return targetAlertCRUD.removeTarget(args[0]);
			
		case "update":
			restArgs = new String[args.length - 1];
			for(int i=0;i<restArgs.length;i++){
				restArgs[i] = args[i+1];
			}
			return targetAlertCRUD.updateTarget(args[0], restArgs);
			
		case "output":
			return targetAlertCRUD.outputDatas();
			
		case "get":
			return targetAlertCRUD.getTarget(args[0]);
			
		default:
			log.debug("processType undefined => {}",processType);;
			return "processType undefined";
		}
    }
	
	public static void main(String[] args) throws Exception {
		FlowOfCRUD crud = new FlowOfCRUD();
		
//		Gson gson = new Gson();
//		Boolean b = true;
//		String a = b.toString(),a1 = b.toString()+"";
//		System.out.println(gson.toJson(a));
//		System.out.println(gson.toJson(b));
		
	}
}
