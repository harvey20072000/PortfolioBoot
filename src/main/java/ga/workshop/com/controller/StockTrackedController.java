package ga.workshop.com.controller;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ga.workshop.com.crud.FlowOfCRUD;
import ga.workshop.com.util.PortfolioContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/track")
public class StockTrackedController {
	
	private Gson gson = new Gson();
	
	@Autowired
	private FlowOfCRUD crud/* = new TargetCRUD()*/;
	
    @ResponseBody
	@RequestMapping(value = "add", method={RequestMethod.POST, RequestMethod.GET})
	public String addTrackedTarget(@RequestParam String userName,@RequestParam String stockId) {
    	log.debug("addTrackedTarget[stockId:{}]",stockId);
		Map result = new HashMap();
		result.put("stockId", stockId);
		try {
			setUser(userName);
			result.put("status", "0000");
			result.put("isOK", crud.process("tracked", "add", stockId));
		} catch (Exception e) {
			log.error("createTarget fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gson.toJson(result);
    }
    
    @ResponseBody
	@RequestMapping(value = "get", method={RequestMethod.POST, RequestMethod.GET})
    public String getTrackedTarget(@RequestParam String stockId){	
    	log.debug("getTrackedTarget[stockId:{}]",stockId);
		Map result = new HashMap();
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		gsonBuilder.serializeSpecialFloatingPointValues();
		result.put("stockId", stockId);
		try {
			result.put("status", "0000");
			result.put("target", crud.process("tracked", "get", stockId));
		} catch (Exception e) {
			log.error("getTrackedTarget fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gsonBuilder.create().toJson(result);
    }
    
    @ResponseBody
	@RequestMapping(value = "maxPages", method={RequestMethod.POST, RequestMethod.GET})
    public String maxPages(){
//    	log.debug("getTarget[proxyName:{}]",proxyName);
		Map result = new HashMap();
		try {
			result.put("status", "0000");
			result.put("targets", crud.process("tracked", "maxPages", null));
		} catch (Exception e) {
			log.error("listSize fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gson.toJson(result);
    }
    
    @ResponseBody
	@RequestMapping(value = "list", method={RequestMethod.POST, RequestMethod.GET})
    public String listTargets(@RequestParam String userName,@RequestParam(defaultValue="1") int page){
    	log.debug("listTargets[page:{}]",page);
		Map result = new HashMap();
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		gsonBuilder.serializeSpecialFloatingPointValues();
		try {
//			List<TrackedTarget> wrappers = crud.process("track", "list", page+"");
//			for(Target target : crud.listAll(page)){
//				wrappers.add(new TargetWrapper(target));
//			}
			setUser(userName);
			result.put("status", "0000");
			result.put("targets", crud.process("tracked", "list", page+""));
		} catch (Exception e) {
			log.error("listTargets fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gsonBuilder.create().toJson(result).replaceAll("NaN", "0.0");
    }
    
    @ResponseBody
	@RequestMapping(value = "update", method={RequestMethod.POST, RequestMethod.GET})
    public String updateTarget(HttpServletRequest request) { //@RequestParam String userName,@RequestParam String stockId   
    	// update要把參數變成field:param的形式輸入到args裡面
    	Map result = new HashMap(),params = new HashMap<>(request.getParameterMap());
		try {
			String userName = ((String[])params.get("userName"))[0].toString(),stockId = ((String[])params.get("stockId"))[0].toString();
			String[] restArgs = new String[params.size()-1];
			restArgs[0] = stockId;
			int i = 1;
			for(Object key : params.keySet()){
				if("userName".equals(key.toString()) || "stockId".equals(key.toString())){
					continue;
				}else {
					restArgs[i] = key.toString() + ":" + ((String[])params.get(key))[0].toString();
					i++;
				}
			}
			
			log.debug("updateTarget[stockId:{}]",stockId);
			result.put("stockId", stockId);
			setUser(userName);
			result.put("status", "0000");
			result.put("isOK", crud.process("tracked", "update", restArgs));
		} catch (Exception e) {
			log.error("updateTarget fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gson.toJson(result);
	}
    
    @ResponseBody
	@RequestMapping(value = "remove", method={RequestMethod.POST, RequestMethod.GET})
    public String removeTarget(@RequestParam String userName,@RequestParam String stockId) {
    	log.debug("removeTarget[stockId:{}]",stockId);
		Map result = new HashMap();
		result.put("stockId", stockId);
		try {
			setUser(userName);
			result.put("status", "0000");
			result.put("isOK", crud.process("tracked", "remove", stockId));
		} catch (Exception e) {
			log.error("deleteTarget fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gson.toJson(result);
	}
    
    @ResponseBody
	@RequestMapping(value = "output", method={RequestMethod.POST, RequestMethod.GET})
    public String outputDatas(@RequestParam String userName) {
		Map result = new HashMap();
		try {
			setUser(userName);
			result.put("status", "0000");
			result.put("isOK", crud.process("tracked", "output", null));
		} catch (Exception e) {
			log.error("outputDatas fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gson.toJson(result);
	}
    
    private void setUser(String userName){
    	PortfolioContext.userName = userName;
    }

}
