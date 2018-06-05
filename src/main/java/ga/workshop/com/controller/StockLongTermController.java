package ga.workshop.com.controller;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ga.workshop.com.crud.FlowOfCRUD;
import ga.workshop.com.model.Target;
import ga.workshop.com.model.TargetWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/lts")
public class StockLongTermController {
	
	private Gson gson = new Gson();
	
	@Autowired
	private FlowOfCRUD crud/* = new TargetCRUD()*/;
	
    @ResponseBody
	@RequestMapping(value = "create", method={RequestMethod.POST, RequestMethod.GET})
	public String createTarget(@RequestParam String stockId) {
    	log.debug("createTarget[stockId:{}]",stockId);
		Map result = new HashMap();
		result.put("stockId", stockId);
		try {
			result.put("status", "0000");
			result.put("isOK", crud.process("target", "create", stockId));
		} catch (Exception e) {
			log.error("createTarget fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gson.toJson(result);
    }
    
    @ResponseBody
	@RequestMapping(value = "get", method={RequestMethod.POST, RequestMethod.GET})
    public String getTarget(@RequestParam String stockId){	
    	log.debug("getTarget[stockId:{}]",stockId);
		Map result = new HashMap();
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		gsonBuilder.serializeSpecialFloatingPointValues();
		result.put("stockId", stockId);
		try {
			result.put("status", "0000");
			result.put("target", new TargetWrapper((Target)crud.process("target", "get", stockId)));
		} catch (Exception e) {
			log.error("getTarget fail, exception => {}", e.toString());
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
			result.put("targets", crud.process("target", "maxPages", null));
		} catch (Exception e) {
			log.error("maxPages fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gson.toJson(result);
    }
    
    @ResponseBody
	@RequestMapping(value = "list", method={RequestMethod.POST, RequestMethod.GET})
    public String listTargets(@RequestParam(defaultValue="1") int page){
//    	log.debug("getTarget[proxyName:{}]",proxyName);
		Map result = new HashMap();
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		gsonBuilder.serializeSpecialFloatingPointValues();
		try {
			List<TargetWrapper> wrappers = new LinkedList<>();
//			List<Target> targets = (List<Target>)crud.process("target", "list", page + "");
			for(Target target : (List<Target>)crud.process("target", "list", page + "")){
				wrappers.add(new TargetWrapper(target));
			}
			result.put("status", "0000");
			result.put("targets", wrappers);
		} catch (Exception e) {
			log.error("listTargets fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gsonBuilder.create().toJson(result).replaceAll("NaN", "0.0");
    }
    
    @ResponseBody
	@RequestMapping(value = "update", method={RequestMethod.POST, RequestMethod.GET})
    public String updateTarget(@RequestParam String stockId) {
    	log.debug("updateTarget[stockId:{}]",stockId);
		Map result = new HashMap();
		result.put("stockId", stockId);
		try {
			result.put("status", "0000");
			result.put("isOK", crud.process("target", "update", stockId));
		} catch (Exception e) {
			log.error("updateTarget fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gson.toJson(result);
	}
    
    @ResponseBody
	@RequestMapping(value = "delete", method={RequestMethod.POST, RequestMethod.GET})
    public String deleteTarget(@RequestParam String stockId) {
    	log.debug("deleteTarget[stockId:{}]",stockId);
		Map result = new HashMap();
		result.put("stockId", stockId);
		try {
			result.put("status", "0000");
			result.put("isOK", crud.process("target", "delete", stockId));
		} catch (Exception e) {
			log.error("deleteTarget fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gson.toJson(result);
	}
    
    @ResponseBody
	@RequestMapping(value = "output", method={RequestMethod.POST, RequestMethod.GET})
    public String outputDatas() {
		Map result = new HashMap();
		try {
			result.put("status", "0000");
			result.put("isOK", crud.process("target", "output", null));
		} catch (Exception e) {
			log.error("outputDatas fail, exception => {}", e.toString());
			result.put("status", "9999");
		}
    	return gson.toJson(result);
	}

}
