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
@RequestMapping("/alert")
public class StockAlertController {
	
	private Gson gson = new Gson();
	
	@Autowired
	private FlowOfCRUD crud/* = new TargetCRUD()*/;
	
    @ResponseBody
	@RequestMapping(value = "add", method={RequestMethod.POST, RequestMethod.GET})
	public String addAlert(HttpServletRequest request) {
//    	log.debug("addAlert[userName:{}, stockId:{}, type:{}, compareSymbol:{}, thresholdValue:{}, "
//    			+ "repeatTimes:{}, note:{}, ]",userName,stockId,type,compareSymbol,thresholdValue,repeatTimes,note);
		Map result = new HashMap(),params = new HashMap<>(request.getParameterMap());
		
		try {
			String userName = ((String[])params.get("userName"))[0].toString(),stockId = ((String[])params.get("stockId"))[0].toString();
			String[] restArgs = new String[params.size()-1];
			restArgs[0] = stockId;
			int i = 1;
			for(Object key : params.keySet()){
				if("userName".equals(key.toString()) || "stockId".equals(key.toString()) 
						|| "".equals(params.get(key)) || params.get(key) == null){
					continue;
				}else {
					restArgs[i] = key.toString() + ":" + ((String[])params.get(key))[0].toString();
					i++;
				}
			}
			
			setUser(userName);
			result.put("status", "0000");
			result.put("userName", userName);
			result.put("stockId", stockId);
			result.put("isOK", crud.process("alert", "add", restArgs));
		} catch (Exception e) {
			log.error("addAlert fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gson.toJson(result);
    }
    
    @ResponseBody
	@RequestMapping(value = "get", method={RequestMethod.POST, RequestMethod.GET})
    public String getAlert(@RequestParam String id){	
    	log.debug("getAlert[id:{}]",id);
		Map result = new HashMap();
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		gsonBuilder.serializeSpecialFloatingPointValues();
		result.put("id", id);
		try {
			result.put("status", "0000");
			result.put("target", crud.process("alert", "get", id));
		} catch (Exception e) {
			log.error("getAlert fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gsonBuilder.create().toJson(result);
    }
    
    @ResponseBody
	@RequestMapping(value = "list", method={RequestMethod.POST, RequestMethod.GET})
    public String listAlert(@RequestParam String userName){
    	log.debug("listAlert[userName:{}]",userName);
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
			result.put("targets", crud.process("alert", "list", null));
		} catch (Exception e) {
			log.error("listAlert fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gsonBuilder.create().toJson(result).replaceAll("NaN", "0.0");
    }
    
    @ResponseBody
	@RequestMapping(value = "listTriggered", method={RequestMethod.POST, RequestMethod.GET})
    public String listTriggeredAlert(@RequestParam String userName){
    	log.debug("listTriggeredAlert[userName:{}]",userName);
		Map result = new HashMap();
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		gsonBuilder.serializeSpecialFloatingPointValues();
		try {
			setUser(userName);
			result.put("status", "0000");
			result.put("targets", crud.process("alert", "listTriggered", null));
		} catch (Exception e) {
			log.error("listAlert fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gsonBuilder.create().toJson(result).replaceAll("NaN", "0.0");
    }
    
    @ResponseBody
	@RequestMapping(value = "update", method={RequestMethod.POST, RequestMethod.GET})
    public String updateAlert(HttpServletRequest request) { //@RequestParam String userName,@RequestParam String stockId   
    	// update要把參數變成field:param的形式輸入到args裡面
    	Map result = new HashMap(),params = new HashMap<>(request.getParameterMap());
		try {
			String userName = ((String[])params.get("userName"))[0].toString(),id = ((String[])params.get("id"))[0].toString();
			String[] restArgs = new String[params.size()-1];
			restArgs[0] = id;
			int i = 1;
			for(Object key : params.keySet()){
				if("userName".equals(key.toString()) || "id".equals(key.toString())
						|| "".equals(params.get(key)) || params.get(key) == null){
					continue;
				}else {
					restArgs[i] = key.toString() + ":" + ((String[])params.get(key))[0].toString();
					i++;
				}
			}
			
			log.debug("updateAlert[id:{}]",id);
			result.put("id", id);
			setUser(userName);
			result.put("status", "0000");
			result.put("isOK", crud.process("alert", "update", restArgs));
		} catch (Exception e) {
			log.error("updateAlert fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gson.toJson(result);
	}
    
    @ResponseBody
	@RequestMapping(value = "remove", method={RequestMethod.POST, RequestMethod.GET})
    public String removeAlert(@RequestParam String userName,@RequestParam String id) {
    	log.debug("removeAlert[id:{}]",id);
		Map result = new HashMap();
		result.put("id", id);
		try {
			setUser(userName);
			result.put("status", "0000");
			result.put("isOK", crud.process("alert", "remove", id));
		} catch (Exception e) {
			log.error("removeAlert fail, exception => {}", e.toString());
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
			result.put("isOK", crud.process("alert", "output", null));
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
